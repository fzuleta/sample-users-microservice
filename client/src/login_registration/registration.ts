import {bindable, inject, customElement} from 'aurelia-framework';
import {EventAggregator} from 'aurelia-event-aggregator';
import {I18N} from 'aurelia-i18n'; 
import Nav from "../_/common/navigation";
import Api from "../_/connect/api";
import Alerts from "../_/common/alerts";
import User from "../_/data/user"; 
import {elegibleCountries, trace, validateEmail, validPassword} from "../_/common/functions";
import constants from "./../_/data/constants"
import ltSocket from './../_/connect/socket';

@inject(Nav, EventAggregator, I18N, Api, User, Alerts, ltSocket)
export default class Section {
    eventAggregator:EventAggregator;
    nav: Nav;
    i18n: I18N;
    api: Api;
    user: User;
    alerts: Alerts; 
    socket: ltSocket; 
    nationalities = []; 
    nationality;
    residence;
    hasErrors = false;
    errors: {};
    firstname = "";
    lastname = "";
    email = "";
    password = "";
    chk0 = false;
    chk1 = false;
    showLoader = false; 
    captchaEnabled = false;
    captchaDiv;
    captchaToken = "";
    fcn_login;
    constructor(Nav, EventAggregator, I18n, Api, User, Alerts, ltSocket) { 
        this.nav = Nav;
        this.eventAggregator = EventAggregator
        this.i18n = I18n;
        this.api = Api;
        this.user = User;
        this.alerts = Alerts;
        this.socket = ltSocket;

        this.captchaEnabled = constants.captchaEnabled;
    }
    activate() {
        this.nationalities = elegibleCountries();
        this.nationalities.unshift({name: this.i18n.tr("loginreg:register.selectyourcountry"), value: "-1" });
        this.nationality = this.nationalities[0];
        this.residence = this.nationalities[0];
        
        this.eventAggregator.subscribe("change_lang", payout => {
            this.nationalities[0].name = this.i18n.tr("loginreg:register.selectyourcountry")
        });
    }
    attached() {
        this.fcn_login = (event) => event.which == 13 || event.keyCode == 13 ? this.register() : null;
        window.addEventListener('keypress', this.fcn_login, false);
    }
    detached() {
        window.removeEventListener('keypress', this.fcn_login); 
    }
    openForgotPassword(){
        this.nav.to("forgotpassword");
    }
    openWhitepaper(){
        window.open("pdf/whitepaper.pdf", "_blank");
    }
    openAcceptable(){
        this.nav.open("usepolicy");
    }
    openTOS(){
        this.nav.open("tos");
    }
    openPrivacy(){
        this.nav.open("privacy");
    }
    register() { 
        if (this.showLoader) return;
        this.errors = {};
        // if (this.firstname === "") { this.errors["firstname"] = true; }
        // if (this.lastname === "") { this.errors["lastname"] = true; }
        if (!validateEmail(this.email)) { this.errors["email"] = true; }
        if (!validPassword(this.password, this.password)) { this.errors["password"] = true; }
        // if (this.nationality.value === "-1") { this.errors["nationality"] = true;  }
        // if (this.residence.value === "-1") { this.errors["residence"] = true; }
        if (!this.chk0) { this.errors["check0"] = true; }
        // if (!this.chk1) { this.errors["check1"] = true; }
        this.hasErrors = Object.keys(this.errors).length > 0;
        if (this.hasErrors) { this.captchaDiv && this.captchaDiv.reset(); return;}

        if (!constants.captchaEnabled) { return this.submit(); }

        this.captchaDiv.render();
    }
    onCaptchaVerified(token) { 
        this.captchaToken = this.captchaDiv.getResponse(); 
        this.submit();
    }
    submit() {
        if (!this.chk0) { return; }
        this.showLoader = true;
        const nationality = this.nationality.value;
        const residence = this.residence.value;
        const o = {
            email:          this.email, 
            password:       this.password,
            password2:      this.password,
            firstName:      this.firstname,
            lastName:       this.lastname,
            nationality:    nationality,
            residence:      residence,
            approve:        true,
            captcha:        this.captchaToken,
            rememberMe:     true,
        };

        this.eventAggregator.publish('setIsLoading', true);
        
        this.api.call("/member/register/", o)
        .then((u:any) => {
            const data = u.data
            this.eventAggregator.publish('setIsLoading', false);

            if (u.success) {
                this.user.me.email = this.email
                this.nav.to("confirm-email-sent");
            } else {
                this.captchaDiv && this.captchaDiv.reset();
                this.showLoader = false;
                let msg = "server_error";
                if (u.error == "USER_EXISTS_OR_WRONG") { msg = "wrong_user"}
                this.alerts.showGenericError({ error: true, message: this.i18n.tr(`translation:error.${msg}`) }); 
            }
            // todo Navigate somewhere
        });
    }
}