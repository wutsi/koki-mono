class RoomPage {
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
        console.log('fullPhone', document.getElementById('fullPhone').value);
        console.log('country', document.getElementById('country').value);

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
                document.querySelector('#room-message-modal .btn-close').click();
                alert('Your message has been sent');
            } else {
                response.text().then(txt => {
                    console.log('Error', txt);
                    alert('Failed');
                });
            }
            document.getElementById("btn-send").disabled = false;
        });
    }
}

document.addEventListener(
    'DOMContentLoaded',
    function () {
        const page = new RoomPage();

        // Modal
        const modal = document.getElementById('room-message-modal')
        modal.addEventListener('shown.bs.modal', () => {
            page.on_message_modal_opened()
        });

        // Form
        const frmSend = document.getElementById('frm-send');
        frmSend.addEventListener('submit', () => {
            page.on_message_submitted()
        });
    }
);
