/**
 * IntlTel
 *
 * Attributes:
 *  - data-country: Default country
 */
class IntlTelWidget {
    init() {
        let count = 0;
        document.querySelectorAll('input[type=tel]')
            .forEach((elt) => {
                    let country = elt.getAttribute("data-country");
                    window.intlTelInput(elt, {
                        initialCountry: (!country || country.length === 0 ? "auto" : country),
                        strictMode: true,
                        geoIpLookup: callback => {
                            fetch("https://ipapi.co/json")
                                .then(res => res.json())
                                .then(data => callback(data.country_code))
                                .catch(() => callback("us"));
                        },
                        hiddenInput: () => ({phone: elt.getAttribute("name") + "Full"}),
                        loadUtils: () => import("https://cdn.jsdelivr.net/npm/intl-tel-input@25.3.1/build/js/utils.js")
                    });

                    count++
                }
            );
        console.log(count + ' intl-tel component(s) found');
    }
}

document.addEventListener('DOMContentLoaded', function () {
        const widget = new IntlTelWidget();
        koki.w['intlTel'] = widget;
        widget.init();
    }
);

