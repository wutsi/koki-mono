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
                count++
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
                }
            });
        console.log(count + ' ajax-fragment component(s) found');
    }
}

/**
 * Dropzone widget
 */
class DropzoneWidget {
    init() {
        let count = 0;
        document.querySelectorAll('[data-component-id=dropzone]')
            .forEach((elt) => {
                let id = elt.getAttribute('id');
                console.log('...dropzone #' + id + ' found');
                if (id) {
                    let dz = new Dropzone("#" + id, {
                        maxFiles: 10,
                        maxFilesize: 10,
                        createImageThumbnails: false,
                        url: elt.getAttribute('data-upload-url'),
                        dictDefaultMessage: "Drag and drop files you want to upload here, or click to select files",

                        addedfile: file => {
                            console.log("addedfile()", file);
                        },

                        complete: file => {
                            console.log("complete()", file);
                            if (dz.getUploadingFiles().length === 0 && dz.getQueuedFiles().length === 0) {
                                let targetId = elt.getAttribute('data-target-id');
                                kokiFiles.refresh(targetId);
                            }
                        }
                    });

                    count++
                }
            });
        console.log(count + ' dropzone component(s) found');
    }
}

/**
 * Widget container
 */
class KokiWidgets {
    constructor() {
        this.loadMore = new LoadMoreWidget();
        this.ajaxFragment = new AjaxFragmentWidget();
        this.dropzone = new DropzoneWidget();
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
        this.widgets.dropzone.init();
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

