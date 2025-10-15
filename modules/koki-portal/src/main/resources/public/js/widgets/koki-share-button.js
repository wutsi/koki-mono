/**
 * Button for sharing a page
 *
 * Parameters
 *   - data-url (Required): URL to share
 *   - data-text (Optional): Text to share
 *   - data-modal-url (Required): URL of the modal to open for on web device. Mobile device use HTML share API.
 *   - data-modal-title (Required): Title of the share modal
 */
class ShareButtonWidget {
    init(root) {
        let count = 0;
        const base = root ? root : document;
        base.querySelectorAll('[data-component-id=share-button]')
            .forEach((elt) => {
                    count++;
                    elt.removeEventListener('click', this._on_click);
                    elt.addEventListener('click', this._on_click);
                }
            );
        console.log(count + ' share-button component(s) found');
    }

    _on_click() {
        console.log('_on_click', event.target);

        const elt = event.target.closest('[data-component-id=share-button]');
        const url = elt.getAttribute('data-url');
        if (!this._is_mobile_ua() || !navigator.share) {
            const modalUrl = elt.getAttribute('data-modal-url');
            const modalTitle = elt.getAttribute('data-modal-title');
            if (modalUrl) {
                koki.w.modal.open(modalUrl, modalTitle);
                return
            }
        }

        try {
            if (navigator.share) {
                const text = elt.getAttribute('data-text');
                navigator.share({
                    text: text,
                    url: url
                });
            }
        } catch (ex) {
            console.error(ex.message);
            alert(ex.message);
        }
    }

    _is_mobile_ua() {
        return /Mobi|Android|iPhone|iPad|iPod|BlackBerry|Windows Phone/i.test(navigator.userAgent);
    }
}

document.addEventListener('DOMContentLoaded', function () {
        const widget = new ShareButtonWidget();
        koki.w['shareButton'] = widget;
        widget.init();
    }
);

