function declencherImportationCours() {
    var fileInput = document.getElementById('import-file-input');
    if (fileInput.files.length === 0) {
        alert("Veuillez sélectionner un fichier .txt");
        return;
    }

    var file = fileInput.files[0];
    var reader = new FileReader();

    reader.onload = function(e) {
        var texteBrut = e.target.result;

        fetch(COMPOSITION_DATA.contextPath + '/composition/import?id=' + COMPOSITION_DATA.id, {
            method: 'POST',
            headers: { 'Content-Type': 'text/plain;charset=UTF-8' },
            body: texteBrut
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                window.location.reload();
            } else {
                alert("Erreur lors du traitement du flux texte par le serveur.");
            }
        });
    };

    reader.readAsText(file);
}