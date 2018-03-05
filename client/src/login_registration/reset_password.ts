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
    hasErrors = false;
    errors;
    password0 = "";
    password1 = "";
    code="";
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
    activate(params, routeConfig) {
        this.code = (params && params.reference) ? decodeURIComponent(params.reference) : ""; 
        if (this.code == "") {
            this.nav.to("")
        }
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
        if (!validPassword(this.password0, this.password1)) { this.errors["password"] = true; } 
        this.hasErrors = Object.keys(this.errors).length > 0;
        if (this.hasErrors) { return; }

        if (!constants.captchaEnabled) { return this.submit(); }
        this.captchaDiv.render();
    }
    onCaptchaVerified(token) { 
        this.captchaToken = this.captchaDiv.getResponse();
        this.submit();
    }
    submit() {
        const obj = {
            code:       this.code,
            captcha:    this.captchaToken,
            password0:  this.password0,
            password1:  this.password1,
        }

        this.api.call("/member/forgot-password-recover/", obj).then((o:any) => {
            this.showLoader = false; 
            this.eventAggregator.publish('setIsLoading', false);
            if (o.success) {
                this.nav.to("login")
            } else {
                this.alerts.showGenericError({ error: true, message: this.i18n.tr(`translation:error.server_error`) });
            } 
        }) 
    }
}
