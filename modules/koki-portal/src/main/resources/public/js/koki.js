/**
 * Ajax checkbox
 * When this button is clicked:
 * 1. It will execute an action by calling the endpoint defined by attribute "data-action-url"
 *
 * Attributes:
 *   - data-action-url: URL of the action to execute. The query parameter "checked" is appended to the query parameters of the URL
 *     This URL must return the json:
 *      {
 *          success: true | false
 *          error: Error (if success=false)
 *      }
 */
class AjaxCheckboxWidget {
    init() {
        let count = 0;
        let me = this;
        document.querySelectorAll('[data-component-id=ajax-checkbox]')
            .forEach((elt) => {
                    elt.removeEventListener('click', koki.widgets.ajaxCheckbox.onClick);
                    elt.addEventListener('click', koki.widgets.ajaxCheckbox.onClick);

                    count++
                }
            );
        console.log(count + ' ajax-checkbox component(s) found');
    }

    onClick() {
        console.log('onClick()');
        const elt = window.event.target;
        const actionUrl = elt.getAttribute('data-action-url');
        if (actionUrl) {
            // Confirm
            const confirmMsg = elt.getAttribute('data-action-confirm');
            if (confirmMsg && !confirm(confirmMsg)) {
                return
            }

            // Execute
            const separator = actionUrl.indexOf('?') > 0 ? '&' : '?';
            const url = actionUrl + separator + 'checked=' + elt.checked
            koki.widgets.ajaxCheckbox._execute(url);
        }
    }

    _execute(actionUrl) {
        console.log('Executing action ' + actionUrl);
        fetch(actionUrl)
            .then(response => {
                if (response.ok) {
                    response.json().then(json => {
                        console.log('Executed', json);
                        if (!json.success) {
                            alert('Failed: ' + json.error);
                        }
                    });
                } else {
                    console.log('Failed to submit to ' + form.action, response.statusText);
                    alert('Failed');
                }
            });
    }
}


/**
 * Widget container
 */
class KokiWidgets {
    constructor() {
        this.ajaxCheckbox = new AjaxCheckboxWidget();
        // this.map = new MapWidget();
        //this.uploader = new UploaderWidget();
        //this.address = new AddressWidget();
        // this.ajaxFragment = new AjaxFragmentWidget();
        // this.intlTel = new IntlTel();
        // this.ajaxButton = new AjaxButtonWidget();
        // this.loadMore = new LoadMoreWidget();
        // this.modal = new ModalWidget();
        // this.modalButton = new ModalButtonWidget();

        this.ajaxCheckbox.init();
        // this.map.init();
        //this.uploader.init();
        //this.address.init();
        //this.ajaxFragment.init();
        //this.intlTel.init();
        //this.widgets.ajaxButton.init();
        //this.widgets.loadMore.init();
        //this.widgets.modal.init();
        //this.widgets.modalButton.init();
    }
}

/**
 * Main Class
 */
class Koki {
    constructor() {
        this.widgets = new KokiWidgets();
        this.w = {};
    }

    init(root) {
        console.log('init()', root);
        for (const [key, value] of Object.entries(this.w)) {
            value.init(root);
        }
    }

    /**
     * Load the content of a URL into an element of the DOM document
     *
     * @param url - URL where to fetch the content
     * @param targetId - ID of the DOM element where to inject the content fetched from the URL
     * @param successCallback - function to call on success
     */
    load(url, targetId, successCallback) {
        const target = document.getElementById(targetId);
        if (!target) {
            console.error('Element #' + targetId + ' found');
            return
        }

        if (url) {
            target.innerHTML = '<div class="text-center w-100"><i class="fas fa-spinner fa-spin"></div>';
            fetch(url)
                .then(response => {
                    if (response.ok) {
                        response.text().then(html => {
                            target.innerHTML = html;
                            if (successCallback) {
                                successCallback();
                            }

                            // Reinitialize all widgets
                            koki.init(target);
                        });
                    }
                }).catch(() => {
                target.innerHTML = 'Error';
            });
        } else {
            console.error('No url provided to refresh from');
        }
    }

    /**
     * Load the content of a URL to replace a DOM element
     *
     * @param url - URL where to fetch the content
     * @param targetId - ID of the DOM element where to inject the content fetched from the URL
     */
    replaceWith(url, targetId) {
        const target = document.getElementById(targetId);
        if (!target) {
            console.error('Element #' + targetId + ' found');
            return
        }

        if (url) {
            const parent = target.parentElement;
            target.innerHTML = '<div class="text-center"><i class="fas fa-spinner fa-spin"></div>';
            fetch(url)
                .then(response => {
                    if (response.ok) {
                        response.text().then(html => {
                            $('#' + targetId).replaceWith(html);

                            // Reinitialize all widgets
                            koki.init(parent);
                        });
                    }
                }).catch(() => {
                target.innerHTML = 'Error';
            });
        } else {
            console.error('No url provided to refresh from');
        }
    }

    /**
     *
     * @param formId
     * @param successCallback
     * @param errorCallback
     */
    submit_form(formId, successCallback, errorCallback) {
        const form = document.getElementById(formId);
        if (form == null) {
            console.log('FORM #' + formId + '> found');
            return;
        }
        if (!form.checkValidity()) {
            console.error('The form is not valid');
            return;
        }

        event.preventDefault();
        const data = new FormData(form);
        const method = form.getAttribute("method");
        fetch(
            form.action,
            {
                method: method ? method : 'GET',
                body: new URLSearchParams(data).toString(),
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            }).then(response => {
            if (response.ok) {
                response.json().then((json) => {
                    if (successCallback) {
                        successCallback(json);
                    }
                });
            } else {
                if (errorCallback) {
                    errorCallback();
                }
            }
        });
    }
}

const koki = new Koki();

