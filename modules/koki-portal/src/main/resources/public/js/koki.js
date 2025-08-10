/**
 * Address widget
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


/**
 * Widget for loading content asynchronously
 *
 * Attributes
 *  - data-url: URL of the fragment to load
 */
class AjaxFragmentWidget {
    init() {
        let count = 0;
        document.querySelectorAll('[data-component-id=ajax-fragment]')
            .forEach((elt) => {
                let url = elt.getAttribute('data-url');
                if (url) {
                    fetch(url).then(function (response) {
                        response.text().then(function (html) {
                            elt.innerHTML = html;

                            let callback = elt.getAttribute('data-callback');
                            if (callback) {
                                eval(callback + "()");
                            }
                        });
                    });

                    count++
                }
            });
        console.log(count + ' ajax-fragment component(s) found');
    }
}

/**
 * Uploader widget
 *
 * Attributes
 * - data-upload-button-id: ID of the button to click for selecting the files
 *
 * Upload button attribute
 * - data-file-button-id: ID of the FILE button
 */
class UploaderWidget {
    init() {
        let count = 0;
        document.querySelectorAll('[data-component-id=uploader]')
            .forEach((elt) => {
                const file = elt.querySelector("input[type=file]");
                if (file) {
                    file.removeEventListener('change', koki.widgets.uploader.onUploaded);
                    file.addEventListener('change', koki.widgets.uploader.onUploaded);

                    const btnId = elt.getAttribute("data-upload-button-id");
                    if (btnId) {
                        const btn = document.getElementById(btnId);
                        if (btn) {
                            btn.removeEventListener('click', koki.widgets.uploader.onClick);
                            btn.addEventListener('click', koki.widgets.uploader.onClick);
                        }
                    } else {
                        console.log('No upload button' + (btnId ? ' <#' + fileId + '>' : ''))
                    }

                    count++
                }
            });
        console.log(count + ' uploader component(s) found');
    }

    onClick() {
        const elt = window.event.target;
        const fileId = elt.getAttribute("data-file-button-id");
        if (fileId) {
            let file = document.getElementById(fileId);
            if (file) {
                file.click()
            }
        } else {
            console.log('No file input' + (fileId ? ' <#' + fileId + '>' : ''))
        }
    }

    async onUploaded() {
        const elt = window.event.target;

        // Init progress bar
        const progressId = elt.getAttribute("data-progress-id");
        const progressBar = progressId ? document.querySelector('#' + progressId + ' .progress-bar') : null;
        if (progressBar) {
            progressBar.style.display = 'block';
            progressBar.setAttribute("aria-valuenow", 0);
            progressBar.setAttribute("aria-valuemax", elt.files.length);
            progressBar.style.width = "0%";
        } else {
            console.log('No progress bar' + (progressId ? ' <#' + progressId + ' .progress-bar>' : ''))
        }

        // Upload
        let uploadUrl = elt.getAttribute('data-upload-url');
        let maxMb = elt.getAttribute('data-max-file-size');
        if (!maxMb || maxMb.length === 0) {
            maxMb = 1
        }
        for (var i = 0; i < elt.files.length; i++) {
            // Uploading...
            const file = elt.files[i];
            if (file.size <= maxMb * 1024 * 1024) {
                console.log('Uploading ', file);
                const data = new FormData();
                data.append('file', file);
                const response = await fetch(uploadUrl, {
                    method: 'POST',
                    body: data
                });
                if (response.ok || response.status === 0) {
                    console.log("SUCCESS - Uploading " + file.name);
                } else {
                    console.log("FAILED - Uploading " + file.name);
                }
            } else {
                console.log("FAILED - " + file.name + " is too big. size=" + (file.size / (1024 * 1024)) + "Mb - max size=" + maxMb + "Mb");
            }

            // Progress
            let now = i + 1
            let percent = 100 * now / elt.files.length;
            if (progressBar) {
                console.log('Updating the progress bar. now=' + now + ' - percent=' + percent);
                progressBar.setAttribute("aria-valuenow", now);
                progressBar.style.width = percent + "%";
            }
        }

        // Refresh
        koki.widgets.ajaxButton.refresh(elt);
    }
}

/**
 * AjaxButton
 * When this button is clicked:
 *  1. It will execute an action by calling the endpoint defined by attribute "data-action-url"
 *  2. IT will refresh a fragment of the page if the attributes "data-target-id" and "data-refresh-url" are  provided
 *
 * Attributes:
 *   - data-action-confirm: Confirmation message displayed before executing the action. The action is executed if user click OK
 *   - data-action-url: URL of the action to execute. This URL must return the json:
 *      {
 *          success: true | false
 *          error: Error (if success=false)
 *      }
 *   - data-target-id: ID of the element to refresh
 *   - data-refresh-url: URL where to load the data to refresh
 */
class AjaxButtonWidget {
    init() {
        let count = 0;
        let me = this;
        document.querySelectorAll('[data-component-id=ajax-button]')
            .forEach((elt) => {
                    elt.removeEventListener('click', koki.widgets.ajaxButton.onClick);
                    elt.addEventListener('click', koki.widgets.ajaxButton.onClick);

                    count++
                }
            );
        console.log(count + ' ajax-button component(s) found');
    }

    onClick() {
        console.log('onClick()');
        const elt = window.event.target;
        const actionUrl = elt.getAttribute('data-action-url');
        if (actionUrl) {
            // Confirm
            const confirmMsg = elt.getAttribute('data-action-confirm');
            if (confirmMsg && !confirm(confirmMsg)) {
                return
            }

            // Execute
            koki.widgets.ajaxButton._execute(actionUrl, elt);
        } else {
            koki.widgets.ajaxButton.refresh(elt)
        }
    }

    _execute(actionUrl, elt) {
        console.log('Executing action ' + actionUrl);
        fetch(actionUrl)
            .then(response => {
                if (response.ok) {
                    response.json().then(json => {
                        console.log('Executed', json);
                        if (json.success) {
                            koki.widgets.ajaxButton.refresh(elt);
                        } else {
                            alert('Failed: ' + json.error);
                        }
                    });
                } else {
                    console.log('Failed to submit to ' + form.action, response.statusText);
                    alert('Failed');
                }
            });
    }

    refresh(elt) {
        const targetId = elt.getAttribute('data-target-id');
        const refreshUrl = elt.getAttribute('data-refresh-url');
        if (targetId && refreshUrl) {
            console.log('Refreshing #' + targetId + ' from ' + refreshUrl);
            const container = document.getElementById(targetId);
            if (container) {
                fetch(refreshUrl)
                    .then(response => {
                        response.text()
                            .then(html => {
                                container.innerHTML = html;
                                koki.init();
                            })
                    });
            } else {
                console.log('#' + targetId + ' not found in the DOM')
            }
        }
    }
}

/**
 * Ajax checkbox
 * When this button is clicked:
 * 1. It will execute an action by calling the endpoint defined by attribute "data-action-url"
 *
 * Attributes:
 *   - data-action-url: URL of the action to execute. The query parameter "checked" is appended to the query parameters of the URL
 *     This URL must return the json:
 *      {
 *          success: true | false
 *          error: Error (if success=false)
 *      }
 */
class AjaxCheckboxWidget {
    init() {
        let count = 0;
        let me = this;
        document.querySelectorAll('[data-component-id=ajax-checkbox]')
            .forEach((elt) => {
                    elt.removeEventListener('click', koki.widgets.ajaxCheckbox.onClick);
                    elt.addEventListener('click', koki.widgets.ajaxCheckbox.onClick);

                    count++
                }
            );
        console.log(count + ' ajax-checkbox component(s) found');
    }

    onClick() {
        console.log('onClick()');
        const elt = window.event.target;
        const actionUrl = elt.getAttribute('data-action-url');
        if (actionUrl) {
            // Confirm
            const confirmMsg = elt.getAttribute('data-action-confirm');
            if (confirmMsg && !confirm(confirmMsg)) {
                return
            }

            // Execute
            const separator = actionUrl.indexOf('?') > 0 ? '&' : '?';
            const url = actionUrl + separator + 'checked=' + elt.checked
            koki.widgets.ajaxCheckbox._execute(url);
        }
    }

    _execute(actionUrl) {
        console.log('Executing action ' + actionUrl);
        fetch(actionUrl)
            .then(response => {
                if (response.ok) {
                    response.json().then(json => {
                        console.log('Executed', json);
                        if (!json.success) {
                            alert('Failed: ' + json.error);
                        }
                    });
                } else {
                    console.log('Failed to submit to ' + form.action, response.statusText);
                    alert('Failed');
                }
            });
    }
}

/**
 * Button for loading additional data
 */
class LoadMoreWidget {
    init() {
        let count = 0;
        document.querySelectorAll('[data-component-id=load-more]')
            .forEach((elt) => {
                count++
                elt.removeEventListener('click', this.onClick);
                elt.addEventListener('click', this.onClick);
            });
        console.log(count + ' load-more component(s) found');
    }

    onClick() {
        console.log('onLoadMore()');

        const elt = window.event.target;
        console.log('elt', elt);

        const containerId = elt.getAttribute('data-container-id');
        const container = document.querySelector('#' + containerId);
        container.innerHTML = '<div class="text-center"><i class="fas fa-spinner fa-spin"></div>';

        const url = elt.getAttribute('data-url');
        console.log('Loading ' + url);
        fetch(url).then(function (response) {
            response.text().then(function (html) {
                $('#' + containerId).replaceWith(html);
                koki.init();
            });
        });
    }
}

/**
 * Modal widget
 */
class ModalWidget {
    init() {
        let count = 0;
        document.querySelectorAll('[data-component-id=modal]')
            .forEach((elt) => {
                    count++
                }
            );
        console.log(count + ' modal component(s) found');
    }

    open(url, title, open_callback, close_callback) {
        const modal = document.getElementById('koki-modal');
        if (modal) {
            modal.addEventListener('hidden.bs.modal', close_callback);

            fetch(url)
                .then(response => {
                    if (response.ok) {
                        response.text()
                            .then(html => {
                                // Set the body
                                document.getElementById("koki-modal-body").innerHTML = html;
                                if (open_callback) {
                                    open_callback();
                                }

                                // Set the title
                                document.getElementById("koki-modal-title").innerHTML = title;

                                // Show
                                const modal = new bootstrap.Modal('#koki-modal');
                                modal.show();
                            })
                    } else {
                        console.log('Unable to fetch the modal', response.text());
                    }
                });
        }
    }

    close() {
        document.querySelector('#koki-modal .btn-close').click();
    }
}

/**
 * Map widget based on https://leafletjs.com
 *
 * Attributes:
 *  - id: ID of the map
 *  - data-latitude, data-longitude: Lat/Long of the center of the map
 *  - data-zoom: Initial zoom of the map viewport (Default: 10)
 *  - data-max-zoom: Initial zoom of the map viewport (Default: 20)
 *  - data-show-marker: (true|false) Show marker in the center of the map? (Default: false)
 *  - data-on-click: Name of the callback called when user click. The callback will receive mouse event. See https://leafletjs.com/reference.html#mouseevent
 *  - data-on-ready: Name of the callback called when map is ready
 */
class MapWidget {
    init() {
        let count = 0;
        document.querySelectorAll('[data-component-id=map]')
            .forEach((elt) => {
                const id = elt.getAttribute("id");
                if (id) {
                    const latitude = elt.getAttribute("data-latitude");
                    const longitude = elt.getAttribute("data-longitude");
                    const zoom = elt.getAttribute("data-zoom");
                    const center = latitude && longitude ? [latitude, longitude] : null;
                    const mapOptions = {
                        center: center,
                        zoom: zoom && zoom.length > 0 ? zoom : 10,
                    };

                    // Kill previous instance - see https://stackoverflow.com/questions/19186428/refresh-leaflet-map-map-container-is-already-initialized
                    var container = L.DomUtil.get(id);
                    if (container != null) {
                        container._leaflet_id = null;
                    }

                    // Create new map instance
                    let map = L.map(id, mapOptions);

                    const maxZoom = elt.getAttribute("data-max-zoom");
                    let layer = L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
                        maxZoom: maxZoom && maxZoom.length > 0 ? zoom : 20,
                        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
                    });
                    map.addLayer(layer);

                    const showMarker = elt.getAttribute("data-show-marker");
                    if (showMarker === "true") {
                        let marker = L.marker([latitude, longitude]);
                        map.addLayer(marker);
                    }

                    setTimeout(
                        function () {
                            map.invalidateSize(true);

                            // on-click
                            let onclick = elt.getAttribute('data-on-click');
                            if (onclick) {
                                map.on('click', function (evt) {
                                    eval(onclick)(evt);
                                });
                            }

                            // on-ready
                            let onReady = elt.getAttribute('data-on-ready');
                            if (onReady) {
                                eval(onReady)(id, map);
                            }
                        },
                        500);
                    count++
                }
            });
        console.log(count + ' map component(s) found');
    }
}

class IntlTel {
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

/**
 * Widget container
 */
class KokiWidgets {
    constructor() {
        this.address = new AddressWidget();
        this.ajaxButton = new AjaxButtonWidget();
        this.ajaxCheckbox = new AjaxCheckboxWidget();
        this.ajaxFragment = new AjaxFragmentWidget();
        this.loadMore = new LoadMoreWidget();
        this.map = new MapWidget();
        this.modal = new ModalWidget();
        this.uploader = new UploaderWidget();
        this.intlTel = new IntlTel();
    }
}

/**
 * Main Class
 */
class Koki {
    constructor() {
        this.widgets = new KokiWidgets();
    }

    init() {
        console.log('init()');
        this.widgets.address.init();
        this.widgets.ajaxButton.init();
        this.widgets.ajaxCheckbox.init();
        this.widgets.ajaxFragment.init();
        this.widgets.loadMore.init();
        this.widgets.map.init();
        this.widgets.modal.init();
        this.widgets.uploader.init();
        this.widgets.intlTel.init();
    }
}

const koki = new Koki();
document.addEventListener(
    'DOMContentLoaded',
    function () {
        koki.init();
    },
    false
);

