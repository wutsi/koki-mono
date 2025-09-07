function koki_account_address_same_changed() {
    console.log('koki_account_address_same_changed()');

    const same = (document.getElementById('billingSameAsShippingAddress').value === 'true');
    document.querySelectorAll('.billing-address select, .billing-address input').forEach((elt) => {
        if (same) {
            elt.setAttribute("disabled", "disabled");
        } else {
            elt.removeAttribute("disabled");
        }
    });
    document.getElementById('billingCountry').required = !same;
    document.getElementById('billingCityId').required = !same;
}
