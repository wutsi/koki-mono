function track(event, productId, component, value) {
    console.log('track()', event, productId, value, component);

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
