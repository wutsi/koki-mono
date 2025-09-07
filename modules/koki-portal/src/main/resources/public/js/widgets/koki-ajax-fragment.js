/**
 * Widget for loading content asynchronously
 *
 * Attributes
 *  - data-url: URL of the fragment to load
 *  - data-callback: The callback function to invoke after loading the the widget completed
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
                                eval(callback)();
                            }
                        });
                    });

                    count++
                }
            });
        console.log(count + ' ajax-fragment component(s) found');
    }
}

document.addEventListener('DOMContentLoaded', function () {
        const widget = new AjaxFragmentWidget();
        koki.w['ajaxFragment'] = widget;
        widget.init();
    }
);

