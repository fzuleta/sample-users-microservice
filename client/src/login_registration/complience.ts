import {bindable, inject, customElement} from 'aurelia-framework';
import {EventAggregator} from 'aurelia-event-aggregator';
import {I18N} from 'aurelia-i18n'; 
import Nav from "../_/common/navigation";
import {trace, validPassword} from "../_/common/functions";

@inject(Nav, EventAggregator, I18N)
export default class Section {
    eventAggregator:EventAggregator;
    nav: Nav;
    sent = false;
    hasErrors = false;
    errors;
    password0 = "";
    password1 = "";
    code="";
    constructor(Nav, EventAggregator) { 
        this.eventAggregator = EventAggregator
        this.nav = Nav;
    }
    activate(params, routeConfig) {
        this.code = (params && params.reference) ? decodeURIComponent(params.reference) : "";
        trace(this.code)
    }
    recover(){
        this.errors = {}; 
        if (!validPassword(this.password0, this.password1)) { this.errors["password"] = true; } 
        this.hasErrors = Object.keys(this.errors).length > 0;
        if (this.hasErrors) { return; }

        trace("All good!");
        this.sent = true;
    }
}
