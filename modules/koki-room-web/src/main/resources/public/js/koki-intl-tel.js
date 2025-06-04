/**
 * Map widget based on https://leafletjs.com
 *
 * Attributes:
 *  - id: ID of the map
 *  - data-country, default country
 */
class KokiIntlTelWidget {
    init() {
        let count = 0;
        document.querySelectorAll('[type=tel]')
            .forEach((elt) => {
                let country = elt.getAttribute("data-country");
                let name = elt.getAttribute("name");

                window.intlTelInput(elt, {
                    initialCountry: (!country || country.length === 0 ? "auto" : country),
                    strictMode: true,
                    geoIpLookup: callback => {
                        fetch("https://ipapi.co/json")
                            .then(res => res.json())
                            .then(data => callback(data.country_code))
                            .catch(() => callback("us"));
                    },
                    hiddenInput: () => ({phone: name + "Full", country: name + "Country"}),
                    loadUtils: () => import("https://cdn.jsdelivr.net/npm/intl-tel-input@25.3.1/build/js/utils.js")
                });

                count++;
            });
        console.log(count + ' tel component(s) found');
    }
}

document.addEventListener(
    'DOMContentLoaded',
    function () {
        const widget = new KokiIntlTelWidget();
        widget.init();
    },
    false
);

