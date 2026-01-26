class KokiTrackWidget {
    init() {
        const elts = document.querySelectorAll('[data-component-id=track]');
        console.log(elts.length + ' element(s) tracked');

        elts.forEach((elt) => {
            this.add_event_listener(elt);
        });
    }

    add_event_listener(elt) {
        elt.addEventListener('click', kokiTracking.on_click);
        elt.addEventListener('click', kokiTracking.on_click);
    }

    track(event, productId, productType, recipientId, component, value, rank) {
        console.log('track()', event, productId, value, component, rank);

        const data = {
            time: new Date().getTime(),
            event: event,
            component: component,
            productId: productId,
            productType: productType,
            value: (value ? value : null),
            page: document.head.querySelector("[name=wutsi\\:page_name]").content,
            hitId: document.head.querySelector("[name=wutsi\\:hit_id]").content,
            ua: navigator.userAgent,
            url: window.location.href,
            referrer: document.referrer,
            rank: rank,
            recipientId: recipientId,
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
            const event = elt.getAttribute("data-event");
            const productId = elt.getAttribute("data-product-id");
            const productType = elt.getAttribute("data-product-type");
            const component = elt.getAttribute("data-component");
            const value = elt.getAttribute("data-value");
            const rank = elt.getAttribute("data-rank");
            const recipientId = elt.getAttribute("data-recipient-id");
            kokiTracking.track(event, productId, productType, recipientId, component, value, rank)
        }
    }
}

document.addEventListener('DOMContentLoaded', function () {
        const widget = new KokiTrackWidget();
        koki.w['track'] = widget;
        widget.init();
    }
);
