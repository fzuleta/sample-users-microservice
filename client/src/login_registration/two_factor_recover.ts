import {bindable, inject, customElement} from 'aurelia-framework';
import {EventAggregator} from 'aurelia-event-aggregator';
import {I18N} from 'aurelia-i18n'; 
import Nav from "../_/common/navigation";
import {trace, validateEmail} from "../_/common/functions"; 
import Api from "../_/connect/api";
import Alerts from "../_/common/alerts";
import User from "../_/data/user"; 
import ltSocket from './../_/connect/socket';
import LocalStorage from './../_/common/localstorage';
import constants from "./../_/data/constants"

@inject(Nav, EventAggregator, I18N, Api, User, Alerts, ltSocket, LocalStorage)
export default class Section {
    eventAggregator:EventAggregator;
    nav: Nav;
    i18n: I18N;
    api: Api;
    user: User;
    alerts: Alerts; 
    socket: ltSocket; 
    ls: LocalStorage; 
    errors; 
    showLoader = false;
    captchaToken = "";
    captchaDiv;
    captchaEnabled = true;
    code = ""; 
    constructor(Nav, EventAggregator, I18n, Api, User, Alerts, ltSocket,LocalStorage) { 
        this.nav = Nav;
        this.eventAggregator = EventAggregator
        this.i18n = I18n;
        this.api = Api;
        this.user = User;
        this.alerts = Alerts;
        this.socket = ltSocket;
        this.ls = LocalStorage;
 
        this.captchaEnabled = constants.captchaEnabled;
    } 
    attached() { 
        
    }
    recover() {
        if(this.showLoader) return;
        this.errors = {};  
        if (this.code == "") { this.errors["code"] = true; }
        if (Object.keys(this.errors).length > 0) { return; }

        if (!constants.captchaEnabled) { return this.submit(); }
        this.captchaDiv.render();
    }
    onCaptchaVerified(token) { 
        this.captchaToken = this.captchaDiv.getResponse(); 
        this.submit();
    }
    submit() {
        this.showLoader = true; 
        this.eventAggregator.publish('setIsLoading', true); 

        const o = { 
            email: this.user.me.email,
            code: this.code 
        };
        //Form submit
        this.api.call("/member/two-factor-recover/", o).then((u:any) => {
            this.showLoader = false; 
            this.eventAggregator.publish('setIsLoading', false);
            if (u.success) {
                this.api.refresh().then( () => {
                    this.socket.restart(); 
                    this.nav.to("folio"); 
                    this.eventAggregator.publish("justloggedIn");
                }); 
            } else {
                this.alerts.showGenericError({ error: true, message: this.i18n.tr(`translation:error.bad2fa`) });
            }
        });
        return false;
    } 
}