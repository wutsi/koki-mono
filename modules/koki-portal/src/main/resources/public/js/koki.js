/**
 * Button for loading additional data
 */
class LoadMoreWidget {
    init() {
        let count = 0;
        document.querySelectorAll('[data-component-id=load-more]')
            .forEach((elt) => {
                count++
                elt.removeEventListener('click', this.onClick);
                elt.addEventListener('click', this.onClick);
            });
        console.log(count + ' load-more component(s) found');
    }

    onClick() {
        console.log('onLoadMore()');

        const elt = window.event.target;
        console.log('elt', elt);

        const containerId = elt.getAttribute('data-container-id');
        const container = document.querySelector('#' + containerId);
        container.innerHTML = '<div class="text-center"><i class="fas fa-spinner fa-spin"></div>';

        const url = elt.getAttribute('data-url');
        console.log('Loading ' + url);
        fetch(url).then(function (response) {
            response.text().then(function (html) {
                $('#' + containerId).replaceWith(html);
                koki.init();
            });
        });
    }
}

/**
 * Widget for loading content asynchronously
 *
 * Attributes
 *  - data-url: URL of the fragment to load
 */
class AjaxFragmentWidget {
    init() {
        let count = 0;
        document.querySelectorAll('[data-component-id=ajax-fragment]')
            .forEach((elt) => {
                let url = elt.getAttribute('data-url');
                if (url) {
                    fetch(url).then(function (response) {
                        response.text().then(function (html) {
                            elt.innerHTML = html;

                            let callback = elt.getAttribute('data-callback');
                            if (callback) {
                                eval(callback + "()");
                            }
                        });
                    });

                    count++
                }
            });
        console.log(count + ' ajax-fragment component(s) found');
    }
}

/**
 * Uploader widget
 *
 * Attributes
 * - data-upload-button-id: ID of the button to click for selecting the files
 */
class UploaderWidget {
    init() {
        let count = 0;
        document.querySelectorAll('[data-component-id=uploader]')
            .forEach((elt) => {
                const file = elt.querySelector("input[type=file]");
                if (file) {
                    file.removeEventListener('change', koki.widgets.uploader.onUploaded);
                    file.addEventListener('change', koki.widgets.uploader.onUploaded);

                    const btnId = elt.getAttribute("data-upload-button-id");
                    if (btnId) {
                        const btn = document.getElementById(btnId);
                        if (btn) {
                            btn.removeEventListener('click', koki.widgets.uploader.onClick);
                            btn.addEventListener('click', koki.widgets.uploader.onClick);
                        }
                    } else {
                        console.log('No upload button' + (btnId ? ' <#' + fileId + '>' : ''))
                    }

                    count++
                }
            });
        console.log(count + ' uploader component(s) found');
    }

    onClick() {
        const elt = window.event.target;
        const fileId = elt.getAttribute("data-file-button-id");
        if (fileId) {
            let file = document.getElementById(fileId);
            if (file) {
                file.click()
            }
        } else {
            console.log('No file input' + (fileId ? ' <#' + fileId + '>' : ''))
        }
    }

    async onUploaded() {
        const elt = window.event.target;

        // Init progress bar
        const progressId = elt.getAttribute("data-progress-id");
        const progressBar = progressId ? document.querySelector('#' + progressId + ' .progress-bar') : null;
        if (progressBar) {
            progressBar.style.display = 'block';
            progressBar.setAttribute("aria-valuenow", 0);
            progressBar.setAttribute("aria-valuemax", elt.files.length);
            progressBar.style.width = "0%";
        } else {
            console.log('No progress bar' + (progressId ? ' <#' + progressId + ' .progress-bar>' : ''))
        }

        // Upload
        let uploadUrl = elt.getAttribute('data-upload-url');
        let maxMb = elt.getAttribute('data-max-file-size');
        if (!maxMb || maxMb.length === 0) {
            maxMb = 1
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
                    console.log("SUCCESS - Uploading " + file.name);
                } else {
                    console.log("FAILED - Uploading " + file.name);
                }
            } else {
                console.log("FAILED - " + file.name + " is too big. size=" + (file.size / (1024 * 1024)) + "Mb - max size=" + maxMb + "Mb");
            }

            // Progress
            let now = i + 1
            let percent = 100 * now / elt.files.length;
            if (progressBar) {
                console.log('Updating the progress bar. now=' + now + ' - percent=' + percent);
                progressBar.setAttribute("aria-valuenow", now);
                progressBar.style.width = percent + "%";
            }
        }

        // Refresh
        koki.widgets.ajaxButton.refresh(elt);
    }
}

/**
 * AjaxButton
 * When this button is clicked:
 *  1. It will execute an action by calling the endpoint defined by attribute "data-action-url"
 *  2. IT will refresh a fragment of the page if the attributes "data-target-id" and "data-refresh-url" are  provided
 *
 * Attributes:
 *   - data-action-confirm: Confirmation message displayed before executing the action. The action is executed if user click OK
 *   - data-action-url: URL of the action to execute. This URL must return the json:
 *      {
 *          success: true | false
 *          error: Error (if success=false)
 *      }
 *   - data-target-id: ID of the element to refresh
 *   - data-refresh-url: URL where to load the data to refresh
 */
class AjaxButtonWidget {
    init() {
        let count = 0;
        let me = this;
        document.querySelectorAll('[data-component-id=ajax-button]')
            .forEach((elt) => {
                    elt.removeEventListener('click', koki.widgets.ajaxButton.onClick);
                    elt.addEventListener('click', koki.widgets.ajaxButton.onClick);

                    count++
                }
            );
        console.log(count + ' ajax-button component(s) found');
    }

    onClick() {
        console.log('onClick()');
        const elt = window.event.target;
        const actionUrl = elt.getAttribute('data-action-url');
        if (actionUrl) {
            // Confirm
            const confirmMsg = elt.getAttribute('data-action-confirm');
            if (confirmMsg && !confirm(confirmMsg)) {
                return
            }

            // Execute
            koki.widgets.ajaxButton._execute(actionUrl, elt);
        } else {
            koki.widgets.ajaxButton.refresh(elt)
        }
    }

    _execute(actionUrl, elt) {
        console.log('Executing action ' + actionUrl);
        fetch(actionUrl)
            .then(response => {
                if (response.ok) {
                    response.json().then(json => {
                        console.log('Executed', json);
                        if (json.success) {
                            koki.widgets.ajaxButton.refresh(elt);
                        } else {
                            alert('Failed: ' + json.error);
                        }
                    });
                } else {
                    console.log('Failed to submit to ' + form.action, response.statusText);
                    alert('Failed');
                }
            });
    }

    refresh(elt) {
        const targetId = elt.getAttribute('data-target-id');
        const refreshUrl = elt.getAttribute('data-refresh-url');
        if (targetId && refreshUrl) {
            console.log('Refreshing #' + targetId + ' from ' + refreshUrl);
            const container = document.getElementById(targetId);
            if (container) {
                fetch(refreshUrl)
                    .then(response => {
                        response.text()
                            .then(html => {
                                container.innerHTML = html;
                                koki.init();
                            })
                    });
            } else {
                console.log('#' + targetId + ' not found in the DOM')
            }
        }
    }
}

/**
 * Ajax checkbox
 * When this button is clicked:
 * 1. It will execute an action by calling the endpoint defined by attribute "data-action-url"
 *
 * Attributes:
 *   - data-action-url: URL of the action to execute. The query parameter "checked" is appended to the query parameters of the URL
 *     This URL must return the json:
 *      {
 *          success: true | false
 *          error: Error (if success=false)
 *      }
 */
class AjaxCheckboxWidget {
    init() {
        let count = 0;
        let me = this;
        document.querySelectorAll('[data-component-id=ajax-checkbox]')
            .forEach((elt) => {
                    elt.removeEventListener('click', koki.widgets.ajaxCheckbox.onClick);
                    elt.addEventListener('click', koki.widgets.ajaxCheckbox.onClick);

                    count++
                }
            );
        console.log(count + ' ajax-checkbox component(s) found');
    }

    onClick() {
        console.log('onClick()');
        const elt = window.event.target;
        const actionUrl = elt.getAttribute('data-action-url');
        if (actionUrl) {
            // Confirm
            const confirmMsg = elt.getAttribute('data-action-confirm');
            if (confirmMsg && !confirm(confirmMsg)) {
                return
            }

            // Execute
            const separator = actionUrl.indexOf('?') > 0 ? '&' : '?';
            const url = actionUrl + separator + 'checked=' + elt.checked
            koki.widgets.ajaxCheckbox._execute(url);
        }
    }

    _execute(actionUrl) {
        console.log('Executing action ' + actionUrl);
        fetch(actionUrl)
            .then(response => {
                if (response.ok) {
                    response.json().then(json => {
                        console.log('Executed', json);
                        if (!json.success) {
                            alert('Failed: ' + json.error);
                        }
                    });
                } else {
                    console.log('Failed to submit to ' + form.action, response.statusText);
                    alert('Failed');
                }
            });
    }
}

/**
 * Modal widget
 */
class ModalWidget {
    init() {
        let count = 0;
        document.querySelectorAll('[data-component-id=modal]')
            .forEach((elt) => {
                    count++
                }
            );
        console.log(count + ' modal component(s) found');
    }

    open(url, title, open_callback, close_callback) {
        const modal = document.getElementById('koki-modal');
        if (modal) {
            modal.addEventListener('hidden.bs.modal', close_callback);

            fetch(url)
                .then(response => {
                    if (response.ok) {
                        response.text()
                            .then(html => {
                                // Set the body
                                document.getElementById("koki-modal-body").innerHTML = html;
                                if (open_callback) {
                                    open_callback();
                                }

                                // Set the title
                                document.getElementById("koki-modal-title").innerHTML = title;

                                // Show
                                const modal = new bootstrap.Modal('#koki-modal');
                                modal.show();
                            })
                    } else {
                        console.log('Unable to fetch the modal', response.text());
                    }
                });
        }
    }

    close() {
        document.querySelector('#koki-modal .btn-close').click();
    }
}

/**
 * Widget container
 */
class KokiWidgets {
    constructor() {
        this.loadMore = new LoadMoreWidget();
        this.ajaxFragment = new AjaxFragmentWidget();
        this.uploader = new UploaderWidget();
        this.ajaxButton = new AjaxButtonWidget();
        this.ajaxCheckbox = new AjaxCheckboxWidget();
        this.modal = new ModalWidget();
    }
}

/**
 * Main Class
 */
class Koki {
    constructor() {
        this.widgets = new KokiWidgets();
    }

    init() {
        console.log('init()');
        this.widgets.loadMore.init();
        this.widgets.ajaxFragment.init();
        this.widgets.uploader.init();
        this.widgets.ajaxButton.init();
        this.widgets.ajaxCheckbox.init();
        this.widgets.modal.init();
    }
}

const koki = new Koki();
document.addEventListener(
    'DOMContentLoaded',
    function () {
        koki.init();
    },
    false
);

