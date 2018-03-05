import {bindable, inject, customElement} from 'aurelia-framework';
import {EventAggregator} from 'aurelia-event-aggregator';
import {I18N} from 'aurelia-i18n'; 
import Nav from "../../_/common/navigation";
import Api from "../../_/connect/api";
import {trace, validPassword, isNumeric} from "../../_/common/functions";
import Alerts from "../../_/common/alerts";
import User from "../../_/data/user";

@inject(Nav, EventAggregator, I18N, Api, Alerts, User)
export default class Section {
    eventAggregator:EventAggregator;
    nav: Nav; 
    i18n: I18N; 
    api: Api; 
    alerts: Alerts; 
    user: User; 
    secretKey="";
    recoveryCode="";
    verificationCode="";
    txtRecoveryCode="";
    step = 0;
    qr = "";
    showLoader = false;
    constructor(Nav, EventAggregator, I18N, Api, Alerts, User) { 
        this.nav = Nav;
        this.eventAggregator = EventAggregator
        this.i18n = I18N;
        this.api = Api;
        this.alerts = Alerts;
        this.user = User;
    }
    activate(params, routeConfig) {
        return new Promise((resolve, reject)=>{
            this.api.call("/member/two-factor-enable-0/").then((o:any)=>{
                if (o.success) {
                    this.qr = o.data.qr;
                    this.secretKey = o.data.secretKey;
                    this.recoveryCode = o.data.recoveryCode;
                }
                resolve();
            });
        });
    }
    validateStep0() {
        if (this.verificationCode == "") {
            return
        }
        this.showLoader = true;
        this.api.call("/member/two-factor-enable-1/",{ code: this.verificationCode }).then((o:any)=>{
            this.showLoader = false;
            if (o.success) {
                this.step = 1;
            } else {
                this.alerts.showGenericError({ error: true, message: this.i18n.tr(`translation:error.bad2fa`) }); 
            }
        });
    }
    validateStep1() {
        this.step = 2;
    }
    iDidntWriteIt() {
        this.txtRecoveryCode = ""
        this.step = 1;
    }
    setup2fa() {
        if (this.recoveryCode != this.txtRecoveryCode) {
            return;
        }
        this.showLoader = true;
        this.api.call("/member/two-factor-enable-2/").then((o:any)=>{
            this.showLoader = false;
            if (o.success) {
                this.user.twofactorEnabled = this.user.me.twofactorEnabled = true;
                this.nav.to("dashboard");
            } else {
                this.alerts.showGenericError({ error: true, message: this.i18n.tr(`translation:error.bad2fa`) }); 
            }
        });
    }
    returnToAccount() {
        this.nav.to("account");
    }
}
