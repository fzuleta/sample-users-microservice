import {bindable, inject, customElement} from 'aurelia-framework';
import {EventAggregator} from 'aurelia-event-aggregator';
import {I18N} from 'aurelia-i18n'; 
import Nav from "../_/common/navigation";
import Api from "../_/connect/api";
import Alerts from "../_/common/alerts";
import User from "../_/data/user"; 
import {elegibleCountries, trace, validateEmail, validPassword} from "../_/common/functions";
import constants from "./../_/data/constants"

@inject(Nav, EventAggregator, I18N, Api, User, Alerts)
export default class Section {
    eventAggregator:EventAggregator;
    nav: Nav;
    i18n: I18N;
    api: Api;
    user: User;
    alerts: Alerts; 
    sent = false;
    errors;
    email = "";
    captchaToken = "";
    captchaDiv;
    captchaEnabled = true;
    showLoader = false; 
    fcn_login;
    constructor(Nav, EventAggregator, I18N, Api, User, Alerts) { 
        this.nav = Nav;
        this.eventAggregator = EventAggregator;
        this.i18n = I18N;
        this.api = Api;
        this.user = User;
        this.alerts = Alerts;
        this.captchaEnabled = constants.captchaEnabled;
    }
    attached() {
        this.fcn_login = (event) => event.which == 13 || event.keyCode == 13 ? this.recover() : null;
        window.addEventListener('keypress', this.fcn_login, false);
    }
    detached() {
        window.removeEventListener('keypress', this.fcn_login); 
    }
    recover(){
        if (this.showLoader) return;
        this.errors = {}; 
        if (!validateEmail(this.email)) { this.errors["email"] = true; } 
        if (Object.keys(this.errors).length > 0) { return; }

        if (!constants.captchaEnabled) { return this.submit(); }
        this.captchaDiv.render();
    }
    onCaptchaVerified(token) { 
        this.captchaToken = this.captchaDiv.getResponse(); 
        this.submit();
    }
    submit() {
        this.eventAggregator.publish('setIsLoading', true);
        this.showLoader = true;  
        return new Promise((resolve,reject) => {
            this.api.call("/member/forgot-password/", {
                email: this.email,
                captcha: this.captchaToken,
            }).then((o: any)=>{
                this.showLoader = false; 
                this.eventAggregator.publish('setIsLoading', false);
                if (o.success) {
                    this.sent = true;
                } else {
                    this.alerts.showGenericError({ error: true, message: this.i18n.tr(`translation:error.server_error`) });
                }
                resolve()
            })
        });
    }
}