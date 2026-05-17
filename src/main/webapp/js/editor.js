var KEY_W       = 62; // largeur du clavier à gauche
var HEADER_H    = 28; // hauteur de la règle des temps en haut
var CELL_W      = 40; // largeur colonne (d'1 temps du coup)
var CELL_H      = 14; // hauteur de ligne (= 1 demi-ton)
var PITCH_MIN   = 24; // note la plus basse, C1 
var PITCH_MAX   = 107; // note la plus haute, B7 
var PITCHES     = PITCH_MAX - PITCH_MIN + 1; // nb de notes

var NOTE_NAMES      = ['C','C#','D','D#','E','F','F#','G','G#','A','A#','B'];
var BLACK_SEMITONES = [1, 3, 6, 8, 10];

var tracks           = []; // alimenté ddepuis le json
var activeTrackIndex = 0; // la track choisie
var scrollX          = 0; // val par défaut du scroll horizontal
var scrollY          = 0; // idem vertical
var totalBeats       = 32; // nb total de temps chargés par défaut
var beatsPerBar      = 4; // nb de temps par mesure
var currentTool      = 'draw'; // outil sélectionné par défaut

var RESIZE_HANDLE_W = 6; // largeur (px) de la zone de redimensionnement sur le bord droit d'une note

// état du redimensionnement en cours
var resizing    = false;
var resizeNote  = null;

// player
var playerMode        = 'track'; // 'track' | 'all'
var playerPlaying     = false;
var playerBeat        = 0;       // position courante en temps (float)
var playerBeatAtStart = 0;       // position au moment du dernier play
var playerAudioCtx    = null;
var playerAudioStart  = 0;       // audioCtx.currentTime au moment du play
var playerSources     = [];      // oscillateurs schedulés
var playerRAF         = null;

var canvas, ctx; // pour dessiner...
var scrollXInput, scrollYInput;
var tabsContainer, cursorInfo;

var ws; // websocket de l'éditeur

function computeTotalBeats() {
    var max, i, j, t, n;
    max = 32;
    for (i = 0; i < tracks.length; i++) {
        t = tracks[i];
        for (j = 0; j < t.notes.length; j++) {
            n   = t.notes[j];
            max = Math.max(max, n.startBeat + n.duration + beatsPerBar);
        }
    }
    return max;
}

// démarrage
function initEditor(tracksData, bpb) {
    tracks      = tracksData || [];
    beatsPerBar = bpb || 4;
    totalBeats  = computeTotalBeats();

    // récup des éléments html
    canvas        = document.getElementById('midi-canvas');
    ctx           = canvas.getContext('2d');
    scrollXInput  = document.getElementById('scroll-x');
    scrollYInput  = document.getElementById('scroll-y');
    tabsContainer = document.getElementById('track-tabs');
    cursorInfo    = document.getElementById('cursor-info');

    resizeCanvas();

    // on centre verticalement sur C4
    var c4Row = PITCH_MAX - 60;
    scrollY = Math.max(0, c4Row * CELL_H - Math.floor((canvas.height - HEADER_H) / 2));

    initWebSocket();

    // création des éléments puis rendu
    buildTrackTabs();
    updateScrollbarLimits();
    syncScrollbars();
    setupEvents();
    initPlayer();
    render();
}

// initialisation de la ws
function initWebSocket() {
    ws = new WebSocket("ws://" + window.location.host + COMPOSITION_DATA.contextPath + '/editeur/' + COMPOSITION_DATA.id);

    ws.onopen = function() {
        console.log("WS connected for composition #" + COMPOSITION_DATA.id);
    };

    ws.onmessage = function(event) {
        var response = JSON.parse(event.data);
        if (!response.success) return;

        // une note a été ajt
        if (response.action === 'NOTE_ADDED') {
            var targetTrack = tracks.find(function(t) { return t.id === response.trackId; });
            if (targetTrack) {
                // check pour éviter les doublons (au moins côté client)
                var exists = targetTrack.notes.some(function(n) { return n.id === response.id; });
                if (!exists) {
                    targetTrack.notes.push({
                        id: response.id,
                        pitch: response.pitch,
                        startBeat: response.startBeat,
                        duration: response.duration,
                        velocity: response.velocity
                    });
                    totalBeats = computeTotalBeats();
                    updateScrollbarLimits();
                    render();
                }
            }
        }
        // une note a été redimensionnée
        else if (response.action === 'NOTE_RESIZED') {
            tracks.forEach(function(track) {
                var note = track.notes.find(function(n) { return n.id === response.noteId; });
                if (note) {
                    note.duration = response.duration;
                    totalBeats = computeTotalBeats();
                    updateScrollbarLimits();
                    render();
                }
            });
        }
        // une note a été supprimée
        else if (response.action === 'NOTE_DELETED') {
            tracks.forEach(function(track) {
                var index = track.notes.findIndex(function(n) { return n.id === response.noteId; });
                if (index >= 0) {
                    track.notes.splice(index, 1);
                    totalBeats = computeTotalBeats();
                    updateScrollbarLimits();
                    render();
                }
            });
        }
        // une piste a été ajt
        else if (response.action === 'TRACK_CREATED') {
            tracks.push(response.track);
            buildTrackTabs();
            render();
        }
    };

    ws.onerror = function(error) {
        console.error("Erreur communication sur l'éditeur : ", error);
    };

    ws.onclose = function() {
        console.log("Connexion à l'éditeur perdue.");
    };
}

// gestion du canvas
function resizeCanvas() {
    canvas.width = canvas.parentElement.clientWidth;
}

// onglet piste
function buildTrackTabs() {
    var i, btn, addBtn;
    tabsContainer.innerHTML = '';

    for (i = 0; i < tracks.length; i++) {
        btn = document.createElement('button');
        btn.className = 'track-tab' + (i === activeTrackIndex ? ' active' : '');
        btn.style.borderBottomColor = tracks[i].color || '#4a9eff';
        btn.textContent = tracks[i].name || ('Piste ' + (i + 1));
        btn.setAttribute('data-index', i);
        btn.addEventListener('click', onTabClick);
        tabsContainer.appendChild(btn);
    }

    addBtn = document.createElement('button');
    addBtn.className = 'track-tab track-tab-add';
    addBtn.textContent = '+ Piste';
    addBtn.addEventListener('click', onAddTrackClick);
    tabsContainer.appendChild(addBtn);
}

function onTabClick(e) {
    activeTrackIndex = parseInt(e.currentTarget.getAttribute('data-index'));
    buildTrackTabs();
    render();
}

// gestion des scrollbars
function updateScrollbarLimits() {
    var maxX = Math.max(0, totalBeats * CELL_W - (canvas.width - KEY_W));
    var maxY = Math.max(0, PITCHES * CELL_H - (canvas.height - HEADER_H));
    scrollXInput.max = maxX;
    scrollYInput.max = maxY;
}

function syncScrollbars() {
    scrollXInput.value = scrollX;
    scrollYInput.value = scrollY;
}

// événéments et helpers
function setupEvents() {
    canvas.addEventListener('mousedown', onMouseDown);
    canvas.addEventListener('mousemove', onMouseMove);
    canvas.addEventListener('mouseleave', onMouseLeave);
    canvas.addEventListener('wheel', onWheel);
    canvas.addEventListener('contextmenu', onContextMenu);

    scrollXInput.addEventListener('input', onScrollXChange);
    scrollYInput.addEventListener('input', onScrollYChange);

    window.addEventListener('resize', onWindowResize);

    document.getElementById('tool-draw').addEventListener('click', onToolDraw);
    document.getElementById('tool-erase').addEventListener('click', onToolErase);
    document.getElementById('btn-create-track').addEventListener('click', onCreateTrack);
}

function onContextMenu(e) {
    e.preventDefault();
}

function onScrollXChange() {
    scrollX = parseInt(scrollXInput.value);
    render();
}

function onScrollYChange() {
    scrollY = parseInt(scrollYInput.value);
    render();
}

function onWindowResize() {
    resizeCanvas();
    updateScrollbarLimits();
    render();
}

function onToolDraw() {
    currentTool = 'draw';
    document.getElementById('tool-draw').classList.add('active');
    document.getElementById('tool-erase').classList.remove('active');
}

function onToolErase() {
    currentTool = 'erase';
    document.getElementById('tool-erase').classList.add('active');
    document.getElementById('tool-draw').classList.remove('active');
}

function onWheel(e) {
    e.preventDefault();
    var maxX = Math.max(0, totalBeats * CELL_W - (canvas.width - KEY_W));
    var maxY = Math.max(0, PITCHES * CELL_H - (canvas.height - HEADER_H));

    if (e.shiftKey) {
        scrollX = Math.max(0, Math.min(maxX, scrollX + e.deltaY));
    } else {
        scrollY = Math.max(0, Math.min(maxY, scrollY + e.deltaY));
    }
    syncScrollbars();
    render();
}

function getCanvasPos(e) {
    var rect = canvas.getBoundingClientRect();
    return { x: e.clientX - rect.left, y: e.clientY - rect.top };
}

function onMouseDown(e) {
    var pos, beat, pitch, track, existing, handle;

    if (!COMPOSITION_DATA.canEdit) return;
    pos = getCanvasPos(e);
    if (pos.x < KEY_W || pos.y < HEADER_H) return;
    if (tracks.length === 0) return;

    // redimensionnement : bord droit d'une note
    handle = findResizeHandle(pos);
    if (handle) {
        resizing   = true;
        resizeNote = handle;
        canvas.style.cursor = 'ew-resize';
        document.addEventListener('mousemove', onResizeMouseMove);
        document.addEventListener('mouseup', onResizeMouseUp);
        return;
    }

    beat  = Math.floor((pos.x - KEY_W + scrollX) / CELL_W);
    pitch = PITCH_MAX - Math.floor((pos.y - HEADER_H + scrollY) / CELL_H);

    if (pitch < PITCH_MIN || pitch > PITCH_MAX || beat < 0) return;

    track    = tracks[activeTrackIndex];
    existing = findNoteAt(track, beat, pitch);

    if (existing >= 0) {
        var note = track.notes[existing];
        if (note.id > 0) {
            ws.send(JSON.stringify({
                action: 'DELETE_NOTE',
                data: { noteId: note.id }
            }));
        }
    } else if (currentTool === 'draw') {
        ws.send(JSON.stringify({
            action: 'ADD_NOTE',
            data: {
                trackId: track.id,
                pitch: pitch,
                startBeat: beat,
                duration: 1,
                velocity: 100
            }
        }));
    }
}

function onMouseMove(e) {
    var pos, beat, pitch, noteName, octave, bar, beatInBar;

    pos = getCanvasPos(e);
    if (pos.x < KEY_W || pos.y < HEADER_H) {
        cursorInfo.textContent = '';
        if (!resizing) canvas.style.cursor = '';
        return;
    }

    beat  = Math.floor((pos.x - KEY_W + scrollX) / CELL_W);
    pitch = PITCH_MAX - Math.floor((pos.y - HEADER_H + scrollY) / CELL_H);

    if (pitch < PITCH_MIN || pitch > PITCH_MAX || beat < 0) {
        cursorInfo.textContent = '';
        if (!resizing) canvas.style.cursor = '';
        return;
    }

    if (!resizing && COMPOSITION_DATA.canEdit) {
        canvas.style.cursor = findResizeHandle(pos) ? 'ew-resize' : '';
    }

    noteName  = NOTE_NAMES[pitch % 12];
    octave    = Math.floor(pitch / 12) - 1;
    bar       = Math.floor(beat / beatsPerBar) + 1;
    beatInBar = (beat % beatsPerBar) + 1;

    cursorInfo.textContent = noteName + octave + '  |  Mesure ' + bar + ', Temps ' + beatInBar;
}

function onMouseLeave() {
    cursorInfo.textContent = '';
}

function findNoteAt(track, beat, pitch) {
    var i, n;
    for (i = 0; i < track.notes.length; i++) {
        n = track.notes[i];
        if (n.pitch === pitch && beat >= n.startBeat && beat < n.startBeat + n.duration) {
            return i;
        }
    }
    return -1;
}

// Renvoie la note dont le bord droit est sous le curseur, ou null
function findResizeHandle(pos) {
    var i, n, nx, ny, noteRight, noteTop, noteBottom;
    if (tracks.length === 0) return null;
    var track = tracks[activeTrackIndex];
    for (i = 0; i < track.notes.length; i++) {
        n         = track.notes[i];
        nx        = KEY_W + n.startBeat * CELL_W - scrollX;
        ny        = HEADER_H + (PITCH_MAX - n.pitch) * CELL_H - scrollY;
        noteRight = nx + n.duration * CELL_W - 2; // bord droit de la note dessinée
        noteTop   = ny + 1;
        noteBottom= ny + CELL_H - 2;
        if (pos.x >= noteRight - RESIZE_HANDLE_W && pos.x <= noteRight + 2 &&
            pos.y >= noteTop && pos.y <= noteBottom) {
            return n;
        }
    }
    return null;
}

function onResizeMouseMove(e) {
    var rect, x, rawBeat, newRightEdge;
    if (!resizing || !resizeNote) return;
    rect        = canvas.getBoundingClientRect();
    x           = e.clientX - rect.left;
    rawBeat     = (x - KEY_W + scrollX) / CELL_W;
    newRightEdge= Math.max(resizeNote.startBeat + 1, Math.round(rawBeat));
    resizeNote.duration = newRightEdge - resizeNote.startBeat;
    totalBeats  = computeTotalBeats();
    updateScrollbarLimits();
    render();
}

function onResizeMouseUp() {
    if (!resizing || !resizeNote) return;
    resizing = false;
    document.removeEventListener('mousemove', onResizeMouseMove);
    document.removeEventListener('mouseup', onResizeMouseUp);
    canvas.style.cursor = '';

    if (resizeNote.id > 0) {
        ws.send(JSON.stringify({
            action: 'RESIZE_NOTE',
            data: { noteId: resizeNote.id, duration: resizeNote.duration }
        }));
    }
    resizeNote = null;
}

// rendu principal
function render() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    drawGridBackground();

    // ZONE NOTES
    ctx.save();
    ctx.beginPath();
    ctx.rect(KEY_W, HEADER_H, canvas.width - KEY_W, canvas.height - HEADER_H);
    ctx.clip();
    drawNotes();
    ctx.restore();

    // CLAVIER
    ctx.save();
    ctx.beginPath();
    ctx.rect(0, HEADER_H, KEY_W, canvas.height - HEADER_H);
    ctx.clip();
    drawPianoKeys();
    ctx.restore();

    // REGLES DE MESURE
    ctx.save();
    ctx.beginPath();
    ctx.rect(KEY_W, 0, canvas.width - KEY_W, HEADER_H);
    ctx.clip();
    drawBeatRuler();
    ctx.restore();

    // EN HAUT A GAUCHE
    ctx.fillStyle = '#0e0e1e';
    ctx.fillRect(0, 0, KEY_W, HEADER_H);

    // MSG SI AUCUNE PISTE
    if (tracks.length === 0) {
        ctx.fillStyle = 'rgba(100,100,160,0.5)';
        ctx.font = '15px sans-serif';
        ctx.textAlign = 'center';
        ctx.fillText('Aucune piste — ajoutez-en une pour commencer', canvas.width / 2, canvas.height / 2);
        ctx.textAlign = 'left';
    }

    drawPlayhead();
}

// dessine la grille en fond
function drawGridBackground() {
    var w, h, p, y, b, x, semitone;

    w = canvas.width;
    h = canvas.height;

    ctx.fillStyle = '#1e1e2e';
    ctx.fillRect(KEY_W, HEADER_H, w - KEY_W, h - HEADER_H);

    for (p = PITCH_MIN; p <= PITCH_MAX + 1; p++) {
        y = HEADER_H + (PITCH_MAX - p + 1) * CELL_H - scrollY;
        if (y < HEADER_H || y > h) continue;

        semitone = p % 12;

        if (BLACK_SEMITONES.indexOf(semitone) >= 0) {
            ctx.fillStyle = '#191928';
            ctx.fillRect(KEY_W, y - CELL_H, w - KEY_W, CELL_H);
        }

        ctx.strokeStyle = (semitone === 0) ? '#383860' : '#242436';
        ctx.lineWidth   = (semitone === 0) ? 1 : 0.5;
        ctx.beginPath();
        ctx.moveTo(KEY_W, y);
        ctx.lineTo(w, y);
        ctx.stroke();
    }

    for (b = 0; b <= totalBeats; b++) {
        x = KEY_W + b * CELL_W - scrollX;
        if (x < KEY_W || x > w) continue;

        ctx.strokeStyle = (b % beatsPerBar === 0) ? '#3e3e62' : '#252538';
        ctx.lineWidth   = (b % beatsPerBar === 0) ? 1 : 0.5;
        ctx.beginPath();
        ctx.moveTo(x, HEADER_H);
        ctx.lineTo(x, h);
        ctx.stroke();
    }
}

// dessine le clavier
function drawPianoKeys() {
    var h, p, y, semitone, octave;

    h = canvas.height;

    for (p = PITCH_MIN; p <= PITCH_MAX; p++) {
        y = HEADER_H + (PITCH_MAX - p) * CELL_H - scrollY;
        if (y + CELL_H < HEADER_H || y > h) continue;

        semitone = p % 12;

        if (BLACK_SEMITONES.indexOf(semitone) >= 0) {
            ctx.fillStyle = '#222';
            ctx.fillRect(0, y, KEY_W * 0.62, CELL_H);
            ctx.fillStyle = '#1a1a1a';
            ctx.fillRect(0, y + CELL_H - 1, KEY_W, 1);
        } else {
            ctx.fillStyle = '#d8d8d8';
            ctx.fillRect(0, y, KEY_W - 1, CELL_H);
            ctx.fillStyle = '#aaa';
            ctx.fillRect(0, y + CELL_H - 1, KEY_W - 1, 1);

            if (semitone === 0) {
                octave = Math.floor(p / 12) - 1;
                ctx.fillStyle = '#555';
                ctx.font = '9px sans-serif';
                ctx.fillText('C' + octave, KEY_W - 22, y + CELL_H - 3);
            }
        }
    }

    ctx.fillStyle = '#3a3a5a';
    ctx.fillRect(KEY_W - 1, HEADER_H, 1, h - HEADER_H);
}

// dessine les règles
function drawBeatRuler() {
    var w, b, x;

    w = canvas.width;

    ctx.fillStyle = '#12122a';
    ctx.fillRect(KEY_W, 0, w - KEY_W, HEADER_H);

    for (b = 0; b <= totalBeats; b++) {
        x = KEY_W + b * CELL_W - scrollX;
        if (x < KEY_W || x > w) continue;

        ctx.strokeStyle = '#3a3a5a';
        ctx.lineWidth = 1;
        ctx.beginPath();
        ctx.moveTo(x, (b % beatsPerBar === 0) ? 0 : HEADER_H * 0.5);
        ctx.lineTo(x, HEADER_H);
        ctx.stroke();

        if (b % beatsPerBar === 0) {
            ctx.fillStyle = '#aaa';
            ctx.font = 'bold 11px sans-serif';
            ctx.fillText(String(b / beatsPerBar + 1), x + 4, HEADER_H - 7);
        } else if (CELL_W >= 30) {
            ctx.fillStyle = '#555';
            ctx.font = '9px sans-serif';
            ctx.fillText(String(b % beatsPerBar + 1), x + 3, HEADER_H - 6);
        }
    }

    ctx.fillStyle = '#3a3a5a';
    ctx.fillRect(KEY_W, HEADER_H - 1, w - KEY_W, 1);
}

// dessine les notes
function drawNotes() {
    var track, color, light, w, h, i, n, nx, ny, nw, nh;

    if (tracks.length === 0) return;

    track = tracks[activeTrackIndex];
    color = track.color || '#4a9eff';
    light = lightenColor(color, 50);
    w     = canvas.width;
    h     = canvas.height;

    for (i = 0; i < track.notes.length; i++) {
        n  = track.notes[i];
        nx = KEY_W + n.startBeat * CELL_W - scrollX;
        ny = HEADER_H + (PITCH_MAX - n.pitch) * CELL_H - scrollY;
        nw = n.duration * CELL_W - 2;
        nh = CELL_H - 2;

        if (nx + nw < KEY_W || nx > w || ny + nh < HEADER_H || ny > h) continue;

        // Corps de la note
        ctx.fillStyle = (resizing && resizeNote && n.id === resizeNote.id) ? lightenColor(color, 30) : color;
        ctx.fillRect(nx + 1, ny + 1, nw, nh);

        // Reflet en haut
        ctx.fillStyle = light;
        ctx.fillRect(nx + 1, ny + 1, nw, 2);

        // Poignée de redimensionnement sur le bord droit
        if (COMPOSITION_DATA.canEdit && nw > RESIZE_HANDLE_W) {
            ctx.fillStyle = 'rgba(255,255,255,0.25)';
            ctx.fillRect(nx + nw - 3, ny + 2, 3, nh - 2);
        }
    }
}

// modal d'ajout d'une piste
function onAddTrackClick() {
    var instruments, sel, i, opt;

    instruments = COMPOSITION_DATA.instruments;
    sel = document.getElementById('new-track-instrument');
    sel.innerHTML = '';
    for (i = 0; i < instruments.length; i++) {
        opt = document.createElement('option');
        opt.value = instruments[i].id;
        opt.textContent = instruments[i].name;
        sel.appendChild(opt);
    }
    document.getElementById('new-track-name').value = '';

    new bootstrap.Modal(document.getElementById('modal-new-track')).show();
}

// création d'une piste
function onCreateTrack() {
    var name, instrumentId, color;

    name         = document.getElementById('new-track-name').value.trim();
    instrumentId = parseInt(document.getElementById('new-track-instrument').value);
    color        = document.getElementById('new-track-color').value;

    if (!name) {
        document.getElementById('new-track-name').focus();
        return;
    }

    // Émission de la création de la piste via WS
    ws.send(JSON.stringify({
        action: 'CREATE_TRACK',
        data: {
            name: name,
            instrumentId: instrumentId,
            color: color
        }
    }));

    bootstrap.Modal.getInstance(document.getElementById('modal-new-track')).hide();
}

// helpers
function lightenColor(hex, amount) {
    var r, g, b;
    r = parseInt(hex.slice(1, 3), 16);
    g = parseInt(hex.slice(3, 5), 16);
    b = parseInt(hex.slice(5, 7), 16);
    r = Math.min(255, r + amount);
    g = Math.min(255, g + amount);
    b = Math.min(255, b + amount);
    return '#' + pad2(r.toString(16)) + pad2(g.toString(16)) + pad2(b.toString(16));
}

function pad2(s) {
    return s.length === 1 ? '0' + s : s;
}

// ------
// partie lecteur

function initPlayer() {
    document.getElementById('player-play').addEventListener('click', onPlayerPlay);
    document.getElementById('player-stop').addEventListener('click', onPlayerStop);
    document.getElementById('player-mode').addEventListener('click', onPlayerMode);
    playerUpdateUI();
}

function onPlayerPlay() {
    if (playerPlaying) { playerPause(); } else { playerPlay(); }
}

function onPlayerStop() {
    playerStop();
}

function onPlayerMode() {
    playerMode = (playerMode === 'track') ? 'all' : 'track';
    playerUpdateUI();
}

function playerPlay() {
    if (tracks.length === 0) return;

    if (!playerAudioCtx) {
        playerAudioCtx = new (window.AudioContext || window.webkitAudioContext)();
    }
    if (playerAudioCtx.state === 'suspended') {
        playerAudioCtx.resume();
    }

    playerPlaying     = true;
    playerBeatAtStart = playerBeat;
    playerAudioStart  = playerAudioCtx.currentTime;

    playerScheduleNotes();
    playerUpdateUI();
    playerRAF = requestAnimationFrame(playerTick);
}

function playerPause() {
    playerPlaying = false;
    if (playerRAF) { cancelAnimationFrame(playerRAF); playerRAF = null; }
    playerSources.forEach(function(s) { try { s.stop(0); } catch(e) {} });
    playerSources = [];
    playerUpdateUI();
}

function playerStop() {
    playerPause();
    playerBeat = 0;
    playerUpdateUI();
    render();
}

function playerTick() {
    var elapsed, beatsPerSec, maxBeat;

    if (!playerPlaying) return;

    beatsPerSec = COMPOSITION_DATA.tempo / 60;
    elapsed     = playerAudioCtx.currentTime - playerAudioStart;
    playerBeat  = playerBeatAtStart + elapsed * beatsPerSec;
    maxBeat     = playerComputeMaxBeat();

    if (playerBeat >= maxBeat) {
        playerBeat = 0;
        playerStop();
        return;
    }

    playerScrollToFollow();
    playerUpdateUI();
    render();
    playerRAF = requestAnimationFrame(playerTick);
}

function playerScheduleNotes() {
    var tracksToPlay, beatsPerSec, delay, dur, freq, osc, gain, t0, vel;

    tracksToPlay = (playerMode === 'all') ? tracks : [tracks[activeTrackIndex]];
    beatsPerSec  = COMPOSITION_DATA.tempo / 60;

    tracksToPlay.forEach(function(track) {
        track.notes.forEach(function(note) {
            if (note.startBeat + note.duration <= playerBeat) return;

            delay = Math.max(0, (note.startBeat - playerBeat) / beatsPerSec);
            dur   = note.duration / beatsPerSec;
            freq  = 440 * Math.pow(2, (note.pitch - 69) / 12);
            vel   = ((note.velocity || 100) / 127) * 0.25;

            osc  = playerAudioCtx.createOscillator();
            gain = playerAudioCtx.createGain();
            osc.connect(gain);
            gain.connect(playerAudioCtx.destination);

            osc.type            = 'triangle';
            osc.frequency.value = freq;

            t0 = playerAudioCtx.currentTime + delay;
            gain.gain.setValueAtTime(0, t0);
            gain.gain.linearRampToValueAtTime(vel, t0 + 0.01);
            gain.gain.setValueAtTime(vel, t0 + Math.max(0.02, dur - 0.06));
            gain.gain.linearRampToValueAtTime(0, t0 + dur);

            osc.start(t0);
            osc.stop(t0 + dur);
            playerSources.push(osc);
        });
    });
}

function playerComputeMaxBeat() {
    var max, tracksToPlay;

    max          = 0;
    tracksToPlay = (playerMode === 'all') ? tracks : [tracks[activeTrackIndex]];
    tracksToPlay.forEach(function(track) {
        track.notes.forEach(function(note) {
            max = Math.max(max, note.startBeat + note.duration);
        });
    });
    return max || totalBeats;
}

function playerScrollToFollow() {
    var px, visW, maxX;

    px   = KEY_W + playerBeat * CELL_W - scrollX;
    visW = canvas.width - KEY_W;

    if (px > canvas.width - CELL_W * 4) {
        maxX    = Math.max(0, totalBeats * CELL_W - visW);
        scrollX = Math.min(maxX, playerBeat * CELL_W - visW / 2);
        syncScrollbars();
    } else if (px < KEY_W) {
        scrollX = Math.max(0, playerBeat * CELL_W - CELL_W);
        syncScrollbars();
    }
}

function playerUpdateUI() {
    var playBtn, modeBtn, bar, beat, timeEl;

    playBtn = document.getElementById('player-play');
    modeBtn = document.getElementById('player-mode');
    timeEl  = document.getElementById('player-time');

    playBtn.innerHTML = playerPlaying
        ? '<i class="bi bi-pause-fill"></i>'
        : '<i class="bi bi-play-fill"></i>';
    playBtn.classList.toggle('active', playerPlaying);

    if (playerMode === 'all') {
        modeBtn.innerHTML = '<i class="bi bi-music-note-list"></i> Toutes les pistes';
        modeBtn.classList.add('all-mode');
    } else {
        modeBtn.innerHTML = '<i class="bi bi-music-note"></i> Piste active';
        modeBtn.classList.remove('all-mode');
    }

    bar  = Math.floor(playerBeat / beatsPerBar) + 1;
    beat = Math.floor(playerBeat % beatsPerBar) + 1;
    if (timeEl) timeEl.textContent = 'M' + bar + ' T' + beat;
}

function drawPlayhead() {
    var x;

    if (!playerPlaying && playerBeat === 0) return;

    x = KEY_W + playerBeat * CELL_W - scrollX;
    if (x < KEY_W || x > canvas.width) return;

    ctx.save();

    ctx.strokeStyle = 'rgba(255, 80, 80, 0.9)';
    ctx.lineWidth   = 1.5;
    ctx.beginPath();
    ctx.moveTo(x, HEADER_H);
    ctx.lineTo(x, canvas.height);
    ctx.stroke();

    ctx.fillStyle = '#ff5050';
    ctx.beginPath();
    ctx.moveTo(x - 5, 0);
    ctx.lineTo(x + 5, 0);
    ctx.lineTo(x, HEADER_H);
    ctx.fill();

    ctx.restore();
}