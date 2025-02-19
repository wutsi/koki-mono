function koki_taxes_remove_product(id) {
    console.log('koki_taxes_remove_product(' + id + ')');
    if (confirm('Are you sure you want to remove the product?')) {
        fetch('/tax-products/' + id + '/delete')
            .then(function () {
                _koki_taxes_refresh_products();
            });
    }
}

function koki_taxes_add_product(taxId) {
    console.log('koki_taxes_add_product(' + taxId + ')');
    koki_modal_open(
        'Add Product',
        '/tax-products/create?tax-id=' + taxId,
        _koki_taxes_product_on_add_modal_opened,
        _koki_taxes_product_on_add_modal_closed,
    );

}

function koki_taxes_edit_product(id) {
    console.log('koki_taxes_edit_product(' + id + ')');
    koki_modal_open(
        'Update Product',
        '/tax-products/' + id + '/edit',
        _koki_taxes_product_on_edit_modal_opened,
        _koki_taxes_product_on_edit_modal_closed,
    );

}

function _koki_taxes_product_on_add_modal_opened() {
    console.log('_koki_taxes_product_on_add_modal_opened');

    document.getElementById('tax-product-form').addEventListener('submit', _koki_taxes_product_on_form_submitted);
    document.getElementById('btn-tax-product-cancel').addEventListener('click', koki_modal_close)

    koki_products_select2('productId', 'koki-modal');
    $('#productId').on('select2:select', function (e) {
        $('#unitPrice').load('/tax-products/prices?product-id=' + $('#productId').val());
    });
}

function _koki_taxes_product_on_add_modal_closed() {
    console.log('_koki_taxes_product_on_add_modal_closed');

    document.getElementById('tax-product-form').removeEventListener('submit', _koki_taxes_product_on_form_submitted);
    document.getElementById('btn-tax-product-cancel').removeEventListener('click', koki_modal_close)
}

function _koki_taxes_product_on_edit_modal_opened() {
    console.log('_koki_taxes_product_on_edit_modal_opened');

    document.getElementById('tax-product-form').addEventListener('submit', _koki_taxes_product_on_form_submitted);
    document.getElementById('btn-tax-product-cancel').addEventListener('click', koki_modal_close)
}

function _koki_taxes_product_on_edit_modal_closed() {
    console.log('_koki_taxes_product_on_edit_modal_closed');

    document.getElementById('tax-product-form').removeEventListener('submit', _koki_taxes_product_on_form_submitted);
    document.getElementById('btn-tax-product-cancel').removeEventListener('click', koki_modal_close)
}

function _koki_taxes_product_on_form_submitted() {
    console.log('_koki_taxes_product_on_form_submitted');

    event.preventDefault();
    const form = document.getElementById("tax-product-form");
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
            _koki_taxes_refresh_products(id);
        } else {
            console.log('Error', response.text());
            alert('Failed');
        }
    });

}

function _koki_taxes_refresh_products(id) {
    console.log('_koki_tax_products_refresh_parent_window(' + id + ')');
    if (id) {
        fetch('/tax-products/' + id + '/fragment')
            .then(response => {
                response.text()
                    .then(html => {
                        document.getElementById("tax-product-" + id).innerHTML = html;
                    })
            });
    } else {
        const container = document.getElementById('tax-product-list');
        const productId = container.getAttribute("data-tax-id");
        fetch('/tax-products/tab/items?tax-id=' + productId)
            .then(response => {
                response.text()
                    .then(html => {
                        container.innerHTML = html;
                    })
            });
    }
}
