var notesHtmlEditor;

function koki_notes_delete(id) {
    console.log('Deleting Note#' + id);
    if (confirm('Are you sure you want to delete the note?')) {
        fetch('/notes/' + id + '/delete')
            .then(function () {
                _koki_notes_refresh();
            });
    }
}

function koki_notes_edit(id) {
    console.log('Editing Note#' + id);
    fetch('/notes/' + id + '/edit')
        .then(response => {
            if (response.ok) {
                response.text()
                    .then(html => {
                        _koki_notes_open_modal(html, true);
                    })
            } else {
                console.log('Unable to fetch the editor', response.text());
            }
        });
}

function koki_notes_update(event) {
    event.preventDefault();
    console.log('Updating Note');

    const form = document.getElementById("note-form");
    const id = form.getAttribute("data-id");
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
            _koki_notes_close_modal();
            _koki_notes_refresh(id);
        } else {
            console.log('Error', response.text());
            alert('Failed');
        }
    });
}

function koki_notes_close() {
    _koki_notes_close_modal();
}

function koki_notes_create(ownerId, ownerType) {
    console.log('Create Note');
    fetch('/notes/create?owner-id=' + ownerId + '&owner-type=' + ownerType)
        .then(response => {
            if (response.ok) {
                response.text()
                    .then(html => {
                        _koki_notes_open_modal(html, false);
                    })
            } else {
                console.log('Unable to fetch the editor', response.text());
            }
        });
}

function koki_notes_add_new(event) {
    event.preventDefault();

    const form = document.getElementById("note-form");
    if (!form.checkValidity()) {
        console.log('Form validation failed');
        return;
    }

    const data = new FormData(form);
    fetch(
        '/notes/add-new',
        {
            method: 'POST',
            body: new URLSearchParams(data).toString(),
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        }).then(response => {
        if (response.ok) {
            _koki_notes_close_modal();
            _koki_notes_refresh();
        } else {
            console.log('Error', response.text());
            alert('Failed');
        }
    });
}

function koki_notes_on_change() {
    const subject = document.getElementById('subject');

    const body = document.getElementById('body');
    body.value = document.querySelector('.ql-editor').innerHTML;

    const submit = document.getElementById('btn-note-submit')
    submit.disabled = (subject.value.size > 0 && body.value.size > 0);
}

function _koki_notes_refresh(id) {
    if (id) {
        fetch('/notes/' + id)
            .then(response => {
                response.text()
                    .then(html => {
                        document.getElementById("note-" + id).innerHTML = html;
                    })
            });
    } else {
        const container = document.getElementById('note-list');
        const ownerId = container.getAttribute("data-owner-id");
        const ownerType = container.getAttribute("data-owner-type");
        fetch('/notes/widgets/list/more?owner-id=' + ownerId + '&owner-type=' + ownerType)
            .then(response => {
                response.text()
                    .then(html => {
                        container.innerHTML = html;
                    })
            });
    }
}

function _koki_notes_open_modal(html, edit) {
    document.getElementById("note-modal-body").innerHTML = html;
    document.getElementById("note-title-create").style.display = (edit ? 'none' : 'block');
    document.getElementById("note-title-edit").style.display = (!edit ? 'none' : 'block');

    _koki_notes_setup_html_editor();

    const modal = new bootstrap.Modal('#note-modal');
    modal.show();
}

function _koki_notes_setup_html_editor() {
    console.log('Creating HTML editor');
    notesHtmlEditor = new Quill(
        '#html-editor',
        {
            theme: 'snow',
            modules: {
                toolbar: [
                    ['bold', 'italic', 'underline', 'strike', 'link', {'color': []}],
                ]
            }
        }
    );
    notesHtmlEditor.on('text-change', (delta, oldDelta, source) => {
        koki_notes_on_change();
    });
}

function _koki_notes_close_modal() {
    document.querySelector('#note-modal .btn-close').click();
}


