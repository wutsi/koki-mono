function koki_prices_delete(id) {
    console.log('Deleting price#' + id);
    if (confirm('Are you sure you want to delete the price?')) {
        fetch('/prices/' + id + '/delete')
            .then(function () {
                _koki_prices_refresh_parent_window();
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

function koki_product_editor_ready() {
    const type = document.getElementById('type');
    type.addEventListener('change', _on_product_type_changed);

    $('#categoryId').select2({
            ajax: {
                url: function () {
                    return '/categories/selector/search?type=' + type.value;
                },
                dataType: 'json',
                delay: 1000,
                processResults: function (item) {
                    const xitems = item.map(function (item) {
                        return {
                            id: item.id,
                            text: item.name,
                        }
                    });
                    return {
                        results: xitems
                    };
                }
            },
            placeholder: 'Select a category',
            allowClear: true,
            tokenSeparators: [','],
            minimumInputLength: 3,
        }
    );

}

/*===== callbacks =========*/
function _on_product_type_changed() {
    console.log('_on_product_type_changed');

    // Category
    const type = document.getElementById('type');
    $('#categoryId').val('').trigger('change');
    document.getElementById('categoryId').disabled = !type.value || (type.value.length === 0);

    // custom attributes
    document.getElementById('section-service').style.display = (type.value === 'SERVICE' ? 'block' : 'none');
    document.getElementById('section-digital').style.display = (type.value === 'DIGITAL' ? 'block' : 'none');
    document.getElementById('section-physical').style.display = (type.value === 'PHYSICAL' ? 'block' : 'none');
}

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

function koki_products_select2(id, parentId) {
    $('#' + id).select2({
        ajax: {
            url: '/products/selector/search',
            dataType: 'json',
            delay: 1000,
            processResults: function (item) {
                const xitems = item.map(function (item) {
                    return {
                        id: item.id,
                        text: item.name,
                    }
                });
                return {
                    results: xitems
                };
            }
        },
        placeholder: 'Select a product',
        allowClear: true,
        tokenSeparators: [','],
        minimumInputLength: 3,
        dropdownParent: parentId ? $('#' + parentId) : $(document.body),
    });
}






