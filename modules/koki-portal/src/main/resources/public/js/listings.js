class KokiListing {
    open_search_modal() {
        console.log('do_search()');

        koki.widgets.modal.open(
            '/listings/filter',
            null,
            kokiListing._on_search_modal_opened,
            null
        );

    }

    _on_search_modal_opened() {
        console.log('_on_search_modal_opened()');

        document.getElementById('btn-cancel').addEventListener('click', kokiListing._on_search_modal_closed);

        $('#locationId').select2({
            ajax: {
                url: '/locations/selector/search?type=NEIGHBORHOOD',
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
            allowClear: true,
            tokenSeparators: [','],
            minimumInputLength: 3,
            dropdownParent: $('#koki-modal')
        });
    }

    _on_search_modal_closed() {
        console.log('_on_search_modal_closed()');

        koki.widgets.modal.close();
    }
}

const kokiListing = new KokiListing();
