class KokiMessage {
    compose(toUserId, ownerId, ownerType) {
        console.log('KokiMessage.compose()');

        koki.widgets.modal.open(
            `/messages/compose?to-user-id=${toUserId}&owner-id=${ownerId}&owner-type=${ownerType}`,
            'Send Message',
            kokiMessage._on_compose_opened,
            null);
    }

    _on_compose_opened() {
        console.log('KokiMessage._on_compose_opened()');

        document.getElementById('btn-submit').addEventListener('click', kokiMessage.send);
        document.getElementById('btn-cancel').addEventListener('click', koki.widgets.modal.close);
    }

    send() {
        console.log('KokiMessage.send()');

        const form = document.getElementById("message-form");
        if (!form.checkValidity()) {
            console.error('The form is not valid');
            return
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
                        koki.widgets.modal.close();
                        if (json.successMessage) {
                            alert(json.successMessage);
                        }
                    } else {
                        alert(json.errorMessage);
                    }
                });
            } else {
                console.error('Error', response.text());
                alert('Unable to send the message');
            }
        });
    }

    show(id) {
        koki.widgets.modal.open(
            '/messages/' + id,
            'Message',
            null,
            function () {
                const elts = document.querySelectorAll('#message-' + id + ' td');
                elts.forEach((elt) => {
                    elt.classList.remove('message-status-NEW');
                });
            });
    }

    archive(id) {
        this._update_status(id, '/messages/' + id + '/archive');
    }

    unarchive(id) {
        this._update_status(id, '/messages/' + id + '/unarchive');
    }

    _update_status(id, url) {
        const me = this;
        fetch(url)
            .then(() => {
                me.refresh();
                koki.widgets.modal.close();
            });
    }

    refresh() {
        const container = document.getElementById('message-container');
        const folder = document.getElementById("message-folder");
        let url = container.getAttribute("data-refresh-url");
        if (folder) {
            url = url + '&folder=' + folder.value;
        }
        console.log('Reloading ' + url + ' to #message-container');
        fetch(url)
            .then(response => {
                response.text()
                    .then(html => {
                        container.innerHTML = html;
                    })
            });
    }
}

const kokiMessage = new KokiMessage();

