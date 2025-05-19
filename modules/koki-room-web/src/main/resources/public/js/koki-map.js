/**
 * Map widget based on https://leafletjs.com
 *
 * Attributes:
 *  - id: ID of the map
 *  - data-latitude, data-longitude: Lat/Long of the center of the map
 *  - data-zoom: Initial zoom of the map viewport (Default: 10)
 *  - data-max-zoom: Initial zoom of the map viewport (Default: 20)
 *  - data-show-marker: Show marker in the center of the map? (Default: false)
 *  - data-on-click: Name of the callback called when user click. The callback will receive mouse event. See https://leafletjs.com/reference.html#mouseevent
 *  - data-on-ready: Name of the callback called when map created. The callback will receive the id and map object
 */
class KokiMapWidget {
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
                    map.scrollWheelZoom.disable();
                    map.touchZoom.disable();

                    const showMarker = elt.getAttribute("data-show-marker");
                    if (showMarker) {
                        let marker = L.marker([latitude, longitude]);
                        map.addLayer(marker);
                    }

                    let onClick = elt.getAttribute('data-on-click');
                    if (onClick) {
                        map.on('click', function (evt) {
                            eval(onClick)(evt);
                        });
                    }

                    let onReady = elt.getAttribute('data-on-ready');
                    if (onReady) {
                        eval(onReady)(id, map);
                    }

                    setTimeout(
                        function () {
                            map.invalidateSize(true);
                        },
                        1000
                    );
                    count++
                }
            });
        console.log(count + ' map component(s) found');
    }
}

const kokiMap = new KokiMapWidget();
document.addEventListener(
    'DOMContentLoaded',
    function () {
        kokiMap.init();
    },
    false
);

