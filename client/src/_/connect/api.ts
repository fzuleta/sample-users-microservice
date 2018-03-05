import {HttpClient as HttpFetch, json} from 'aurelia-fetch-client'; 
import { inject } from 'aurelia-framework';
import User from "./../data/user"
import constants from "./../data/constants"
import {trace} from "./../common/functions" 
import * as ga from "./../common/analytics/ga"; 

@inject('apiRoot', HttpFetch, User)
export default class Api {
    apiRoot:string
    httpFetch:HttpFetch 
    user:User
    apiversion:string = "/api"
    marketRefreshTimer;
    constructor(apiRoot, httpFetch, User){
        this.apiRoot = apiRoot;
        this.user = User;

        httpFetch.configure(config => {
            config
                .withDefaults({
                    credentials: 'include', // Valid values; omit, same-origin and include
                    headers: {
                        'Accept': 'application/json',
                        'X-Requested-With': 'Fetch'
                    }
                })
                .withInterceptor({
                    request(request) {
                        trace(`Requesting ${request.method} ${request.url}`);
                        // trace(request);
                        return request; 
                    },
                    // response(response) {
                    //     console.log(`Received ${response.status} ${response.url}`);
                    //     return response; // you can return a modified Response
                    // }
                });
        });
        this.httpFetch = httpFetch; 
    }

    call(endPoint="", myPostData={}, isFormData=false) {
        let body = null;
        
        myPostData = Object.assign(myPostData, {expected_token: this.user.expectedToken});

        if (!isFormData) { body = JSON.stringify(myPostData) } else { body = myPostData; }
        return new Promise( (resolve, reject) => {
            this.httpFetch.fetch(this.apiRoot + this.apiversion + endPoint, {
                method: "POST",
                body: body
            })
            .then(response => response.json())
            .then(o => {
                const data = (o && o["data"]) ? o["data"] : null;
                if(data) { 
                    if (data["isRemembered"] != undefined) {
                        this.user.isRemembered = data["isRemembered"] == true;
                    } 

                    if(data["isAuthenticated"] != undefined) {
                        this.user.isAuthenticated = data["isAuthenticated"] == true;
                    }

                    this.user.isAuthenticatedOrRemembered = this.user.isRemembered || this.user.isAuthenticated;  
                    
                }
                resolve(o); 
            })
            .catch( o => {
                reject(o);
            });
        });
    }

    refresh(){ 
        return new Promise((resolve,reject)=>{
            this.call('/refresh/')
            .then( (o:any) => { 
                const data = o.data;
                
                // We filter the array to not include an extra one that was added by the server
                constants.clientURL         = data["login"].clientURL;
                this.user.expectedToken     = data["login"].expected_token;
                constants.captchaEnabled    = data["login"].captchaEnabled;
                constants.env               = data["env"]; 
                       
                //When refreshing my info comes back too
                if (data.me) this.user.setMe(data); 
                
                if (this.user.isAuthenticated) {
                    ga.setUser(this.user.reference);
                }         
                resolve();
            })
            .catch(o=>{ reject(); });
        })
    } 
}