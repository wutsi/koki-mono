/**
 * Button that opens the ModalWidget
 *
 * Parameters
 *   - data-modal-body-url: URL of the body
 *   - data-modal-title (Optional): Title of the modal
 *   - data-modal-large (Optional): true | false. Large modal?
 *
 * Dependencies
 *   - ModalWidget
 */
class ModalButtonWidget {
    init(root) {
        let count = 0;
        const base = root ? root : document;
        base.querySelectorAll('[data-component-id=modal-button]')
            .forEach((elt) => {
                    count++;
                    elt.removeEventListener('click', this._on_click);
                    elt.addEventListener('click', this._on_click);
                }
            );
        console.log(count + ' modal-button component(s) found');
    }

    _on_click() {
        // console.log('_on_click', event.target);

        const elt = event.target;
        const url = elt.getAttribute('data-modal-body-url');
        const title = elt.getAttribute('data-modal-body-title');
        const large = elt.getAttribute('data-modal-body-large');

        const modal = koki.w.modal;
        if (!modal) {
            console.log('Module koki-modal.js not loaded');
        } else {
            modal.open(url, title, null, null, large);
        }
    }
}

document.addEventListener('DOMContentLoaded', function () {
        const widget = new ModalButtonWidget();
        koki.w['modalButton'] = widget;
        widget.init();
    }
);

