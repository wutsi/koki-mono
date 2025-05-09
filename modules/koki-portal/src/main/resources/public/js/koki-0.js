document.addEventListener('DOMContentLoaded', koki_ready, false);

function koki_ready() {
    _koki_load_widgets();
    _koki_tabs_lazyload();
    _koki_activate_current_tab();
    _init_modal();
}

async function _koki_load_widgets() {
    const widgets = document.querySelectorAll('.widget-container');
    console.log(widgets.length + ' widget(s)');
    for (let i = 0; i < widgets.length; i++) {
        const widget = widgets[i];
        const url = widget.getAttribute("data-url");
        if (url) {
            const response = await fetch(url);
            if (response.ok) {
                widget.innerHTML = await response.text();
            } else {
                console.log('Error while loading ' + url + ' - status=' + response.status);
            }
        }
    }
}

function _koki_tabs_lazyload() {
    let tabs = document.querySelectorAll('button[data-bs-toggle="pill"]');
    for (let i = 0; i < tabs.length; i++) {
        // Load content when lazy tab is shows
        console.log('Listening to tab: ' + tabs[i].getAttribute('data-bs-target'));
        tabs[i].addEventListener('show.bs.tab', function (event) {
            const tabId = event.target.getAttribute('data-bs-target');
            console.log('Tab' + tabId + ' selected');
            if (tabId) {
                _koki_tabs_load(tabId)
            }
        })
    }

    // Load content of active lazy tab
    tabs = document.querySelectorAll('.tab-content .tab-pane');
    for (let i = 0; i < tabs.length; i++) {
        if (tabs[i].classList.contains("active")) {
            if (tabs[i].id) {
                console.log('Tab#' + tabs[i].id + ' is active');
                _koki_tabs_load("#" + tabs[i].id)
            }
        }
    }
}

function _koki_tabs_load(tabSelector) {
    const content = document.querySelector(tabSelector);
    const url = content.getAttribute('data-url');
    if (url) {
        console.log("Lazy loading tab: " + tabSelector);
        fetch(url)
            .then(function (response) {
                response.text()
                    .then(function (text) {
                        content.innerHTML = text
                        if (koki) {
                            koki.init();
                        }
                    });
            });
    }
}

function _koki_activate_current_tab() {
    const urlParams = new URLSearchParams(window.location.search);
    const tab = urlParams.get('tab');
    if (tab) {
        console.log('Current tab', tab);
        const tabDiv = document.getElementById("pills-" + tab + "-tab");
        if (tabDiv) {
            tabDiv.click();
        }
    }
}


/*========== MODAL ================*/
/**
 * Global variable that represent the callback to call when modal closed
 */
var __koki_modal_on_close;

function _init_modal() {
    const modal = document.getElementById('koki-modal');
    if (modal) {
        console.log('Configure modal');
        document.getElementById('koki-modal')
            .addEventListener('hidden.bs.modal', event => {
                if (__koki_modal_on_close) {
                    __koki_modal_on_close();
                }
            });
    }
}

/**
 * Open the modal
 * @param title Title of the modal
 * @param url URL of the modal content
 * @param open_callback function to call after the modal is opened
 * @param close_callback function to call after the modal is closed
 */
function koki_modal_open(title, url, open_callback, close_callback) {
    console.log('Loading modal content from ' + url);

    __koki_modal_on_close = close_callback;
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

/**
 * Close the modal
 */
function koki_modal_close() {
    document.querySelector('#koki-modal .btn-close').click();
}

/**
 * Load more data
 * @param containerId ID of the container
 */
function koki_load_more(containerId) {
    const container = document.querySelector('#' + containerId);
    const button = container.querySelector('a');
    const url = button.getAttribute('data-url');

    container.innerHTML = '<div class="text-center"><i class="fas fa-spinner fa-spin"></div>';
    fetch(url).then(function (response) {
        response.text().then(function (html) {
            console.log('Replacing #' + containerId + ' with ', html);
            $('#' + containerId).replaceWith(html);
        });
    });
}

/**
 * Configure the address editor
 * @param countryId - ID of the country element
 * @param cityId - ID of the city element
 */
function koki_address_editor(countryId, cityId) {
    const country = document.getElementById(countryId).value;
    $('#' + countryId).select2();
    $('#' + countryId).on('select2:select', function (e) {
        console.log('country changed....');
        $('#' + cityId).val('').trigger('change');
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
            minimumInputLength: 3,
        }
    );
}

/**
 * Filter rows from a table
 * @param inputId - ID of the search input
 * @param tableId - ID of the table
 * @param columnIndex - ID of the colum where to search
 */
function koki_table_filter(inputId, tableId, columnIndex) {
    // Declare variables
    const filter = document.getElementById(inputId).value.toUpperCase();
    const table = document.getElementById(tableId);
    const tr = table.getElementsByTagName("tr");

    for (var i = 0; i < tr.length; i++) {
        var td = tr[i].getElementsByTagName("td")[columnIndex];
        if (td) {
            var txtValue = td.textContent || td.innerText;
            if (txtValue.toUpperCase().indexOf(filter) > -1) {
                tr[i].style.display = "";
            } else {
                tr[i].style.display = "none";
            }
        }
    }
}

