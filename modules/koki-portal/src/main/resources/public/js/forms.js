function koki_configure_form() {
    const form = document.querySelector('form');
    if (form) {
        console.log('Registering the validation to ' + form);
        form.noValidate = true;
        form.addEventListener('submit', _koki_validate_form);
    } else {
        console.log('Oopsss... no form found!');
    }
}

function _koki_validate_form(e) {
    console.log('validating form...');

    const form = e.target;
    const success1 = form.checkValidity();
    const success2 = _koki_validate_all_checkbox_groups();
    if (!success1 || !success2) {
        e.preventDefault();
    }
}

function _koki_validate_all_checkbox_groups() {
    let result = true;
    document.querySelectorAll('.checkbox-container,.radio-container')
        .forEach((group) => {
            const name = group.querySelector('input').getAttribute('name');
            if (group.hasAttribute('required')) {
                const size = group.querySelectorAll('input[type=checkbox]:checked,input[type=radio]:checked').length;
                if (size > 0) {
                    console.log(name + ' - valid. ' + size + ' item(s) selected');
                    group.classList.remove('user-invalid');
                    group.removeEventListener('click', _koki_validate_all_checkbox_groups);
                } else {
                    console.log(name + ' - NOT valid. ' + size + ' item(s) selected');
                    group.classList.add('user-invalid');
                    group.addEventListener('click', _koki_validate_all_checkbox_groups);
                    result = false;
                }
            }
        });

    console.log(' checkbox validation: ' + result);
    return result;
}

document.addEventListener('DOMContentLoaded', koki_configure_form, false);
