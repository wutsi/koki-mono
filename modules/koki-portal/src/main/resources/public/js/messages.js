class KokiMessage {
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
            });
    }

    refresh() {
        const container = document.getElementById('message-container');
        const folder = document.getElementById("message-folder").value;
        let url = container.getAttribute("data-refresh-url");
        if (folder) {
            url = url + '&folder=' + folder;
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
