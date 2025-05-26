class KokiMessageTable {
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
                const elt = document.getElementById('message-' + id);
                elt.parentNode.removeChild(elt);
            });
    }

    refresh() {
        const container = document.getElementById('message-container');
        const ownerId = container.getAttribute("data-owner-id");
        const ownerType = container.getAttribute("data-owner-type");
        const folder = document.getElementById("message-folder").value;
        fetch('/messages/tab/more?owner-id=' + ownerId + '&owner-type=' + ownerType + '&folder=' + folder)
            .then(response => {
                response.text()
                    .then(html => {
                        container.innerHTML = html;
                    })
            });
    }
}

const kokiMessageTable = new KokiMessageTable();
