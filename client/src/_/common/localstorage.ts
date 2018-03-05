export default class LocalStorage {
    constructor(){
        // if (!instance) instance = this;
        // return instance;
    }
    exists() {
        let b = true;
        try {
            var uid = new Date;
            window.localStorage.setItem(uid.toString(), uid.toString());
            var fail = window.localStorage.getItem(uid.toString()) != uid.toString();
            window.localStorage.removeItem(uid.toString());
            
        } catch (exception) { b = false;}

        return b;
    };
    setItem(key, val) {
        if(this.exists()){
            window.localStorage.setItem(key, val);
        }
    };
    removeItem(key) {
        if(this.exists()){
            window.localStorage.removeItem(key);
        }
    };
    getItem(key) {
        if(this.exists()){
            return window.localStorage.getItem(key);
        }
        return null;
    };
}