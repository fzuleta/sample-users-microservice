import { inject } from 'aurelia-framework';
import { EventAggregator } from 'aurelia-event-aggregator';
import Api from "../connect/api";
import constants from "./../data/constants"

@inject(EventAggregator, Api)
export default class Navigation {
    eventAggregator:EventAggregator
    api: Api
    constructor(EventAggregator, api){
        this.eventAggregator = EventAggregator;
        this.api = api;
    }

    to(where:string, param:string=""){ 
        const encParam = encodeURIComponent(param);
        let o:any = {}; 
        switch (where){

            case "login":               o.route = "/login";                 break;
            case "signup":              o.route = "/signup";                break;
            case "forgotpassword":      o.route = "/forgotpassword";        break;
            case "confirm-email-sent":  o.route = "/confirm-email-sent";    break;
            case "complience":          o.route = "/complience";            break;
            case "two-factor":          o.route = "/two-factor";            break;
            case "two-factor-recover":  o.route = "/two-factor-recover";    break;
            case "two-factor-enable":   o.route = "/two-factor-enable";     break;
            case "two-factor-disable":  o.route = "/two-factor-disable";    break;
            
            case "tos":                 o.route = "/tos";                   break;
            case "privacy":             o.route = "/privacy";               break;
            case "usepolicy":           o.route = "/usepolicy";             break;
            case "faq":                 o.route = "/faq";                   break;

            case "presale":             o.route = "/presale";               break;
            case "presale-thankyou":    o.route = "/pre-thankyou";          break;

            case "dashboard":           o.route = "/dashboard";             break;
            case "account":             o.route = "/account";               break;

            default:                    o.route = "";
        }
        this.eventAggregator.publish("navigateTo", o);
    }
    open(link) {
        if (link === "tos") {
            window.open(`${constants.clientURL}/tos`, "_blank");
        } else if (link === "privacy") {
            window.open(`${constants.clientURL}/privacy`, "_blank");
        }
    }
    logout() {
        this.api.call("/member/logout/", {}).then((u:any) => {
            window.location.reload();
        });
    }
}
