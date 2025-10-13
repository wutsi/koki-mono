/**
 * Main Class
 */
class Koki {
    constructor() {
        this.w = {};
    }

    init(root) {
        console.log('init()', root);
        for (const [key, value] of Object.entries(this.w)) {
            value.init(root);
        }
    }
}

const koki = new Koki();

