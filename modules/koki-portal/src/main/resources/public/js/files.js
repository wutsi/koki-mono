var __koki_file_id = 0;

function koki_files_delete(id) {
    console.log('Deleting File#' + id);
    if (confirm('Are you sure you want to delete the file?')) {
        fetch('/files/' + id + '/delete')
            .then(function () {
                _koki_files_refresh();
            });
    }
}

function _koki_files_open_modal() {
    console.log('_koki_files_open_modal');

    document.getElementById("btn-file-upload").addEventListener('click', _koki_files_on_upload);
    document.getElementById("btn-file-close").addEventListener('click', _koki_files_close_modal);
    document.getElementById("file-upload").addEventListener('change', _koki_files_on_selected);
}

function _koki_files_close_modal() {
    console.log('_koki_files_close_modal');

    document.getElementById("btn-file-upload").removeEventListener('click', _koki_files_on_upload);
    document.getElementById("btn-file-close").removeEventListener('click', _koki_files_close_modal);
    document.getElementById("file-upload").removeEventListener('change', _koki_files_on_selected);

    const files = document.querySelector('.file-entry');
    if (files) {
        _koki_files_refresh();
    }
}

function _koki_files_refresh() {
    console.log('_koki_files_refresh');

    const ownerId = container.getAttribute("data-owner-id");
    const ownerType = container.getAttribute("data-owner-type");
    const url = '/files/tab/more?owner-id=' + ownerId + '&owner-type=' + ownerType;
    koki.load(url, 'file-list');
    // fetch('/files/tab/more?owner-id=' + ownerId + '&owner-type=' + ownerType)
    //     .then(response => {
    //         response.text()
    //             .then(html => {
    //                 container.innerHTML = html;
    //             })
    //     });
}

function _koki_files_on_upload() {
    document.getElementById("file-upload").click();
}

async function _koki_files_on_selected() {
    const fileDiv = document.getElementById("file-upload");
    const uploadUrl = fileDiv.getAttribute("data-upload-url");
    console.log('upload-url', uploadUrl);

    for (i = 0; i < fileDiv.files.length; i++) {
        const file = fileDiv.files[i];
        await _koki_files_upload_file(file, uploadUrl);
        _koki_files_update_progress(i + 1, fileDiv.files.length);
    }
}

async function _koki_files_upload_file(file, uploadUrl) {
    const containerDiv = document.getElementById("file-container");
    const data = new FormData();
    data.append('file', file);

    // Show File
    ++__koki_file_id;
    const re = /(?:\.([^.]+))?$/;
    const ext = re.exec(file.name)[1];
    containerDiv.innerHTML = containerDiv.innerHTML +
        "<DIV id='file-" + __koki_file_id + "' class='padding-small margin-top border file-entry'>" +
        "  <SPAN class='fiv-viv fiv-icon-" + ext + "'></SPAN>&nbsp;" +
        "  <SPAN>" + file.name + "</SPAN>&nbsp;" +
        "  <SPAN class='status'>Uploading... </SPAN>" +
        "</DIV>"

    const response = await fetch(uploadUrl, {
        method: 'POST',
        body: data
    });

    const statusDiv = containerDiv.querySelector("#file-" + __koki_file_id + " .status")
    if (response.ok || response.status === 0) {
        console.log("SUCCESS - Uploading " + file.name + " to " + uploadUrl);
        statusDiv.innerHTML = "<i class='fa-solid fa-check fa-xl success'></i>"
    } else {
        console.log("ERROR - Uploading " + file.name + " to " + uploadUrl, response.statusText);
        statusDiv.innerHTML = "<i class='fa-solid fa-xmark fa-xl error'></i>"
    }
}

function _koki_files_update_progress(now, max) {
    const progressDiv = document.querySelector(".progress .progress-bar");
    let percent = 100 * now / max;
    progressDiv.setAttribute("aria-valuemax", max);
    progressDiv.setAttribute("aria-valuenow", now);
    progressDiv.style.width = percent + "%";
}
