/**
 * Button for loading additional data
 *
 * Parameters
 *  - data-container-id: ID of the element where we will load the data
 *  - data-url: URL where to load the additionnal information
 */
class LoadMoreWidget {
    init(root) {
        let count = 0;
        const base = root ? root : document;
        base.querySelectorAll('[data-component-id=load-more]')
            .forEach((elt) => {
                count++
                elt.removeEventListener('click', this.on_click);
                elt.addEventListener('click', this.on_click);
            });
        console.log(count + ' load-more component(s) found');
    }

    on_click() {
        const elt = window.event.target;
        const containerId = elt.getAttribute('data-container-id');
        const url = elt.getAttribute('data-url');
        if (url) {
            koki.replaceWith(url, containerId);
        }
    }
}

document.addEventListener('DOMContentLoaded', function () {
        const widget = new LoadMoreWidget();
        koki.w['loadMore'] = widget;
        widget.init();
    }
);
