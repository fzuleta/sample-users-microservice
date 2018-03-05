import {inject, customElement} from "aurelia-framework"
import {EventAggregator} from 'aurelia-event-aggregator';
import {I18N} from 'aurelia-i18n';
import { Router } from 'aurelia-router';
import {validPassword} from "./../../_/common/functions";
import {trace} from "./../../_/common/functions";
import Api from './../../_/connect/api';
import Alerts from "./../../_/common/alerts";
import constants from "./../../_/data/constants";
import Nav from "./../../_/common/navigation"; 
import User from "./../../_/data/user";

@inject(EventAggregator,Alerts, I18N, Nav, Api, User, 'apiRoot')
export default class Dashboard {
    eventAggregator:EventAggregator
    alerts:Alerts
    i18n:I18N
    router:Router
    nav:Nav
    api:Api
    user:User

    oldPassword = ""
    newPassword0 = ""
    newPassword1 = ""

    hasErrors = false;
    errors: {};
    constructor(eventAggregator,Alerts, I18N, Nav, Api, User, apiRoot){
        this.eventAggregator = eventAggregator
        this.alerts = Alerts
        this.i18n = I18N
        this.nav = Nav
        this.api = Api
        this.user = User; 
    }
    cancel(){
        this.nav.to("folio");
    }
    save(){
        this.errors = {};
        trace(validPassword(this.oldPassword, this.oldPassword))
        if (!validPassword(this.oldPassword, this.oldPassword)) { this.errors["password0"] = true; }
        if (!validPassword(this.newPassword0, this.newPassword1)) { this.errors["password"] = true; }

        this.hasErrors = Object.keys(this.errors).length > 0;
        if (this.hasErrors) return;

        const o = { 
            oldPassword: this.oldPassword,
            newPassword0: this.newPassword0,
            newPassword1: this.newPassword1
        }
        return new Promise((resolve,reject) => {
            this.api.call("/member/update-password/", o).then((o: any)=>{
                this.hasErrors = false;
                if (o.success) {
                    this.alerts.showGenericError({ success: true, message: this.i18n.tr(`profile:account.success.password`) }); 
                    this.oldPassword = "";
                    this.newPassword0 = "";
                    this.newPassword1 = "";
                } else {
                    this.alerts.showGenericError({ error: true, message: this.i18n.tr(`profile:account.error.password`) }); 
                }
                resolve()
            })
            .catch((o) => {
                this.hasErrors = false;
            })
        })
    }
    enable2fa(){
        this.nav.to("two-factor-enable");
    }
    disable2fa(){
        this.nav.to("two-factor-disable");
    }
}