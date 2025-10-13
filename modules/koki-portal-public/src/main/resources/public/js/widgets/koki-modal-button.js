/**
 * Button that opens the ModalWidget
 *
 * Parameters
 *   - data-modal-body-url: URL of the body
 *   - data-modal-title (Optional): Title of the modal
 *   - data-modal-body-large (Optional): true | false. Large modal?
 *   - data-open-callback (Optional):Callback to invoke after opening the modal
 *   - data-close-callback (Optional):Callback to invoke after closing the modal
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
        console.log('_on_click', event.target);

        const elt = event.target.closest('[data-component-id=modal-button]');
        const url = elt.getAttribute('data-modal-body-url');
        const title = elt.getAttribute('data-modal-title');
        const large = elt.getAttribute('data-modal-body-large');
        const openCallback = elt.getAttribute('data-open-callback');
        const closeCallback = elt.getAttribute('data-close-callback');

        koki.w.modal.open(
            url,
            title,
            function () {
                if (openCallback) {
                    eval(openCallback)()
                }
            },
            function () {
                if (closeCallback) {
                    eval(closeCallback)()
                }
            },
            large);
    }
}

document.addEventListener('DOMContentLoaded', function () {
        const widget = new ModalButtonWidget();
        koki.w['modalButton'] = widget;
        widget.init();
    }
);

