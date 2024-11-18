function koki_ready() {
    _koki_load_widgets();
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

document.addEventListener('DOMContentLoaded', koki_ready, false);
