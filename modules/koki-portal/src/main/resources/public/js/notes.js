function koki_notes_delete(id) {
    console.log('Deleting Note#' + id);
    if (confirm('Are you sure you want to delete the note?')) {
        fetch('/notes/' + id + '/delete')
            .then(function () {
                _koki_notes_refresh();
            });
    }
}

function koki_notes_create() {
    console.log('Create Note');
    const container = document.getElementById('note-list');
    const ownerId = container.getAttribute("data-owner-id");
    const ownerType = container.getAttribute("data-owner-type");

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

function _koki_notes_open_modal(html, edit) {
    /* Body */
    document.getElementById("note-modal-body").innerHTML = html;

    /* Title */
    document.getElementById('note-title-create').style.display = (edit ? 'none' : 'block');
    document.getElementById('note-title-edit').style.display = (!edit ? 'none' : 'block');

    /* Form */
    document.getElementById('note-form').addEventListener('submit', _koki_notes_submit_form);

    /* Subject */
    document.getElementById('subject').addEventListener('keydown', _koki_notes_on_change);

    /* Body */
    const htmlBody = new Quill(
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
    htmlBody.on('text-change', _koki_notes_on_change);

    /* Cancel */
    document.getElementById('btn-note-cancel').addEventListener('click', _koki_notes_close_modal)

    /* open the popup */
    const modal = new bootstrap.Modal('#note-modal');
    modal.show();
}

function _koki_notes_close_modal() {
    /* remove all event listeners */
    document.getElementById('note-form').removeEventListener('submit', _koki_notes_submit_form);
    document.getElementById("subject").removeEventListener('keydown', _koki_notes_on_change);
    document.getElementById("btn-note-cancel").removeEventListener('click', _koki_notes_close_modal)

    /* close */
    document.querySelector('#note-modal .btn-close').click();
}

function _koki_notes_submit_form() {
    console.log('Submitting the Note');

    event.preventDefault();
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

function _koki_notes_on_change() {
    const subject = document.getElementById('subject');
    const editor = document.querySelector('.ql-editor')

    // console.log('subject=' + subject.value, ' - body=' + editor.innerHTML, editor.textContent);

    document.getElementById('body').value = editor.innerHTML;
    document.getElementById('btn-note-submit').disabled = subject.value.size === 0 ||
        !editor.textContent;
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
