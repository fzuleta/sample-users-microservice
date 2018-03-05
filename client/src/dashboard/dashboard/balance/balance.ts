import {inject, customElement} from "aurelia-framework"
import {EventAggregator} from 'aurelia-event-aggregator';
import {I18N} from 'aurelia-i18n';
import { Router } from 'aurelia-router';
import * as functions from "./../../../_/common/functions";
import {trace} from "./../../../_/common/functions";
import Api from './../../../_/connect/api';
import Alerts from "./../../../_/common/alerts";
import constants from "./../../../_/data/constants";
import Nav from "./../../../_/common/navigation"; 
import User from "./../../../_/data/user"; 
import * as CountUp from "countup.js";
import * as moment from 'moment';

@customElement("dashboard-balance")
@inject(EventAggregator,Alerts, I18N, Nav, Api, User, 'apiRoot')
export default class Dashboard {
    eventAggregator:EventAggregator
    alerts:Alerts
    i18n:I18N
    router:Router
    nav:Nav
    api:Api
    user:User
    MONEY = constants.MONEY;
    balance = 0;
    constructor(eventAggregator,Alerts, I18N, Nav, Api, User, apiRoot){
        this.eventAggregator = eventAggregator
        this.alerts = Alerts
        this.i18n = I18N
        this.nav = Nav
        this.api = Api
        this.user = User; 
    }
    activate() {

    }
    attached() { 
    }
    deactivate(){ 
    }
    logout() {
        this.nav.logout();
    }
    openAccount() {
        this.nav.to("account");
    }
}
