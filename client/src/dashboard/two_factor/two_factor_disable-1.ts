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
    ok = false;
    notok = false;
    constructor(Nav, EventAggregator, I18N, API, Alerts) { 
        this.eventAggregator = EventAggregator
        this.nav = Nav;
        this.i18n = I18N;
        this.api = API;
        this.alerts = Alerts;
    }
    activate(params, routeConfig) {
        const code = (params && params.reference) ? decodeURIComponent(params.reference) : ""; 

        return new Promise((resolve,reject) => {
            this.api.call("/member/two-factor-disable-1/", {code}).then((o: any)=>{
                if (o.success) {
                    this.ok = true;
                } else {
                    this.notok = true;
                }
                resolve()
            })
        })
    }
}
