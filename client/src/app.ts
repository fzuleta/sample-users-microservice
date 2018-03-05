import {EventAggregator} from "aurelia-event-aggregator";
import {inject} from "aurelia-framework";
import {I18N} from "aurelia-i18n";
import {trace} from "./_/common/functions";
import * as ga from "./_/common/analytics/ga";
import constants from "./_/data/constants";
import {Router, RedirectToRoute} from "aurelia-router";
import Alerts from "./_/common/alerts";
import LocalStorage from "./_/common/localstorage";
import LTSocket from "./_/connect/socket";
import API from "./_/connect/api";
import User from "./_/data/user";

@inject(I18N, EventAggregator,Alerts, LocalStorage, LTSocket, API)
export class App {
    i18n:I18N;
    eventAggregator:EventAggregator;   
    localStorage: LocalStorage;
    router:Router; 
    alerts:Alerts; 
    socket:LTSocket; 
    api:API; 
    evt_navigateTo; 
    subscriber;
    loader;
    evt_errorshow;
    _showLoader:Boolean = false;
    constructor(i18n, eventAggregator, Alerts, LocalStorage, LTSocket, API) {
        this.i18n = i18n;
        this.eventAggregator = eventAggregator;
        this.alerts = Alerts;
        this.localStorage = LocalStorage;
        this.socket = LTSocket;
        this.api = API;
    }
    activate(){
        this.evt_navigateTo = this.eventAggregator.subscribe("navigateTo", payload => { 
            this.router.navigate(payload.route, payload.params || undefined); 
        });
        window.addEventListener("orientationchange", () => {
            this.eventAggregator.publish("device_orientation_changed", {angle: screen["orientation"].angle});
        });
        ga.init();
        return this.api.refresh();
    }
    attached(){
        this.loader = $("#loadingScreen");
        //Subscribes to the eventAggregator to show the loader or not
        this.subscriber = this.eventAggregator.subscribe("setIsLoading", showLoader => {
            if (showLoader) {
                this.loader.show(); 
            } else {
                this.loader.show();
                this.loader.fadeOut(500, () => { this._showLoader = showLoader; } ); 
            }
        });
        this.evt_errorshow = this.eventAggregator.subscribe("showGenericError", payload => { 
            this.alerts.showGenericError(payload);
        });
        this.evt_errorshow = this.eventAggregator.subscribe("change_lang_trigger", payload => { 
            this.localStorage.setItem("lang", payload);
            this.i18n
            .setLocale(payload)
            .then( () => {
                trace("ioooo changed langue");
                this.eventAggregator.publish("change_lang", payload); 
            });
        });
        
        setTimeout( ()=> {
            this.eventAggregator.publish("setIsLoading", false);    
        },250);
    }
    configureRouter(config, router) {
        this.router = router;
        config.title = "Sample Login";

        // Remove the /#/address/
        // config.options.pushState = true;
        config.options.root = "/"; // this matches the base href tag 

        config.addPipelineStep("authorize",     AuthorizeStep);
        config.addPipelineStep("preRender",     PreRenderStep);
        config.addPipelineStep("postRender",    PostRenderStep);

        config.map([
            {   route:"",
                name: "home", 
                viewPorts:{ mainView:{moduleId: "./home/home"} }, 
                settings: { roles: []} }, 
                

            {   route:["/login"], 
                viewPorts:{ mainView:{moduleId: "./login_registration/login"} },
                name: "login", 
                settings: { roles: ["notloggedin"]} },
            {   route:["/signup"], 
                viewPorts:{ mainView:{moduleId: "./login_registration/registration"} },
                name: "registration", 
                settings: { roles: ["notloggedin"]} },
            {   route:["/forgotpassword"], 
                viewPorts:{ mainView:{moduleId: "./login_registration/forgot_password"} },
                name: "forgotpassword", 
                settings: { roles: ["notloggedin"]} },
            {   route:["/reset-password/:reference"], 
                viewPorts:{ mainView:{moduleId: "./login_registration/reset_password"} },
                name: "resetpassword", 
                settings: { roles: ["notloggedin"]} },
                
            {   route:["/confirm-email/:reference"], 
                viewPorts:{ mainView:{moduleId: "./login_registration/confirm_email"} },
                name: "confirmemail", 
                settings: { roles: []} },
            {   route:["/confirm-email-sent"], 
                viewPorts:{ mainView:{moduleId: "./login_registration/confirm_email_sent"} },
                name: "confirmemailsent", 
                settings: { roles: []} },
            {   route:["/two-factor"], 
                viewPorts:{ mainView:{moduleId: "./login_registration/two_factor"} },
                name: "twofactor", 
                settings: { roles: []} },
            {   route:["/two-factor-recover"], 
                viewPorts:{ mainView:{moduleId: "./login_registration/two_factor_recover"} },
                name: "twofactorrecover", 
                settings: { roles: []} },
            {   route:["/two-factor-enable"], 
                viewPorts:{ mainView:{moduleId: "./dashboard/two_factor/two_factor_enable"} },
                name: "twofactorenable", 
                settings: { roles: ["loggedIn"]} },
            {   route:["/two-factor-disable"], 
                viewPorts:{ mainView:{moduleId: "./dashboard/two_factor/two_factor_disable"} },
                name: "twofactordisable", 
                settings: { roles: ["loggedIn"]} },
                {   route:["/two-factor-disable-cmd/:reference"], 
                    viewPorts:{ mainView:{moduleId: "./dashboard/two_factor/two_factor_disable-1"} },
                    name: "twofactordisablecmd", 
                    settings: { roles: []} },


            {   route:["/dashboard"], 
            viewPorts:{ mainView:{moduleId: "./dashboard/dashboard/dashboard"} },
            name: "dashbboard", 
            settings: { roles: ["loggedIn"]} },
            {   route:["/account", "/a"], 
                viewPorts:{ mainView:{moduleId: "./dashboard/account/account"} },
                name: "account", 
                settings: { roles: ["loggedIn"]} },
                
        ]);

        const handleUnknownRoutes = (instruction) => {
            return { 
                viewPorts:{ "mainView":{ moduleId: "./notfound/notfound" } },
                settings: { roles:[] }
            };
        }
        config.mapUnknownRoutes(handleUnknownRoutes);
    }
}


@inject(User)
class AuthorizeStep {
    user: User;
    constructor(User){
        this.user = User;
    };

    run(navigationInstruction, next) {
        for(let i=0; i < navigationInstruction.getAllInstructions().length; i++) {
            const ni = navigationInstruction.getAllInstructions()[i]; 
            
            if(ni.config.settings.roles.indexOf("loggedIn") >=  0) {
                if (!this.user.isAuthenticatedOrRemembered) { return next.cancel(new RedirectToRoute("login")); }
            }
            if(ni.config.settings.roles.indexOf("isAuthenticated") >= 0) {
                if (!this.user.isAuthenticated) { return next.cancel(new RedirectToRoute("login")); }
            }
            if(ni.config.settings.roles.indexOf("notloggedin") >= 0) {
                if (this.user.isAuthenticatedOrRemembered) { return next.cancel(new RedirectToRoute("home")); }
            }
        }

        return next();
    }
}

@inject(EventAggregator)
class PreRenderStep {
    eventAggregator;
    constructor(EventAggregator){
        this.eventAggregator = EventAggregator;
    };

    run(navigationInstruction, next) {
        this.eventAggregator.publish("setIsLoading", true);
        trace("---- pre");
    return next();
    }
}
@inject(EventAggregator)
class PostRenderStep {
    eventAggregator;
    constructor(EventAggregator){
        this.eventAggregator = EventAggregator;
    };

    run(navigationInstruction, next) {
        trace("---- post");
        this.eventAggregator.publish("setIsLoading", false);
        this.eventAggregator.publish("viewHasFinishedRendering");
        window.scroll(0,0); 
        return next();
    }
}