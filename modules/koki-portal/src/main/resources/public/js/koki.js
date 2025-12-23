/**
 * Main Class
 */
class Koki {
    constructor() {
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

