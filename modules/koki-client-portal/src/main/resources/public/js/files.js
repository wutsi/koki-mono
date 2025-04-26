class KokiFileModule {
    initTab() {
        koki.widgets.dropzone.init();
        koki.widgets.loadMore.init();
    }

    delete(id) {
        fetch('/files/tab/delete?id=' + id)
            .then(response => {
                let elt = document.getElementById('file-' + id);
                elt.parentNode.removeChild(elt);
            });

    }

    refresh(id) {
        const container = document.getElementById(id);
        if (container) {
            const ownerId = container.getAttribute("data-owner-id");
            const ownerType = container.getAttribute("data-owner-type");
            fetch('/files/tab/more?owner-id=' + ownerId + '&owner-type=' + ownerType)
                .then(response => {
                    response.text()
                        .then(html => {
                            container.innerHTML = html;
                            koki.widgets.loadMore.init();
                        })
                });
        }
    }
}

const kokiFiles = new KokiFileModule();
