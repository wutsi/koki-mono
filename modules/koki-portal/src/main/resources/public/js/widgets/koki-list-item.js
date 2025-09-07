/**
 * ListItemWidget
 * Container of items. When an item is selected, it will be assigned the class 'active'
 *
 * Attributes:
 *   - data-item-class: Classname of the item
 */
class ListItemWidget {
    init() {
        let count = 0;
        document.querySelectorAll('[data-component-id=list-item]')
            .forEach((elt) => {
                    const clazz = elt.getAttribute('data-item-class');
                    if (clazz) {
                        elt.querySelectorAll('.' + clazz).forEach((item) => {
                            item.removeEventListener('click', this.on_click);
                            item.addEventListener('click', this.on_click);
                        });
                        count++
                    }
                }
            );
        console.log(count + ' ajax-loader component(s) found');
    }

    on_click() {
        const root = window.event.target.closest('[data-component-id=list-item]');
        const clazz = root.getAttribute('data-item-class');

        root.querySelectorAll('.' + clazz)
            .forEach((elt) => {
                elt.classList.remove('active')
            });

        const active = window.event.target.closest('.' + clazz);
        active.classList.add('active');
    }
}

document.addEventListener('DOMContentLoaded', function () {
        const widget = new AjaxLoaderWidget();
        koki.w['listItem'] = widget;
        widget.init();
    }
);

