/**
 * Button for loading additional data
 *
 * Attributes:
 * - data-container-id: Container of the content to fetch
 * - data-url: URL from where to fetch the content
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

        const url = elt.getAttribute('data-url');
        console.log('Loading ' + url);
        fetch(url).then(function (response) {
            response.text().then(function (html) {
                container.innerHTML = html;
                // $('#' + containerId).replaceWith(html);
                kokiLoadMore.init();
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
