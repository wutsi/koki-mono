/**
 * ListItemWidget
 * Container of items. When an item is selected, it will be assigned the class 'active'
 *
 * Attributes:
 *   - data-item-class: Classname of the item
 *   - data-on-click: Callback to invoke when clicking on an item. The parameter of the callback:
 *      - elt: DOM element selected
 */
class ListItemWidget {
    init(root) {
        let count = 0;
        const base = root ? root : document;
        base.querySelectorAll('[data-component-id=list-item]')
            .forEach((elt) => {
                    // Configure
                    const clazz = elt.getAttribute('data-item-class');
                    const items = elt.querySelectorAll('.' + clazz);
                    items.forEach((item) => {
                        item.removeEventListener('click', this.on_click);
                        item.addEventListener('click', this.on_click);
                    });
                    count++;

                    // Select the 1st item
                    if (items.length > 0) {
                        items[0].click();
                    }
                }
            );
        console.log(count + ' list-item component(s) found');
    }

    on_click() {
        const root = window.event.target.closest('[data-component-id=list-item]');
        const clazz = root.getAttribute('data-item-class');

        root.querySelectorAll('.' + clazz)
            .forEach((elt) => {
                elt.classList.remove('active')
            });

        const elt = window.event.target.closest('.' + clazz);
        elt.classList.add('active');

        let onclick = root.getAttribute('data-on-click');
        if (onclick) {
            eval(onclick)(elt);
        }
    }
}

document.addEventListener('DOMContentLoaded', function () {
        const widget = new ListItemWidget();
        koki.w['listItem'] = widget;
        widget.init();
    }
);

