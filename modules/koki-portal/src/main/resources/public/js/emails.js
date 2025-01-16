function koki_emails_open(id) {
    fetch('/emails/' + id)
        .then(response => {
            if (response.ok) {
                response.text()
                    .then(html => {
                        _koki_emails_open_modal(html, false);
                    })
            } else {
                console.log('Unable to fetch the modal', response.text());
            }
        });
}

function koki_emails_compose() {
    const container = document.getElementById('email-list');
    const ownerId = container.getAttribute("data-owner-id");
    const ownerType = container.getAttribute("data-owner-type");
    const recipientId = container.getAttribute("data-recipient-id");
    const recipientType = container.getAttribute("data-recipient-type");

    let url = '/emails/compose?' +
        (ownerId ? '&owner-id=' + ownerId : '') +
        (ownerType ? '&owner-type=' + ownerType : '') +
        (recipientId ? '&recipient-id=' + recipientId : '') +
        (recipientType ? '&recipient-type=' + recipientType : '');

    fetch(url)
        .then(response => {
            if (response.ok) {
                response.text()
                    .then(html => {
                        _koki_emails_open_modal(html, true);
                    })
            } else {
                console.log('Unable to fetch the modal', response.text());
            }
        });
}

function _koki_emails_open_modal(html, compose) {
    /* Body */
    document.getElementById("email-modal-body").innerHTML = html;

    /* Title */
    document.getElementById('email-title-compose').style.display = (compose ? 'block' : 'none');
    document.getElementById('email-title-view').style.display = (!compose ? 'block' : 'none');

    if (compose) {
        /* Form */
        document.getElementById('email-form').addEventListener('submit', _koki_emails_send);

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
        document.getElementById('recipient-type-account').addEventListener('click', _koki_emails_account_selected);
        document.getElementById('recipient-type-contact').addEventListener('click', _koki_emails_contact_selected);
        document.getElementById('accountId').addEventListener('keydown', _koki_emails_on_change);
        document.getElementById('contactId').addEventListener('keydown', _koki_emails_on_change);
        koki_accounts_select2('accountId', 'email-modal');
        koki_contacts_select2('contactId', 'email-modal');
    }

    /* Cancel */
    document.getElementById('btn-email-cancel').addEventListener('click', _koki_emails_close_modal)

    const modal = new bootstrap.Modal('#email-modal');
    modal.show();
}

function _koki_emails_close_modal() {
    /* remove all event listeners */
    document.getElementById('email-form')?.removeEventListener('submit', _koki_emails_send);
    document.getElementById("subject")?.removeEventListener('keydown', _koki_emails_on_change);
    document.getElementById('recipient-type-account')?.removeEventListener('click', _koki_emails_account_selected);
    document.getElementById('recipient-type-contact')?.removeEventListener('click', _koki_emails_contact_selected);
    document.getElementById('accountId')?.removeEventListener('keydown', _koki_emails_on_change);
    document.getElementById('contactId')?.removeEventListener('keydown', _koki_emails_on_change);
    document.getElementById("btn-email-cancel").removeEventListener('click', _koki_emails_close_modal)

    /* close */
    document.querySelector('#email-modal .btn-close').click();
}

function _koki_emails_send() {
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
            _koki_emails_close_modal();
            _koki_emails_refresh();
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

function koki_emails_close() {
    _koki_emails_close_modal();
}

function _koki_emails_account_selected() {
    _koki_emails_recipient_type_changed('ACCOUNT');
}

function _koki_emails_contact_selected() {
    _koki_emails_recipient_type_changed('CONTACT');
}

function _koki_emails_recipient_type_changed(type) {
    const button = document.getElementById('email-recipient-type-dropdown');
    button.innerHTML = document.getElementById('recipient-type-' + type.toLowerCase()).innerHTML;

    document.getElementById('recipientType').value = type.toUpperCase();
    document.getElementById('account-selector').style.display = (type === 'account' ? 'block' : 'none');
    document.getElementById('contact-selector').style.display = (type === 'contact' ? 'block' : 'none');

    _koki_emails_on_change();
}

function _koki_emails_refresh() {
    const container = document.getElementById('email-list');
    const ownerId = container.getAttribute("data-owner-id");
    const ownerType = container.getAttribute("data-owner-type");
    fetch('/emails/widgets/list/more?owner-id=' + ownerId + '&owner-type=' + ownerType)
        .then(response => {
            response.text()
                .then(html => {
                    container.innerHTML = html;
                })
        });
}
