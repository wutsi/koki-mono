/**
 * Uploader widget
 *
 * Attributes
 * - data-upload-button-id: ID of the button to click for selecting the files
 * - data-upload-button-id: ID of the Upload button
 * - data-file-button-id: ID of the file button
 * - data-progress-id: ID of the progress bar
 * - data-upload-url: URL where to submit the files uploaded
 * - data-max-file-size: Max size the the files to upload. Default=5Mb
 * - data-refresh-url: URL to invoke to refresh the view after the upload is completed
 * - data-target-id: ID of the element to refresh after the upload is completed
 */
class UploaderWidget {
    init() {
        let count = 0;
        document.querySelectorAll('[data-component-id=uploader]')
            .forEach((elt) => {
                const fileId = elt.getAttribute("data-file-button-id");
                const uploadId = elt.getAttribute("data-upload-button-id");

                if (fileId) {
                    const file = document.getElementById(fileId)
                    if (file) {
                        file.removeEventListener('change', this.on_uploaded);
                        file.addEventListener('change', this.on_uploaded);
                    }
                }
                if (uploadId) {
                    const upload = document.getElementById(uploadId);
                    if (upload) {
                        upload.removeEventListener('click', this.on_click);
                        upload.addEventListener('click', this.on_click);
                    }
                }

                count++
            });
        console.log(count + ' uploader component(s) found');
    }

    on_click() {
        const elt = window.event.target;
        const uploader = elt.closest('[data-component-id=uploader]');
        const fileId = uploader.getAttribute("data-file-button-id");
        if (fileId) {
            let file = document.getElementById(fileId);
            if (file) {
                file.click()
            }
        } else {
            console.error('No file input <#' + fileId + '>');
        }
    }

    async on_uploaded() {
        const elt = window.event.target;

        // Init progress bar
        const uploader = elt.closest('[data-component-id=uploader]');
        const progressId = uploader.getAttribute("data-progress-id");
        const progressBar = progressId ? document.querySelector('#' + progressId + ' .progress-bar') : null;
        if (progressBar) {
            progressBar.parentElement.style.display = 'flex';
            progressBar.setAttribute("aria-valuenow", 0);
            progressBar.setAttribute("aria-valuemax", elt.files.length);
            progressBar.style.width = "0%";
        } else {
            console.log('No progress bar' + (progressId ? ' <#' + progressId + ' .progress-bar>' : ''))
        }

        // Upload
        let uploadUrl = uploader.getAttribute('data-upload-url');
        let maxMb = uploader.getAttribute('data-max-file-size');
        if (!maxMb || maxMb.length === 0) {
            maxMb = 5;
        }
        for (var i = 0; i < elt.files.length; i++) {
            // Uploading...
            const file = elt.files[i];
            if (file.size <= maxMb * 1024 * 1024) {
                console.log('Uploading ', file);
                const data = new FormData();
                data.append('file', file);
                const response = await fetch(uploadUrl, {
                    method: 'POST',
                    body: data
                });
                if (response.ok || response.status === 0) {
                    //console.log("SUCCESS - Uploading " + file.name);
                } else {
                    console.error("FAILED - Uploading " + file.name);
                }
            } else {
                console.error("FAILED - " + file.name + " is too big. size=" + (file.size / (1024 * 1024)) + "Mb - max size=" + maxMb + "Mb");
            }

            // Progress
            let now = i + 1
            let percent = 100 * now / elt.files.length;
            if (progressBar) {
                //console.log('Updating the progress bar. now=' + now + ' - percent=' + percent);
                progressBar.setAttribute("aria-valuenow", now);
                progressBar.style.width = percent + "%";
            }
        }

        setTimeout(
            function () {
                // Refresh
                if (progressBar) {
                    progressBar.parentElement.style.display = 'none';
                }
                const targetId = uploader.getAttribute('data-target-id');
                const refreshUrl = uploader.getAttribute('data-refresh-url');
                koki.load(refreshUrl, targetId);
            },
            2000
        );
    }
}

document.addEventListener('DOMContentLoaded', function () {
        const widget = new UploaderWidget();
        koki.w['uploader'] = widget;
        widget.init();
    }
);
