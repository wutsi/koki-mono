function koki_message_submit_form() {
    console.log('koki_message_submit_form()');
    koki.submit_form(
        'message-composer-form',
        function () {
            const id = document.getElementById('conversationId').value;
            const url = '/messages/conversations/' + id + '/messages'

            koki.load(url, 'message-list', function () {
                document.querySelector('#message-composer-form textarea').value = '';
                document.querySelector('#message-composer-form').reset();
            });
        }
    )
}

function koki_message_conversation_selected(elt) {
    console.log('koki_message_conversation_selected()', elt);
    const badge = elt.querySelector('.unread-messages');
    if (badge) {
        badge.parentElement.removeChild(badge);
    }
}
