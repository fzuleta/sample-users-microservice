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

@customElement("anonymous-settings")
@inject(EventAggregator,Alerts, I18N, Nav, Api, User, 'apiRoot')
export default class Dashboard {
    eventAggregator:EventAggregator;
    alerts:Alerts;
    i18n:I18N;
    router:Router;
    nav:Nav;
    api:Api;
    user:User;
    showLoader = false;
    emailInfosent = false;
    email = "";
    errors: any = {};
    constructor(eventAggregator,Alerts, I18N, Nav, Api, User, apiRoot){
        this.eventAggregator = eventAggregator
        this.alerts = Alerts
        this.i18n = I18N
        this.nav = Nav
        this.api = Api
        this.user = User; 
    }
    changeEmail() {
        this.errors = {};
        if (!validateEmail(this.email)) { this.errors["email"] = true; }
        
        const hasErrors = Object.keys(this.errors).length > 0;
        if (hasErrors) { return;}


        const oSend = {
            email: this.email,
        };
        const url = "/anonymous/upgrade-to-email/";

        this.alerts.showGenericError({
            warning: true,
            message: this.i18n.tr("anonymous.settings.q0", { email: this.email }),
            cancel:true,
            ok:true
        }).then(dialogResponse => dialogResponse.closeResult.then(response => {
            trace(response);
            if (response.wasCancelled) { return; } 
            this.showLoader = true;
            this.eventAggregator.publish("setIsLoading", true);
            this.api.call(url, oSend)
            .then((o:any)=>{ 
                this.eventAggregator.publish("setIsLoading", false);
                if (o.success) { 
                    this.emailInfosent = true;
                } else {
                    this.eventAggregator.publish("showGenericError", {
                        error: true,
                        message: this.i18n.tr("translation:error.genericError_0")
                    });
                }
            }); 
        }));
    }
}