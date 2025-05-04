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
                koki.widgets.loadMore.init();
            });
        });
    }
}

/**
 * Widget for loading content asynchronously
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
 * Dropzone widget
 */
class UploaderWidget {
    init() {
        let count = 0;
        document.querySelectorAll('[data-component-id=uploader]')
            .forEach((elt) => {
                let file = elt.querySelector("input[type=file]");
                if (file) {
                    elt.addEventListener('click', koki.widgets.uploader.onClick);
                    file.addEventListener('change', koki.widgets.uploader.onUploaded);

                    count++
                }
            });
        console.log(count + ' uploader component(s) found');
    }

    onClick() {
        const elt = window.event.target;
        let file = elt.querySelector("input[type=file]");
        if (file) {
            file.click()
        }
    }

    async onUploaded() {
        const elt = window.event.target;

        // Upload
        let uploadUrl = elt.getAttribute('data-upload-url');
        for (var i = 0; i < elt.files.length; i++) {
            const file = elt.files[i];
            console.log('Uploading ', file);
            const data = new FormData();
            data.append('file', file);
            const response = await fetch(uploadUrl, {
                method: 'POST',
                body: data
            });
            if (response.ok || response.status === 0) {
                console.log("SUCCESS - Uploading " + file.name + " to " + uploadUrl);
            } else {
                console.log("ERROR - Uploading " + file.name + " to " + uploadUrl, response.statusText);
            }

            // _koki_files_update_progress(i + 1, fileDiv.files.length);
        }

        let targetId = elt.getAttribute('data-target-id');
        kokiFiles.refresh(targetId);
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

