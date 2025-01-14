let fileId = 0;

function koki_files_upload() {
    console.log('Upload file');
    const container = document.getElementById('file-list');
    const ownerId = container.getAttribute("data-owner-id");
    const ownerType = container.getAttribute("data-owner-type");

    fetch('/files/upload?owner-id=' + ownerId + '&owner-type=' + ownerType)
        .then(response => {
            if (response.ok) {
                response.text()
                    .then(html => {
                        _koki_files_open_modal(html, false);
                    })
            } else {
                console.log('Unable to fetch the modal', response.text());
            }
        });
}

function koki_files_close() {
    _koki_files_close_modal();
}

function _koki_files_open_modal(html, edit) {
    document.getElementById("file-modal-body").innerHTML = html;

    const modal = new bootstrap.Modal('#file-modal');
    document.getElementById('file-modal').addEventListener('hidden.bs.modal', event => {
        if (fileId > 0) {
            _koki_files_refresh();
        }
    });
    modal.show();
}

function _koki_files_close_modal() {
    document.querySelector('#file-modal .btn-close').click();
}

function koki_files_delete(id) {
    console.log('Deleting File#' + id);
    if (confirm('Are you sure you want to delete the file?')) {
        fetch('/files/' + id + '/delete')
            .then(function () {
                _koki_files_refresh();
            });
    }
}

function _koki_files_refresh() {
    const container = document.getElementById('file-list');
    const ownerId = container.getAttribute("data-owner-id");
    const ownerType = container.getAttribute("data-owner-type");
    fetch('/files/widgets/list/more?owner-id=' + ownerId + '&owner-type=' + ownerType)
        .then(response => {
            response.text()
                .then(html => {
                    container.innerHTML = html;
                })
        });
}

function koki_files_start_upload() {
    document.getElementById("file-upload").click();
}

async function koki_files_upload_file(file, uploadUrl) {
    const containerDiv = document.getElementById("file-container");
    const data = new FormData();
    data.append('file', file);

    // Show File
    ++fileId;
    const re = /(?:\.([^.]+))?$/;
    const ext = re.exec(file.name)[1];
    containerDiv.innerHTML = containerDiv.innerHTML +
        "<DIV id='file-" + fileId + "' class='padding-small margin-top border'>" +
        "  <SPAN class='fiv-viv fiv-icon-" + ext + "'></SPAN>&nbsp;" +
        "  <SPAN>" + file.name + "</SPAN>&nbsp;" +
        "  <SPAN class='status'>Uploading... </SPAN>" +
        "</DIV>"

    const response = await fetch(uploadUrl, {
        method: 'POST',
        body: data
    });

    const statusDiv = containerDiv.querySelector("#file-" + fileId + " .status")
    if (response.ok || response.status === 0) {
        console.log("SUCCESS - Uploading " + file.name + " to " + uploadUrl);
        statusDiv.innerHTML = "<i class='fa-solid fa-check fa-xl success'></i>"
    } else {
        console.log("ERROR - Uploading " + file.name + " to " + uploadUrl, response.statusText);
        statusDiv.innerHTML = "<i class='fa-solid fa-xmark fa-xl error'></i>"
    }
}

function koki_files_update_progress(now, max) {
    const progressDiv = document.querySelector(".progress .progress-bar");
    let percent = 100 * now / max;
    progressDiv.setAttribute("aria-valuemax", max);
    progressDiv.setAttribute("aria-valuenow", now);
    progressDiv.style.width = percent + "%";
}

async function koki_files_file_selected() {
    const fileDiv = document.getElementById("file-upload");
    const uploadUrl = fileDiv.getAttribute("data-upload-url");
    console.log('upload-url', uploadUrl);

    for (i = 0; i < fileDiv.files.length; i++) {
        const file = fileDiv.files[i];
        await koki_files_upload_file(file, uploadUrl);
        koki_files_update_progress(i + 1, fileDiv.files.length);
    }
}
