class KokiRoomPage {
    init() {

    }

    on_message_modal_opened() {
        console.log('on_message_modal_opened()');

        const form = document.getElementById("frm-send");
        form.reset();
        document.getElementById("body").value = document.getElementById("body").getAttribute("data-body");
    }

    on_message_submitted() {
        console.log('on_message_submitted()');

        event.preventDefault();
        document.getElementById("btn-send").disabled = true;

        const form = document.getElementById("frm-send");
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
                response.json().then(json => {
                    if (json.success) {
                        document.querySelector('#room-message-modal .btn-close').click();
                        alert('Your message has been sent');
                        document.getElementById("btn-send").disabled = false;
                    } else {
                        console.log('Error', json);
                        alert('Failed');
                    }
                });
            } else {
                response.text().then(txt => {
                    console.log('Error', txt);
                    alert('Failed');
                });
            }
        });
    }
}

const kokiRoomPage = new KokiRoomPage();
document.addEventListener(
    'DOMContentLoaded',
    function () {
        kokiRoomPage.init();

        // Modal
        const modal = document.getElementById('room-message-modal')
        modal.addEventListener('shown.bs.modal', kokiRoomPage.on_message_modal_opened);

        // Form
        const frmSend = document.getElementById('frm-send');
        frmSend.addEventListener('submit', kokiRoomPage.on_message_submitted);
    }
);
