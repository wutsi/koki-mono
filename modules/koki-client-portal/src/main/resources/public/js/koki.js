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

        const me = this;
        const url = elt.getAttribute('data-url');
        console.log('Loading ' + url);
        fetch(url).then(function (response) {
            response.text().then(function (html) {
                $('#' + containerId).replaceWith(html);
                me.init();
            });
        });
    }
}

/**
 * Widget container
 */
class KokiWidgets {
    constructor() {
        this.loadMore = new LoadMoreWidget();
    }
}

/**
 * Main Class
 */
class Koki {
    constructor() {
        this.widgets = new KokiWidgets();
    }

    documentReady() {
        console.log('documentReady()');

        this.widgets.loadMore.init();
    }
}

const koki = new Koki();
document.addEventListener(
    'DOMContentLoaded',
    function () {
        koki.documentReady();
    },
    false
);

