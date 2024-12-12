function pad(num, size) {
    num = num.toString();
    while (num.length < size) num = "0" + num;
    return num;
}

function generate_case_id(type) {
    let now = new Date().getTime();
    let random = Math.random() * 1000;
    return now + "-" + type + "-" + pad(random, 3);
}

let id = generate_case_id(type);
