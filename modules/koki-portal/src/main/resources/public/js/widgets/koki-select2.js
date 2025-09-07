/**
 * select2 widget
 *
 * Attributes
 *  - id: ID of the element
 *  - data-placeholder: Placeholder
 *  - data-url: URL from where to fetch the items - This is for fetching the items from JSON source
 *  - data-parent-id: ID of the parent node. If not specified, it will be the document body - Must be provided in dropdown opened in modal
 *  - data-minimum-input-length: Minimum length to fire the search. Default=3
 */
class Select2Widget {
    init() {
        let count = 0;
        document.querySelectorAll('[data-component-id=select2]')
            .forEach((elt) => {
                    const id = elt.getAttribute('id');
                    const placeholder = elt.getAttribute('data-placeholder')
                    const url = elt.getAttribute('data-url');
                    const parentId = elt.getAttribute('data-parent-id');
                    const minLength = elt.getAttribute('data-minimum-input-length');
                    console.log('url=' + url + ' - parentId=' + parentId);
                    const ajax = {
                        url: url,
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
                    };
                    $('#' + id).select2({
                        ajax: url ? ajax : null,
                        placeholder: placeholder,
                        allowClear: true,
                        tokenSeparators: [','],
                        minimumInputLength: minLength ? minLength : 3,
                        dropdownParent: parentId ? $('#' + parentId) : $(document.body)
                    });
                    count++
                }
            );
        console.log(count + ' select2 component(s) found');
    }
}

document.addEventListener('DOMContentLoaded', function () {
        const widget = new Select2Widget();
        koki.w['select2'] = widget;
        widget.init();
    }
);


