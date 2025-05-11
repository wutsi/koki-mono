class KokiImageEditor {
    show(id) {
        koki.widgets.modal.open(
            '/images/' + id,
            'Image',
        );
    }
}

const kokiImageEditor = new KokiImageEditor();
