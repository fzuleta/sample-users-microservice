import {bindable, inject, customElement} from 'aurelia-framework';
import {EventAggregator} from 'aurelia-event-aggregator';
import {I18N} from 'aurelia-i18n'; 
import Nav from "../_/common/navigation";
import Api from "../_/connect/api";
import Alerts from "../_/common/alerts";
import LTSocket from './../_/connect/socket';
import {trace, validPassword} from "../_/common/functions";

@inject(Nav, EventAggregator, I18N, Api, Alerts, LTSocket)
export default class Section {
    eventAggregator:EventAggregator;
    nav: Nav;  
    i18n: I18N;  
    api: Api;  
    alerts: Alerts;
    socket: LTSocket; 
    ok = false;
    notok = false;
    constructor(Nav, EventAggregator, I18N, API, Alerts, LTSocket) { 
        this.eventAggregator = EventAggregator
        this.nav = Nav;
        this.i18n = I18N;
        this.api = API;
        this.alerts = Alerts;
        this.socket = LTSocket;
    }
    activate(params, routeConfig) {
        const code = (params && params.reference) ? decodeURIComponent(params.reference) : ""; 

        this.eventAggregator.publish('setIsLoading', true);

        return new Promise((resolve,reject) => {
            this.api.call("/member/confirm-email/", {code}).then((o: any)=>{
                this.eventAggregator.publish('setIsLoading', false);
                if (o.success) {
                    this.api.refresh().then( () => {
                        this.socket.restart(); 
                        this.nav.to("folio"); 
                        this.eventAggregator.publish("justloggedIn");
                    }); 

                } else {
                    this.notok = true;
                }
                resolve()
            })
        })
    }
}
