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
    email = "";
    password = "";
    action = "";
    captchaToken = "";
    captchaDiv;
    captchaEnabled = true;
    showLoader = false;
    rememberMe = true;
    fcn_login;
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

        this.email = this.ls.getItem("email") || "";
    }
    attached() {
        this.fcn_login = (event) => event.which == 13 || event.keyCode == 13 ? this.login() : null;
        window.addEventListener('keypress', this.fcn_login, false);
    }
    detached() {
        window.removeEventListener('keypress', this.fcn_login); 
    }
    openSignUp(){
        this.nav.to("signup");
    }
    openForgotPassword(){
        this.nav.to("forgotpassword");
    }
    keypressCallback() { this.login(); }

    login() {
        if (this.showLoader) return;
        this.errors = {}; 
        if (!validateEmail(this.email)) { this.errors["email"] = true; }
        if (this.password === "") { this.errors["password"] = true; } 
        if (Object.keys(this.errors).length > 0) { this.captchaDiv && this.captchaDiv.reset(); return; }

        this.action = "login";
        if (!constants.captchaEnabled) { return this.submit(); }
        this.captchaDiv.render();
    } 
    anonymous() {
        if (this.ls.getItem("anonymous") != null) {    
            this.alerts.showGenericError({ error: true, message: this.i18n.tr(`translation:anonymous.tip.error`) }); 
            return;
        }
        this.action = "anonymous";
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
            password:       this.password,  
            captcha:        this.captchaToken,
            rememberMe:     this.rememberMe,
        };
        const url = this.action === "login" ? "/member/login/" : "/member/register-anonymous/"
        //Form submit
        this.api.call(url, o).then((u:any) => {
            this.showLoader = false; 
            this.eventAggregator.publish('setIsLoading', false);
            if (u.success) {
                if (this.action == "login") {
                    this.loginThen(u);
                } else {
                    this.anonymousThen(u);
                }
            } else { 
                this.captchaDiv && this.captchaDiv.reset();
                let msg = "server_error";
                if (u.error == "USER_EXISTS_OR_WRONG") { msg = "wrong_user"}
                this.alerts.showGenericError({ error: true, message: this.i18n.tr(`translation:error.${msg}`) }); 
            }
        });
        return false;
    }
    loginThen(u: any) { 
        this.user.me.email = this.email;

        if (u.data.confirmEmail) {
            this.user.me.sendAgainConfirmPinEmail = true;
            return this.nav.to("confirm-email-sent")
        }
        if (u.data.show2FA) {
            return this.nav.to("two-factor");
        }

        this.api.refresh().then( () => {
            this.socket.restart(); 
            this.nav.to("folio"); 
            this.eventAggregator.publish("justloggedIn");
        }); 

        if (this.rememberMe) {
            this.ls.setItem("email", this.email);
        } else { 
            this.ls.removeItem("email");
        }
    }
    anonymousThen(u: any) {
        this.api.refresh().then( () => {
            this.ls.setItem("email", this.user.me.email);
            this.ls.setItem("anonymous", "true");
            this.socket.restart(); 
            this.nav.to("folio"); 
            this.eventAggregator.publish("justloggedIn");
        }); 
 
    }
}