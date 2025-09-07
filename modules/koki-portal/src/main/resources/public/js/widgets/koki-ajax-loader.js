/**
 * AjaxLoader
 * When this element is clicked it will refresh a fragment of the page.
 *
 * Attributes:
 *   - data-target-id: ID of the element to refresh
 *   - data-url: URL where to load the data to refresh
 */
class AjaxLoaderWidget {
    init(root) {
        let count = 0;
        const base = root ? root : document;
        base.querySelectorAll('[data-component-id=ajax-loader]')
            .forEach((elt) => {
                    elt.removeEventListener('click', this.on_click);
                    elt.addEventListener('click', this.on_click);

                    count++
                }
            );
        console.log(count + ' ajax-loader component(s) found');
    }

    on_click() {
        const elt = window.event.target.closest('[data-component-id=ajax-loader]');

        const targetId = elt.getAttribute('data-target-id');
        const url = elt.getAttribute('data-url');
        koki.load(url, targetId);
    }
}

document.addEventListener('DOMContentLoaded', function () {
        const widget = new AjaxLoaderWidget();
        koki.w['ajaxLoader'] = widget;
        widget.init();
    }
);

