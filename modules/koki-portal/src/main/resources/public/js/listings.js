function koki_listing_filter_opened() {
    console.log('koki_listing_filter_opened()');
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
