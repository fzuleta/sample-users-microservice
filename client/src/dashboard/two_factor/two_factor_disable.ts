import {bindable, inject, customElement} from 'aurelia-framework';
import {EventAggregator} from 'aurelia-event-aggregator';
import {I18N} from 'aurelia-i18n'; 
import Nav from "../../_/common/navigation";
import Api from "../../_/connect/api";
import Alerts from "../../_/common/alerts";
import {trace, validPassword} from "../../_/common/functions";

@inject(Nav, EventAggregator, I18N, Api, Alerts)
export default class Section {
    eventAggregator:EventAggregator;
    nav: Nav;  
    i18n: I18N;  
    api: Api;  
    alerts: Alerts;  
    password = "";
    hasErrors = false;
    errors;
    sent = false;
    constructor(Nav, EventAggregator, I18N, API, Alerts) { 
        this.eventAggregator = EventAggregator
        this.nav = Nav;
        this.i18n = I18N;
        this.api = API;
        this.alerts = Alerts;
    }
    activate(params, routeConfig) {

    }
    disable(){
        this.errors = {}; 
        if (!validPassword(this.password, this.password)) { this.errors["password"] = true; } 
        this.hasErrors = Object.keys(this.errors).length > 0;
        if (this.hasErrors) { return; }

        this.eventAggregator.publish('setIsLoading', true);

        return new Promise((resolve, reject)=>{
            this.api.call("/member/two-factor-disable-0/", { password: this.password}).then((o:any)=>{
                this.eventAggregator.publish('setIsLoading', false);
                if (o.success) {
                    this.sent = true;
                } else {
                    this.alerts.showGenericError({ error: true, message: this.i18n.tr(`translation:error.server_error`) }); 
                }
                resolve();
            });
        });
    }
}
