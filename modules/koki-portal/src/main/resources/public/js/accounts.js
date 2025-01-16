function koki_accounts_select2(id, parentId) {
    $('#' + id).select2({
        ajax: {
            url: '/accounts/selector/search',
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
        placeholder: 'Select an account',
        allowClear: true,
        tokenSeparators: [','],
        minimumInputLength: 3,
        dropdownParent: parentId ? $('#' + parentId) : $(document.body),
    });
}
