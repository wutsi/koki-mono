/**
 * LazyBootstrapTabs
 *
 * Attributes:
 *   - data-target-id: ID of the element to refresh
 *   - data-url: URL where to load the data to refresh
 */
class LazyBootstrapTabsWidget {
    init(root) {
        const base = root ? root : document;
        const tabs = base.querySelectorAll('button[data-bs-toggle="pill"]');
        if (tabs.length > 0) {
            const me = this;
            for (let i = 0; i < tabs.length; i++) {
                tabs[i].addEventListener('show.bs.tab', function (event) {
                    const tabId = event.target.getAttribute('data-bs-target');
                    if (tabId) {
                        me.on_tab_selected(tabId.substring(1));
                    }
                })
            }

            // Load content of active lazy tab
            setTimeout(
                function () {
                    me.activate_default_tab()
                },
                100
            )
        }

        console.log(tabs.length + ' lazy-bootstrap-tabs component(s) found');
    }

    on_tab_selected(id) {
        console.log('on_tab_selected()', id);

        const content = document.getElementById(id);
        const url = content.getAttribute('data-url');
        if (url) {
            koki.load(url, id);
        }
    }

    activate_default_tab() {
        const urlParams = new URLSearchParams(window.location.search);
        const tab = urlParams.get('tab');
        if (tab) {
            console.log('Current tab', tab);
            const tabDiv = document.getElementById("pills-" + tab + "-tab");
            if (tabDiv) {
                tabDiv.click();
            }
        } else {
            const active = document.querySelector('.tab-content > .active');
            if (active != null) {
                const id = active.getAttribute("id");
                if (id) {
                    this.on_tab_selected(id);
                }
            }
        }
    }
}

document.addEventListener('DOMContentLoaded', function () {
        const widget = new LazyBootstrapTabsWidget();
        koki.w['lazyBootstrapTabs'] = widget;
        widget.init();
    }
);

