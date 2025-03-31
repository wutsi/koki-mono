function koki_notes_delete(id) {
    console.log('Deleting Note#' + id);
    if (confirm('Are you sure you want to delete the note?')) {
        fetch('/notes/' + id + '/delete')
            .then(function () {
                _koki_notes_refresh_parent_window();
            });
    }
}

function koki_notes_create() {
    console.log('Create Note');
    const container = document.getElementById('note-list');
    const ownerId = container.getAttribute("data-owner-id");
    const ownerType = container.getAttribute("data-owner-type");

    koki_modal_open(
        'Create Note',
        '/notes/create?owner-id=' + ownerId + '&owner-type=' + ownerType,
        _koki_notes_on_modal_opened,
        _koki_notes_on_modal_closed,
    );
}

function koki_notes_edit(id) {
    console.log('Editing Note#' + id);
    koki_modal_open(
        'Note',
        '/notes/' + id + '/edit',
        _koki_notes_on_modal_opened,
        _koki_notes_on_modal_closed,
    );
}

function koki_notes_view(id) {
    console.log('View Note#' + id);
    koki_modal_open(
        'Note',
        '/notes/' + id,
        _koki_notes_on_viewer_opened,
        _koki_notes_on_viewer_closed,
    );
}


/*===== callbacks =========*/
function _koki_notes_on_modal_opened() {
    console.log('_koki_notes_on_modal_opened');

    /* Form */
    document.getElementById('note-form').addEventListener('submit', _koki_notes_on_form_submitted);

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

    document.getElementById("durationHours").addEventListener('change', _koki_notes_on_change);
    document.getElementById("durationMinutes").addEventListener('change', _koki_notes_on_change);

    /* Cancel */
    document.getElementById('btn-note-cancel').addEventListener('click', koki_modal_close)
}

function _koki_notes_on_modal_closed() {
    console.log('_koki_notes_on_modal_closed');

    /* remove all event listeners */
    document.getElementById('note-form').removeEventListener('submit', _koki_notes_on_form_submitted);
    document.getElementById("subject").removeEventListener('keydown', _koki_notes_on_change);
    document.getElementById("durationHours").removeEventListener('change', _koki_notes_on_change);
    document.getElementById("durationMinutes").removeEventListener('change', _koki_notes_on_change);
    document.getElementById("btn-note-cancel").removeEventListener('click', koki_modal_close)
}

function _koki_notes_on_viewer_opened() {
    console.log('_koki_notes_on_viewer_opened');
    document.getElementById('btn-note-cancel').addEventListener('click', koki_modal_close)
}

function _koki_notes_on_viewer_closed() {
    console.log('_koki_notes_on_viewer_opened');
    document.getElementById("btn-note-cancel").removeEventListener('click', koki_modal_close)
}

function _koki_notes_on_form_submitted() {
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
            koki_modal_close();
            _koki_notes_refresh_parent_window(id);
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
    document.getElementById('btn-note-submit').disabled = subject.value.size === 0;
}

function _koki_notes_refresh_parent_window(id) {
    if (id) {
        fetch('/notes/' + id + '/fragment')
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
        fetch('/notes/tab/more?owner-id=' + ownerId + '&owner-type=' + ownerType)
            .then(response => {
                response.text()
                    .then(html => {
                        container.innerHTML = html;
                    })
            });
    }
}
