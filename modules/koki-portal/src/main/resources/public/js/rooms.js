class KokiRoomUnitEditor {
    create(roomId) {
        koki.widgets.modal.open(
            '/room-units/create?room-id=' + roomId,
            'Add Room Unit',
            kokiRoomUnitEditor.onOpened,
            kokiRoomUnitEditor.onClosed,
        );
    }

    edit(roomUnitId) {
        koki.widgets.modal.open(
            '/room-units/' + roomUnitId + '/edit',
            'Edit Room Unit',
            kokiRoomUnitEditor.onOpened,
            kokiRoomUnitEditor.onClosed,
        );
    }

    onOpened() {
        console.log('opening...');
        document.getElementById('btn-room-unit-cancel').addEventListener('click', koki.widgets.modal.close);
        document.getElementById('frm-room-unit').addEventListener('submit', kokiRoomUnitEditor.onSubmit);
    }

    onClosed() {
        console.log('closing...');
        document.getElementById('btn-room-unit-cancel').removeEventListener('click', koki.widgets.modal.close);
        document.getElementById('frm-room-unit').removeEventListener('submit', kokiRoomUnitEditor.onSubmit);
    }

    onSubmit() {
        event.preventDefault();

        const form = document.getElementById("frm-room-unit");

        // Check validity
        if (!form.checkVisibility()) {
            console.log('Form not valid');
            return
        }

        // Submit
        const roomId = document.getElementById("roomId").value;
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
                    console.log('Saved', json);
                    if (json.success) {
                        window.location.href = '/rooms/' + roomId + '?tab=room-unit'
                    } else {
                        alert('Failed: ' + json.error);
                    }
                });
            } else {
                console.log('Failed to submit to ' + form.action, response.statusText);
                alert('Failed');
            }
        });
    }
}

const kokiRoomUnitEditor = new KokiRoomUnitEditor();

class KokiEditor {
    ready() {
        const elt = document.getElementById('leaseType');
        elt.addEventListener('change', kokiEditor.onLeaseTypeChanged);

        this.onLeaseTypeChanged();
    }

    onLeaseTypeChanged() {
        const leaseType = document.getElementById('leaseType').value;

        document.getElementById('pricePerNight').disabled = (leaseType !== 'SHORT_TERM');
        document.getElementById('pricePerMonth').disabled = (leaseType !== 'LONG_TERM');
    }
}

const kokiEditor = new KokiEditor();
