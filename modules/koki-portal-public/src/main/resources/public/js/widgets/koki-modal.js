/**
 * Modal widget
 */
class ModalWidget {
    init(root) {
        let count = 0;
        const base = root ? root : document;
        base.querySelectorAll('[data-component-id=modal]')
            .forEach((elt) => {
                    count++
                }
            );
        console.log(count + ' modal component(s) found');
    }

    open(url, title, open_callback, close_callback, large) {
        const modal = document.getElementById('koki-modal');
        const me = this;
        if (modal) {
            modal.addEventListener('hidden.bs.modal', close_callback);

            fetch(url)
                .then(response => {
                    if (response.ok) {
                        response.text()
                            .then(html => {
                                document.getElementById("koki-modal-body").innerHTML = html;
                                if (large) {
                                    document.getElementById("koki-modal-dialog").classList.add('modal-xl');
                                } else {
                                    document.getElementById("koki-modal-dialog").classList.remove('modal-xl');
                                }

                                document.getElementById("koki-modal-title").innerHTML = title;

                                // Default close button
                                const btnClose = document.querySelector("#koki-modal [data-close]");
                                if (btnClose) {
                                    btnClose.removeEventListener('click', me.close);
                                    btnClose.addEventListener('click', me.close);
                                }

                                // Form submit
                                const btnSubmit = document.querySelector("#koki-modal [type=submit]");
                                if (btnSubmit && btnSubmit.getAttribute('data-async')) {
                                    btnSubmit.removeEventListener('click', me._submit);
                                    btnSubmit.addEventListener('click', me._submit);
                                }

                                // Open callback
                                if (open_callback) {
                                    open_callback();
                                }

                                // Show
                                const modal = new bootstrap.Modal('#koki-modal');
                                modal.show();

                                // Init the components
                                setTimeout(
                                    function () {
                                        const body = document.getElementById("koki-modal-body");
                                        koki.init(body);
                                    },
                                    100
                                )
                            })
                    } else {
                        console.log('Unable to fetch the modal', response.text());
                    }
                });
        }
    }

    close() {
        document.querySelector('#koki-modal .btn-close').click();
    }

    _submit() {
        console.log('_submit()');

        const form = document.querySelector("#koki-modal form");
        if (!form.checkValidity()) {
            console.error('The form is not valid');
            return;
        }

        event.preventDefault();
        const data = new FormData(form);
        fetch(
            form.action,
            {
                method: 'POST',
                body: new URLSearchParams(data).toString(),
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            }).then(response => {
            if (response.ok) {
                response.json().then((json) => {
                    console.log('Message received', json);
                    if (json.success) {
                        koki.w.modal.close();
                        if (json.message.success) {
                            alert(json.successMessage);
                        }
                    } else if (json.message.error) {
                        alert(json.message.error);
                    }
                });
            } else {
                console.error('Error', response.text());
                alert('Failed!');
            }
        });
    }
}


document.addEventListener('DOMContentLoaded', function () {
        const widget = new ModalWidget();
        koki.w['modal'] = widget;
        widget.init();
    }
);
