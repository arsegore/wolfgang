var KEY_W       = 62;
var HEADER_H    = 28;
var CELL_W      = 40;
var CELL_H      = 14;
var PITCH_MIN   = 24;
var PITCH_MAX   = 107;
var PITCHES     = PITCH_MAX - PITCH_MIN + 1;

var NOTE_NAMES      = ['C','C#','D','D#','E','F','F#','G','G#','A','A#','B'];
var BLACK_SEMITONES = [1, 3, 6, 8, 10];

var tracks           = [];
var activeTrackIndex = 0;
var scrollX          = 0;
var scrollY          = 0;
var totalBeats       = 32;
var beatsPerBar      = 4;
var currentTool      = 'draw';

var canvas, ctx;
var scrollXInput, scrollYInput;
var tabsContainer, cursorInfo;

// Instance globale de la WebSocket MIDI
var midiSocket;

// démarrage
function initEditor(tracksData, bpb) {
    tracks      = tracksData || [];
    beatsPerBar = bpb || 4;

    canvas        = document.getElementById('midi-canvas');
    ctx           = canvas.getContext('2d');
    scrollXInput  = document.getElementById('scroll-x');
    scrollYInput  = document.getElementById('scroll-y');
    tabsContainer = document.getElementById('track-tabs');
    cursorInfo    = document.getElementById('cursor-info');

    resizeCanvas();

    // on centre sur C4
    var c4Row = PITCH_MAX - 60;
    scrollY = Math.max(0, c4Row * CELL_H - Math.floor((canvas.height - HEADER_H) / 2));

    // --- INITIALISATION DE LA WEBSOCKET FULL DUPLEX ---
    initMidiWebSocket();

    buildTrackTabs();
    updateScrollbarLimits();
    syncScrollbars();
    setupEvents();
    render();
}

// Initialisation de la connexion temps réel
function initMidiWebSocket() {
    // On cible le Endpoint configuré côté Java (en minuscules pour pallier la casse)
    midiSocket = new WebSocket("ws://" + window.location.host + COMPOSITION_DATA.contextPath + '/editeur/' + COMPOSITION_DATA.id);

    midiSocket.onopen = function() {
        console.log("Éditeur connecté en temps réel sur la partition #" + COMPOSITION_DATA.id);
    };

    midiSocket.onmessage = function(event) {
        var response = JSON.parse(event.data);
        if (!response.success) return;

        // Traitement des notifications de broadcast du serveur
        if (response.action === 'NOTE_ADDED') {
            // On cherche la piste concernée
            var targetTrack = tracks.find(function(t) { return t.id === response.trackId; });
            if (targetTrack) {
                // On évite les doublons graphiques
                var exists = targetTrack.notes.some(function(n) { return n.id === response.id; });
                if (!exists) {
                    targetTrack.notes.push({
                        id: response.id,
                        pitch: response.pitch,
                        startBeat: response.startBeat,
                        duration: response.duration,
                        velocity: response.velocity
                    });
                    if (response.startBeat + 1 > totalBeats) {
                        totalBeats = response.startBeat + 1 + beatsPerBar;
                        updateScrollbarLimits();
                    }
                    render();
                }
            }
        }
        else if (response.action === 'NOTE_DELETED') {
            // On cherche la note dans toutes les pistes pour la supprimer
            tracks.forEach(function(track) {
                var index = track.notes.findIndex(function(n) { return n.id === response.noteId; });
                if (index >= 0) {
                    track.notes.splice(index, 1);
                    render();
                }
            });
        }
        else if (response.action === 'TRACK_CREATED') {
            // Une nouvelle piste a été ajoutée par nous ou un collaborateur
            tracks.push(response.track);
            buildTrackTabs();
            render();
        }
    };

    midiSocket.onerror = function(error) {
        console.error("Erreur WebSocket Éditeur : ", error);
    };

    midiSocket.onclose = function() {
        console.log("Connexion de l'éditeur perdue.");
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
    var pos, beat, pitch, track, existing;

    pos = getCanvasPos(e);
    if (pos.x < KEY_W || pos.y < HEADER_H) return;
    if (tracks.length === 0) return;

    beat  = Math.floor((pos.x - KEY_W + scrollX) / CELL_W);
    pitch = PITCH_MAX - Math.floor((pos.y - HEADER_H + scrollY) / CELL_H);

    if (pitch < PITCH_MIN || pitch > PITCH_MAX || beat < 0) return;

    track    = tracks[activeTrackIndex];
    existing = findNoteAt(track, beat, pitch);

    // Changement d'approche ici : plus de manipulation locale directe du tableau, on délègue à la WebSocket
    if (existing >= 0) {
        var note = track.notes[existing];
        if (note.id > 0) {
            // Émission de la suppression via WS
            midiSocket.send(JSON.stringify({
                action: 'DELETE_NOTE',
                data: { noteId: note.id }
            }));
        }
    } else if (currentTool === 'draw') {
        // Émission de l'ajout via WS
        midiSocket.send(JSON.stringify({
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
        return;
    }

    beat  = Math.floor((pos.x - KEY_W + scrollX) / CELL_W);
    pitch = PITCH_MAX - Math.floor((pos.y - HEADER_H + scrollY) / CELL_H);

    if (pitch < PITCH_MIN || pitch > PITCH_MAX || beat < 0) {
        cursorInfo.textContent = '';
        return;
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

        ctx.fillStyle = color;
        ctx.fillRect(nx + 1, ny + 1, nw, nh);

        ctx.fillStyle = light;
        ctx.fillRect(nx + 1, ny + 1, nw, 2);
    }
}

// Boîte de dialogue pour ajouter une piste
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

// Soumission de la création d'une piste
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
    midiSocket.send(JSON.stringify({
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

// (Les anciennes fonctions saveNote et deleteNote basées sur XHR ont été nettoyées)

function pad2(s) {
    return s.length === 1 ? '0' + s : s;
}