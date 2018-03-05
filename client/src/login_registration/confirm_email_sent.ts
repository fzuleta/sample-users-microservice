import {bindable, inject, customElement} from 'aurelia-framework';
import {EventAggregator} from 'aurelia-event-aggregator';
import {I18N} from 'aurelia-i18n'; 
import Api from "../_/connect/api";
import Alerts from "../_/common/alerts";
import Nav from "../_/common/navigation";
import User from "../_/data/user";
import {trace} from "../_/common/functions";

@inject(Nav, EventAggregator, I18N, User, Api, Alerts)
export default class Section {
    eventAggregator:EventAggregator;
    nav: Nav; 
    i18n: I18N; 
    api: Api;
    user: User;
    alerts: Alerts; 
    email= ""
    sendOnOpen = false 
    constructor(Nav, EventAggregator, I18N, User, Api, Alerts) { 
        this.eventAggregator = EventAggregator
        this.nav = Nav;
        this.i18n = I18N;
        this.api = Api;
        this.user = User;
        this.alerts = Alerts;
    } 
    activate(){
        this.email = this.user.me ? this.user.me.email : "feli@felipezuleta.com"; 
        
        if(this.user.me.sendAgainConfirmPinEmail){
            this.sendAgain();
        }
    }
    attached() {

    }
    sendAgain() {
        if (this.email == "") return;
        this.eventAggregator.publish('setIsLoading', true);
        const o = { 
            email: this.email,
        }
        this.api.call("/member/confirm-email-send-again/", o).then((o:any) => {
            this.eventAggregator.publish('setIsLoading', false);
            if (o.success) {
                trace("okokok")
                // this.emailSent = true;
            } else {
                trace(o)
            }
        });
    }
}