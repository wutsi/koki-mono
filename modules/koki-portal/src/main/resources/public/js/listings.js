class KokiListing {
    open_search_modal(url) {
        console.log('do_search()');

        koki.w.modal.open(
            url,
            null,
            kokiListing._on_search_modal_opened,
            null,
            true
        );

    }

    _on_search_modal_opened() {
        console.log('_on_search_modal_opened()');
        setTimeout(
            function () {
                $('#locationIds').select2({
                    ajax: {
                        url: '/locations/selector/search?type=NEIGHBORHOOD&type=CITY',
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
            },
            100
        )
    }
}

const kokiListing = new KokiListing();
