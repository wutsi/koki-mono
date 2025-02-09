function koki_prices_delete(id) {
    console.log('Deleting price#' + id);
    if (confirm('Are you sure you want to delete the price?')) {
        fetch('/prices/' + id + '/delete')
            .then(function () {
                _koki_notes_refresh_parent_window();
            });
    }
}

function koki_prices_create() {
    console.log('Create Note');
    const container = document.getElementById('price-list');
    const productId = container.getAttribute("data-product-id");

    koki_modal_open(
        'Create Price',
        '/prices/create?product-id=' + productId,
        _koki_prices_on_modal_opened,
        _koki_prices_on_modal_closed,
    );
}

function koki_prices_edit(id) {
    console.log('Update Note');
    koki_modal_open(
        'Update Price',
        '/prices/' + id + '/edit',
        _koki_prices_on_modal_opened,
        _koki_prices_on_modal_closed,
    );
}

/*===== callbacks =========*/
function _koki_prices_on_modal_opened() {
    console.log('_koki_prices_on_modal_opened');

    /* Form */
    document.getElementById('price-form').addEventListener('submit', _koki_prices_on_form_submitted);

    /* Cancel */
    document.getElementById('btn-price-cancel').addEventListener('click', koki_modal_close)
}

function _koki_prices_on_modal_closed() {
    console.log('_koki_prices_on_modal_closed');

    /* remove all event listeners */
    document.getElementById('price-form').removeEventListener('submit', _koki_prices_on_form_submitted);
    document.getElementById("btn-price-cancel").removeEventListener('click', koki_modal_close)
}

function _koki_prices_on_form_submitted() {
    console.log('_koki_prices_on_form_submitted');

    event.preventDefault();
    const form = document.getElementById("price-form");
    const id = form.getAttribute("data-id")
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
            _koki_prices_refresh_parent_window(id);
        } else {
            console.log('Error', response.text());
            alert('Failed');
        }
    });
}

function _koki_prices_refresh_parent_window(id) {
    console.log('_koki_prices_refresh_parent_window(' + id + ')');
    if (id) {
        fetch('/prices/' + id + '/fragment')
            .then(response => {
                response.text()
                    .then(html => {
                        document.getElementById("price-" + id).innerHTML = html;
                    })
            });
    } else {
        const container = document.getElementById('price-list');
        const productId = container.getAttribute("data-product-id");
        fetch('/prices/tab/more?product-id=' + productId)
            .then(response => {
                response.text()
                    .then(html => {
                        container.innerHTML = html;
                    })
            });
    }
}





