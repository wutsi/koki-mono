/**
 * AjaxButton
 * When this button is clicked it will refresh a fragment of the page.
 *
 * Attributes:
 *   - data-target-id: ID of the element to refresh
 *   - data-url: URL where to load the data to refresh
 */
class AjaxButtonWidget {
    init() {
        let count = 0;
        document.querySelectorAll('[data-component-id=ajax-button]')
            .forEach((elt) => {
                    elt.removeEventListener('click', this.on_click);
                    elt.addEventListener('click', this.on_click);

                    count++
                }
            );
        console.log(count + ' ajax-button component(s) found');
    }

    on_click() {
        const elt = window.event.target;
        const targetId = elt.getAttribute('data-target-id');
        const url = elt.getAttribute('data-refresh-url');
        koki.load(url, targetId);
    }
}
