function _koki_configure_form() {
    const form = document.querySelector('form');
    if (form) {
        console.log('Configuring form validation');
        form.noValidate = true;
        form.addEventListener('submit', _koki_validate_form);
    }
}

function _koki_validate_form(e) {
    console.log('validating form...');

    const form = e.target;
    const valid1 = form.checkValidity();
    const valid2 = _koki_validate_all_checkbox_groups();
    const valid3 = _koki_validate_all_file();
    if (!valid1 || !valid2 || !valid3) {
        console.log('form valid...');
        e.preventDefault();
    } else {
        console.log('form not valid...');
    }
}

function _koki_validate_all_checkbox_groups() {
    console.log('Validating checkboxes');
    let result = true;
    document.querySelectorAll('.checkbox-container, .radio-container')
        .forEach((group) => {
            const name = group.querySelector('input').getAttribute('name');
            if (group.hasAttribute('required')) {
                const size = group.querySelectorAll('input[type=checkbox]:checked,input[type=radio]:checked').length;
                if (size > 0) {
                    // console.log(name + ' - valid. ' + size + ' item(s) selected');
                    group.classList.remove('user-invalid');
                    group.removeEventListener('click', _koki_validate_all_checkbox_groups);
                } else {
                    // console.log(name + ' - NOT valid. ' + size + ' item(s) selected');
                    console.log('...' + name + ' NOT VALID');
                    group.classList.add('user-invalid');
                    group.addEventListener('click', _koki_validate_all_checkbox_groups);
                    result = false;
                }
            }
        });

    console.log('...checkbox validation: ' + result);
    return result;
}

function _koki_validate_all_file() {
    let result = true;
    console.log('Validating files');
    document.querySelectorAll('.file-upload-container .btn-upload')
        .forEach((elt) => {
            const rel = elt.getAttribute("rel");
            const input = document.querySelector("input[name=" + rel + "]");
            if (input.hasAttribute('required')) {
                const value = input.value;
                if (value === '') {
                    console.log('...' + rel + ' NOT VALID');
                    input.parentElement.classList.add('user-invalid');
                    result = false;
                } else {
                    input.parentElement.classList.remove('user-invalid');
                }
            }
        });

    console.log('...file validation: ' + result);
    return result;

}

function _koki_configure_upload() {
    console.log('Configuring file upload');
    document.querySelectorAll('.file-upload-container .btn-upload')
        .forEach((elt) => {
            const rel = elt.getAttribute("rel");
            console.log('... file upload: ' + rel);

            elt.addEventListener('click', () => {
                console.log('Uploading file ' + rel);
                document.querySelector("[name=" + rel + "-file]").click();
            });
        });

    document.querySelectorAll('.file-upload-container input[type=file]')
        .forEach((elt) => {
            elt.addEventListener('change', () => {
                _koki_upload(elt);
            });
        });

    document.querySelectorAll('.file-upload-container .btn-close')
        .forEach((elt) => {
            elt.addEventListener('click', () => {
                _koki_remove_file(elt);
            });
        });
}

async function _koki_upload(elt) {
    let uploadUrl = elt.getAttribute("data-upload-url");
    const rel = elt.getAttribute("rel");

    const token = _koki_get_cookie("__atk");
    if (token) {
        uploadUrl = uploadUrl + '&access-token=' + token;
    }
    const data = new FormData();
    data.append('file', elt.files[0]);
    const response = await fetch(uploadUrl, {
        method: 'POST',
        body: data
    });

    if (response.ok || response.status === 0) {
        const json = await response.json();
        const re = /(?:\.([^.]+))?$/;
        const ext = re.exec(json.name)[1];

        document.querySelector("[name=" + rel + "]").setAttribute('value', json.id);
        document.querySelector("[data-name=" + rel + "-filename]").innerHTML =
            "<a class='filename' href='/files/" + json.id + "/" + json.name + "'>" +
            "<span class='fiv-viv fiv-icon-" + ext + "'></span>&nbsp;" +
            json.name +
            "</a>" +
            "<button class='btn-close' type='button' rel='" + rel + "' name='" + rel + "-close'></button>";

        const close = document.querySelector("[name=" + rel + "-close]");
        close.addEventListener('click', () => {
            _koki_remove_file(close);
        });
    } else {
        console.log('FAILED', response.headers);
        alert('Upload failed!!');
    }

}

function _koki_get_cookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}

function _koki_remove_file(elt) {
    const rel = elt.getAttribute("rel");

    document.querySelector("[name=" + rel + "]").setAttribute('value', '');
    document.querySelector("[data-name=" + rel + "-filename]").innerHTML = '';
}

function _koki_configure() {
    _koki_configure_form();
    _koki_configure_upload();
}

document.addEventListener('DOMContentLoaded', _koki_configure, false);
