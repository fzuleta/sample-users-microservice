import {trace} from "../common/functions"; 
export default class User {
    expectedToken:string = ""
    isRemembered:boolean = false
    isAuthenticated:boolean = false
    isAuthenticatedOrRemembered:boolean = false
    firstTime:boolean = false  
    reference:string=""
    me: any; 
    twofactorEnabled = false;

    constructor(){
        this.me = {}
    }
    setMe(o){
        if (o.me) {
            this.me = o.me 
            this.reference = this.me.reference; 
            this.twofactorEnabled = this.me.twofactorEnabled;
        }
    } 
}