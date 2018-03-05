import {bindable, inject, customElement} from 'aurelia-framework';
import {EventAggregator} from 'aurelia-event-aggregator';
import Nav from "../_/common/navigation";
import {trace} from "../_/common/functions";
import LocalStorage from "../_/common/localstorage";
import User from "../_/data/user";

@inject(Nav, EventAggregator, LocalStorage, User)
@customElement('header')
export class Section {
    public nav:Nav;
    public eventAggregator:EventAggregator;
    public localStorage:LocalStorage;
    public user:User;
    public showFlags = false;
    public interactWithDoc = false;
    public langSelected = "EN";
    private timeout;
    constructor(Nav, EventAggregator, LocalStorage, User) {
        this.nav = Nav;
        this.eventAggregator = EventAggregator;
        this.localStorage = LocalStorage;
        this.user = User;
    }
    attached(){
        this.langSelected = this.localStorage.getItem("lang") || "EN";
        window.document.addEventListener("click", () => {
            if (!this.interactWithDoc) {
                return;
            }
            this.showFlags = false;
            this.interactWithDoc = false;
            clearTimeout(this.timeout);
        });
        trace(this.user.isAuthenticatedOrRemembered);
    }
    openHome() {
        this.nav.to("");
    }
    goto(place) {
        this.nav.to(place);
    }
    open(place) {
        this.nav.to("");
        setTimeout(()=>{
            const position = $(`.section${place}`).offset().top;
            trace("animating position: " + position);
            $("HTML, BODY").animate({ scrollTop: position }, 350);
        }, 32);
    }
    openFlags() {
        this.showFlags = !this.showFlags;
        clearTimeout(this.timeout);
        this.timeout = setTimeout(()=>{this.interactWithDoc = true},200);
    }
    changeLangue(lang) { 
        this.langSelected = lang;
        this.eventAggregator.publish("change_lang_trigger", lang); 
    }
}
