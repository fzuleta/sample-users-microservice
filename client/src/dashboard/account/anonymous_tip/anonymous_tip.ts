import {inject, customElement} from "aurelia-framework"
import {EventAggregator} from 'aurelia-event-aggregator';
import {I18N} from 'aurelia-i18n';
import { Router } from 'aurelia-router';
import {validPassword} from "./../../../_/common/functions";
import {trace, validateEmail} from "./../../../_/common/functions";
import Api from './../../../_/connect/api';
import Alerts from "./../../../_/common/alerts";
import constants from "./../../../_/data/constants";
import Nav from "./../../../_/common/navigation"; 
import User from "./../../../_/data/user";

@customElement("anonymous-tip")
@inject(EventAggregator,Alerts, I18N, Nav, Api, User, 'apiRoot')
export default class Dashboard {
    eventAggregator:EventAggregator
    alerts:Alerts
    i18n:I18N
    router:Router
    nav:Nav
    api:Api
    user:User
    showEmailMe = false;
    errors;
    email = "";
    captchaToken = "";
    captchaDiv;
    captchaEnabled = true;
    showLoader = false;
    emailInfosent = false;
    constructor(eventAggregator,Alerts, I18N, Nav, Api, User, apiRoot){
        this.eventAggregator = eventAggregator
        this.alerts = Alerts
        this.i18n = I18N
        this.nav = Nav
        this.api = Api
        this.user = User; 
        this.captchaEnabled = constants.captchaEnabled;
    }
    emailMe() {
        this.showEmailMe = true;
    }
    login() {
        if (this.showLoader) return;
        this.errors = {}; 
        if (!validateEmail(this.email)) { this.errors["email"] = true; } 
        if (Object.keys(this.errors).length > 0) { this.captchaDiv && this.captchaDiv.reset(); return; }
 
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
            email:          this.email,
            captcha:        this.captchaToken,
        };
        const url = "/anonymous/send-me-my-info/";
        //Form submit
        this.api.call(url, o).then((u:any) => {
            this.showLoader = false; 
            this.eventAggregator.publish('setIsLoading', false);
            if (u.success) {
                this.emailInfosent = true;
            } else { 
                this.captchaDiv && this.captchaDiv.reset();
                let msg = "server_error";
                this.alerts.showGenericError({ error: true, message: this.i18n.tr(`translation:error.${msg}`) }); 
            }
        }); 
    }
}