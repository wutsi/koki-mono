/**
 * Address widget
 *
 * Attributes
 *  - data-country-id: ID of the dropdown that contains the countries
 *  - data-city-id: ID of the dropdown that contains the cities
 *  - data-neighborhood-id: ID of the dropdown that contains the countries
 */
class AddressWidget {
    init() {
        let count = 0;
        document.querySelectorAll('[data-component-id=address]')
            .forEach((elt) => {
                    const countryId = elt.getAttribute("data-country-id");
                    const cityId = elt.getAttribute("data-city-id");
                    const neighborhoodId = elt.getAttribute("data-neighborhood-id");

                    $('#' + countryId).select2();
                    $('#' + countryId).on('select2:select', function (e) {
                        console.log('country changed....');
                        $('#' + cityId).val('').trigger('change');
                        if (neighborhoodId) {
                            $('#' + neighborhoodId).val('').trigger('change');
                        }
                    });

                    $('#' + cityId).select2({
                            ajax: {
                                url: function () {
                                    return '/locations/selector/search?type=CITY&country=' + document.getElementById(countryId).value;
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
                            placeholder: 'Select an city',
                            allowClear: true,
                            tokenSeparators: [','],
                            minimumInputLength: 2,
                        }
                    );
                    if (neighborhoodId) {
                        $('#' + cityId).on('select2:select', function (e) {
                            console.log('city changed....');
                            $('#' + neighborhoodId).val('').trigger('change');
                        });

                        $('#' + neighborhoodId).select2({
                                ajax: {
                                    url: function () {
                                        return '/locations/selector/search?type=NEIGHBORHOOD&' +
                                            '&parent-id=' + document.getElementById(cityId).value +
                                            '&country=' + document.getElementById(countryId).value;
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
                                placeholder: 'Select a neighbourhood',
                                allowClear: true,
                                tokenSeparators: [','],
                                minimumInputLength: 2,
                            }
                        );
                    }
                    count++
                }
            );
        console.log(count + ' address component(s) found');
    }
}

document.addEventListener('DOMContentLoaded', function () {
        const widget = new AddressWidget();
        koki.w['address'] = widget;
        widget.init();
    }
);


