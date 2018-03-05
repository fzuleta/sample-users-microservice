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
    code = ""; 
    captchaToken = "";
    captchaDiv;
    captchaEnabled = true;
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
    login() {
        this.errors = {};  
        if (this.errors.length < 6) { this.errors["code"] = true; } 
        if (Object.keys(this.errors).length > 0) { return; }
        
        if (!constants.captchaEnabled) { return this.doLogin(); }
        this.captchaDiv.render();
    } 
    onCaptchaVerified(token) { 
        this.captchaToken = this.captchaDiv.getResponse(); 
        this.doLogin();
    }
    doLogin() {
        this.showLoader = true; 
        this.eventAggregator.publish('setIsLoading', true);

        const o = { code: this.code };
        //Form submit
        this.api.call("/member/login-2fa/", o).then((u:any) => {
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
    openUnableToSubmitCode(){
        this.nav.to("two-factor-recover")
    }
}