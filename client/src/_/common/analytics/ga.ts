import {trace} from "../functions";

const id = "UA-70822515-5";
let userSet = false;
let gaTimeout;
export function init(){
    try {
        if (!window["ga"]) {
            gaTimeout = setTimeout(()=>{
                init();
            }, 250);
            return;
        }
        window["ga"]('create', id, 'auto');
        window["ga"]('send', 'pageview');
        trace("starting the GA journey");
    } catch (e){
        trace(e);
    }
}
export function setUser(user_id){
    try {
        if (!userSet) window["ga"]('set', 'userId', user_id);
        userSet = true;
    } catch (e){
        trace(e);
    }
}
