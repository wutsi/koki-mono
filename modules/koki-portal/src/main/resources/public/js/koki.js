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
 */
class UploaderWidget {
    init() {
        let count = 0;
        document.querySelectorAll('[data-component-id=uploader]')
            .forEach((elt) => {
                let file = elt.querySelector("input[type=file]");
                if (file) {
                    elt.removeEventListener('click', koki.widgets.uploader.onClick);
                    elt.addEventListener('click', koki.widgets.uploader.onClick);

                    file.removeEventListener('change', koki.widgets.uploader.onUploaded);
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
        }

        // Refresh
        koki.widgets.ajaxButton.refresh(elt);
    }
}

/**
 * Uploader widget
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
        const href = elt.getAttribute('data-href');
        console.log('data-href=' + href);
        if (href) {
            fetch(href)
                .then(response => {
                    response.text()
                        .then(html => {
                            koki.widgets.ajaxButton.refresh(elt);
                        })
                });

        } else {
            koki.widgets.ajaxButton.refresh(elt)
        }
    }

    refresh(elt) {
        const targetId = elt.getAttribute('data-target-id');
        const refreshUrl = elt.getAttribute('data-refresh-url');
        if (targetId && refreshUrl) {
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
            }
        }
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

    open(url, title, open_callback) {
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

/**
 * Widget container
 */
class KokiWidgets {
    constructor() {
        this.loadMore = new LoadMoreWidget();
        this.ajaxFragment = new AjaxFragmentWidget();
        this.uploader = new UploaderWidget();
        this.ajaxButton = new AjaxButtonWidget();
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

