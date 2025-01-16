function koki_contacts_select2(id, parentId) {
    $('#' + id).select2({
        ajax: {
            url: '/contacts/selector/search',
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
        placeholder: 'Select a contact',
        allowClear: true,
        tokenSeparators: [','],
        minimumInputLength: 3,
        dropdownParent: parentId ? $('#' + parentId) : $(document.body),
    });
}
