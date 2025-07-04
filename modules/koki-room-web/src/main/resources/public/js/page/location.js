class KokiLocationPage {
    map = null;

    init() {
        // Event handler
        document.querySelector('#view-switcher-map a')?.addEventListener('click', kokiLocationPage.on_view_switched_to_map);
        document.querySelector('#view-switcher-list a')?.addEventListener('click', kokiLocationPage.on_view_switched_to_list);

        // Track impression
        let ids = '';
        let rooms = document.querySelectorAll('.room');
        rooms.forEach((elt) => {
            const id = elt.getAttribute("data-id");
            if (id && id.length > 0) {
                if (ids.length > 0) ids = ids + '|'
                ids = ids + id;
            }
        });
        if (ids.length > 0) {
            this._track(ids);
        }
    }

    on_map_ready(id, mmap) {
        console.log('on_map_ready()', id, mmap);
        this.map = mmap;

        // Load the marker in the city
        const cityId = document.getElementById('map').getAttribute('data-city-id');
        fetch('/l/' + cityId + '/map')
            .then((response) => {
                response.json().then((json) => {
                    console.log(json);
                    for (var i = 0; i < json.length; i++) {
                        let item = json[i];
                        let marker = L.marker(
                            [item.latitude, item.longitude],
                            {
                                roomId: item.id,
                                icon: L.divIcon({
                                    className: 'map-room-icon',
                                    html: '<span>' + item.price + '</span>',
                                }),
                            }
                        );
                        marker.on('click', () => {
                            kokiLocationPage.on_marker_click(item.id, marker);
                        });
                        this.map.addLayer(marker);
                    }
                });
            });
    }

    on_marker_click(roomId, marker) {
        console.log('open_marker_popup()', roomId, marker);

        fetch(`/l/map/rooms/${roomId}`)
            .then((response) => {
                response.json().then((json) => {
                    let location = '';
                    if (json.neighborhood) {
                        location = json.neighborhood.name + ', ' + json.address.city.name;
                    } else {
                        location = json.address.city.name;
                    }

                    const html = `<div class="map-room-card">
                            <a href="${json.url}" target="_blank">
                                <div>
                                    <img src="${json.heroImage.contentUrl}" />
                                </div>
                                <div class="map-room-card-details">
                                    <div class="price text-primary">${json.displayPrice.text}</div>
                                    <ul class="breadcrumb">
                                        <li>
                                            <i class="fa-solid fa-bed"></i>
                                            <span>${json.numberOfRooms}</span>
                                        </li>
                                        <li>
                                            <span>-</span>
                                            <i class="fa-solid fa-bath"></i>
                                            <span>${json.numberOfBathrooms}</span>
                                        </li>
                                    </ul>
                                    <div class="address">${location}</div>
                                </div>
                            </a>
                        </div>`;
                    marker.bindPopup(html);
                    marker.openPopup();

                    this._track(roomId, 'map');
                });
            });
    }

    on_view_switched_to_map() {
        console.log('on_view_switched_to_map()');
        document.getElementById('map-container').style.display = 'block';
        document.getElementById('room-list-container').style.display = 'none';
        document.getElementById('view-switcher-map').classList.add('hidden');
        document.getElementById('view-switcher-list').classList.remove('hidden');
        kokiLocationPage.map.invalidateSize(true);
    }

    on_view_switched_to_list() {
        console.log('on_view_switched_to_list()');
        document.getElementById('map-container').style.display = 'none';
        document.getElementById('room-list-container').style.display = 'block';
        document.getElementById('view-switcher-map').classList.remove('hidden');
        document.getElementById('view-switcher-list').classList.add('hidden');
    }

    on_load_more() {
        console.log('on_load_more()');
        let elt = document.getElementById('track-room-ids');
        if (elt) {
            let ids = elt.getAttribute("data-room-ids")
            this._track(ids);
            elt.parentNode.removeChild(elt); // Tracking complete, remove the element from the DOM
        }
    }

    _track(ids, component) {
        kokiTracking.track('IMPRESSION', ids, component);
    }
}

const kokiLocationPage = new KokiLocationPage();
document.addEventListener(
    'DOMContentLoaded',
    () => {
        kokiLocationPage.init();
    }
);
