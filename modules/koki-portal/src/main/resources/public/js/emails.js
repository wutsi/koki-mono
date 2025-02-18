/*====== viewer ============*/
function koki_emails_open(id) {
    koki_modal_open(
        'Email',
        '/emails/' + id,
        _koki_emails_open_viewer_callback,
        _koki_emails_close_viewer_callback
    );
}

function _koki_emails_open_viewer_callback() {
    console.log('_koki_emails_open_viewer_callback');

    document.getElementById('btn-email-cancel').addEventListener('click', koki_modal_close)
}

function _koki_emails_close_viewer_callback() {
    console.log('_koki_emails_close_viewer_callback');

    document.getElementById("btn-email-cancel").removeEventListener('click', koki_modal_close)
}


/*==== editor ============*/
function koki_emails_compose(attachmentIds) {
    const ownerId = document.querySelector("[data-owner-id]").getAttribute("data-owner-id");
    const ownerType = document.querySelector("[data-owner-type]").getAttribute("data-owner-type");

    let url = '/emails/compose?' +
        (ownerId ? '&owner-id=' + ownerId : '') +
        (ownerType ? '&owner-type=' + ownerType : '');
    if (attachmentIds) {
        if (Array.isArray(attachmentIds)) {
            for (var i = 0; i < attachmentIds.length; i++) {
                url = url + '&attachment-file-id=' + attachmentIds;
            }
        } else {
            url = url + '&attachment-file-id=' + attachmentIds;
        }
    }

    koki_modal_open(
        'Compose Email',
        url,
        _koki_emails_on_editor_opened,
        _koki_emails_om_editor_closed
    );
}

/*===== callbacks =======*/
function _koki_emails_on_editor_opened() {
    console.log('_koki_emails_open_editor_callback');

    /* Form */
    document.getElementById('email-form').addEventListener('submit', _koki_emails_on_form_submitted);

    /* Subject */
    document.getElementById('subject').addEventListener('keydown', _koki_emails_on_change);

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
    htmlBody.on('text-change', _koki_emails_on_change);

    /* recipient */
    document.getElementById('recipient-type-account').addEventListener('click', _koki_emails_on_account_selected);
    document.getElementById('recipient-type-contact').addEventListener('click', _koki_emails_on_contact_selected);
    document.getElementById('accountId').addEventListener('keydown', _koki_emails_on_change);
    document.getElementById('contactId').addEventListener('keydown', _koki_emails_on_change);
    koki_accounts_select2('accountId', 'koki-modal');
    koki_contacts_select2('contactId', 'koki-modal');

    /* Cancel */
    document.getElementById('btn-email-cancel').addEventListener('click', koki_modal_close)
}

function _koki_emails_om_editor_closed() {
    console.log('_koki_emails_close_editor_callback');

    document.getElementById('email-form')?.removeEventListener('submit', _koki_emails_on_form_submitted);
    document.getElementById("subject")?.removeEventListener('keydown', _koki_emails_on_change);
    document.getElementById('recipient-type-account')?.removeEventListener('click', _koki_emails_on_account_selected);
    document.getElementById('recipient-type-contact')?.removeEventListener('click', _koki_emails_on_contact_selected);
    document.getElementById('accountId')?.removeEventListener('keydown', _koki_emails_on_change);
    document.getElementById('contactId')?.removeEventListener('keydown', _koki_emails_on_change);
    document.getElementById("btn-email-cancel").removeEventListener('click', koki_modal_close)
}

function _koki_emails_on_form_submitted() {
    console.log('Sending the email');

    event.preventDefault();
    document.getElementById("btn-email-submit").disabled = true;
    const form = document.getElementById("email-form");
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
            _koki_emails_refresh_parent_window();
        } else {
            console.log('Error', response.text());
            alert('Failed');
        }
        document.getElementById("btn-email-submit").disabled = false;
    });

}

function _koki_emails_on_change() {
    const accountId = document.getElementById('accountId');
    const contactId = document.getElementById('contactId');
    const recipientType = document.getElementById('recipientType');
    const recipientId = recipientType.value === 'ACCOUNT' ? accountId.value : contactId.value
    const subject = document.getElementById('subject');
    const editor = document.querySelector('.ql-editor');

    console.log('accountId=', accountId.value);
    console.log('contactId=', contactId.value);
    console.log('recipientType=', recipientType.value);
    console.log('recipientId=', recipientId, !recipientId);
    console.log('subject=', subject.value);
    console.log('body=', body.value, editor.innerText);

    document.getElementById('body').value = editor.innerHTML;
    document.getElementById('btn-email-submit').disabled = subject.value.size === 0 ||
        !editor.textContent ||
        !recipientId;
}

function _koki_emails_on_account_selected() {
    _koki_emails_on_recipient_type_changed('ACCOUNT');
}

function _koki_emails_on_contact_selected() {
    _koki_emails_on_recipient_type_changed('CONTACT');
}

function _koki_emails_on_recipient_type_changed(type) {
    const button = document.getElementById('email-recipient-type-dropdown');
    button.innerHTML = document.getElementById('recipient-type-' + type.toLowerCase()).innerHTML;

    document.getElementById('recipientType').value = type.toUpperCase();
    document.getElementById('account-selector').style.display = (type === 'account' ? 'block' : 'none');
    document.getElementById('contact-selector').style.display = (type === 'contact' ? 'block' : 'none');

    _koki_emails_on_change();
}

function _koki_emails_refresh_parent_window() {
    const container = document.getElementById('email-list');
    if (container) {
        const ownerId = container.getAttribute("data-owner-id");
        const ownerType = container.getAttribute("data-owner-type");
        fetch('/emails/tab/more?owner-id=' + ownerId + '&owner-type=' + ownerType)
            .then(response => {
                response.text()
                    .then(html => {
                        container.innerHTML = html;
                    })
            });
    }
}
