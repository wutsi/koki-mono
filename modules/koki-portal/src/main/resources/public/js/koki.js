function koki_ready() {
    _koki_load_widgets();
    _koki_tabs_lazyload();
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
    const tabs = document.querySelectorAll('button[data-bs-toggle="pill"]');
    for (let i = 0; i < tabs.length; i++) {
        tabs[i].addEventListener('show.bs.tab', function (event) {
            const content = document.querySelector(tabs[i].getAttribute('data-bs-target'));
            const url = content.getAttribute('data-url');
            if (url) {
                fetch(url)
                    .then(function (response) {
                        response.text()
                            .then(function (text) {
                                content.innerHTML = text
                            });
                    });
            }
        })
    }
}

document.addEventListener('DOMContentLoaded', koki_ready, false);
