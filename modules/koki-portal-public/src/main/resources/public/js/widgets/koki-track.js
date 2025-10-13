class KokiTrackWidget {
    init() {
        const elts = document.querySelectorAll('[koki-track]');
        console.log(elts.length + ' element(s) tracked');

        elts.forEach((elt) => {
            this.add_event_listener(elt);
        });
    }

    add_event_listener(elt) {
        elt.addEventListener('click', kokiTracking.on_click);
        elt.addEventListener('click', kokiTracking.on_click);
    }

    track(event, productId, component, value, rank) {
        console.log('track()', event, productId, value, component, rank);

        const data = {
            time: new Date().getTime(),
            event: event,
            component: component,
            productId: productId,
            value: (value ? value : null),
            page: document.head.querySelector("[name=wutsi\\:page_name]").content,
            hitId: document.head.querySelector("[name=wutsi\\:hit_id]").content,
            ua: navigator.userAgent,
            url: window.location.href,
            referrer: document.referrer,
            rank: rank
        };

        // Track
        fetch(
            '/track',
            {
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            }
        );
    }

    on_click() {
        console.log('on_click()');

        var elt = event.target;
        while (elt && !elt.hasAttribute("koki-track")) { // Find hyperlink
            elt = elt.parentElement;
        }
        if (elt != null) {
            const productId = elt.getAttribute("koki-track-product-id");
            const component = elt.getAttribute("koki-track-component");
            const value = elt.getAttribute("koki-track-value");
            const rank = elt.getAttribute("koki-track-rank");
            kokiTracking.track('CLICK', productId, component, value, rank)
        }
    }
}

document.addEventListener('DOMContentLoaded', function () {
        const widget = new KokiTrackWidget();
        koki.w['track'] = widget;
        widget.init();
    }
);
