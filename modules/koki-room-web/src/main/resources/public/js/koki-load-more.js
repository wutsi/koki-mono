/**
 * Button for loading additional data
 *
 * Attributes:
 * - data-container-id: Container of the content to fetch
 * - data-url: URL from where to fetch the content
 * - data-on-ready: Function to call on ready
 */
class KokiLoadMoreWidget {
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

        const containerId = elt.getAttribute('data-container-id');
        const container = document.querySelector('#' + containerId);
        container.innerHTML = '<div class="text-center"><i class="fas fa-spinner fa-spin"></div>';

        const onReady = elt.getAttribute('data-on-ready');
        const url = elt.getAttribute('data-url');
        console.log('Loading url=' + url + ", onReady=" + onReady);
        fetch(url).then(function (response) {
            response.text().then(function (html) {
                container.innerHTML = html;
                // $('#' + containerId).replaceWith(html);
                kokiLoadMore.init();

                if (onReady) {
                    eval(onReady)();
                }
            });
        });
    }
}

const kokiLoadMore = new KokiLoadMoreWidget();
document.addEventListener(
    'DOMContentLoaded',
    function () {
        kokiLoadMore.init();
    },
    false
);
