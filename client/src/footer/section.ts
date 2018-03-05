import {inject, customElement} from "aurelia-framework"
import {EventAggregator} from 'aurelia-event-aggregator';
import {I18N} from 'aurelia-i18n';
import { Router } from 'aurelia-router';
import {trace, validateEmail} from "../_/common/functions";
import Api from './../_/connect/api';
import Alerts from "./../_/common/alerts";
import constants from "./../_/data/constants";
import Nav from "./../_/common/navigation"; 
import User from "./../_/data/user";

@inject(EventAggregator,Alerts, I18N, Nav, Api, User, 'apiRoot')
@customElement('footer')
export class Section { 
    eventAggregator:EventAggregator
    alerts:Alerts
    i18n:I18N
    router:Router
    nav:Nav
    api:Api
    user:User 
    emailPlaceholder = "";
    showLoader = false;
    emailsent = false;
    email = "";
    constructor(eventAggregator,Alerts, I18N, Nav, Api, User, apiRoot){
        this.eventAggregator = eventAggregator
        this.alerts = Alerts
        this.i18n = I18N
        this.nav = Nav
        this.api = Api
        this.user = User; 
    }
    attached() {  
        this.emailPlaceholder = this.i18n.tr("footer.placeholdersubscribe")
    } 
    openHome() {
        this.nav.to("");
    }
    openTos() {
        this.nav.to("tos");
    }
    openPrivacy() {
        this.nav.to("privacy");
    } 
    openFAQ() {
        this.nav.to("faq");
    }
    openInteractions() {
        this.nav.to("interactions");
    }
    openRewards() {
        this.nav.to("rewards");
    }
    openDashboard() {
        this.nav.to("folio");
    }
    openAccount() {
        this.nav.to("account");
    }
    openSignup() {
        this.nav.to("login");
    }
    openLogout() {
        this.nav.logout();
    }
    openSupport() {
        trace("Todo Open support");
    }

    subscribeForUpdates() {

        if (this.showLoader) { return; }
        if (!validateEmail(this.email)) { return; }

        const obj = {
            email: this.email,
        }
        this.showLoader = true;
        this.api.call("/tokensale/subscribe-updates/", obj)
        .then((o: any)=>{
            this.emailsent = o.success;
        });
    }
    btnApplyForPresale() {
        this.nav.to("presale");
    }
}
