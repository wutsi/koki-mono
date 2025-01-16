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

function koki_ready() {
    _koki_load_widgets();
    _koki_tabs_lazyload();
    _koki_activate_current_tab();
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

document.addEventListener('DOMContentLoaded', koki_ready, false);
