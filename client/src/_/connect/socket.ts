import {inject} from "aurelia-framework"
import {EventAggregator} from 'aurelia-event-aggregator';
import User from './../data/user';
import {trace} from "./../common/functions";

@inject("socketUrl",EventAggregator,User)
export default class ltSocket {
    socketUrl;
    eventAggregator;
    user;
    ws = null;
    closed = true;
    prevSocketId = 0;
    socketId = 0;
    evt_justLoggedIn;

    constructor(socketUrl,EventAggregator,user) {
        this.socketUrl=socketUrl;
        this.eventAggregator = EventAggregator;
        this.user = user;
        this.ws = null;
        this.closed = true;
        this.prevSocketId = 0;
        this.socketId = 0;
        this.start();

        this.evt_justLoggedIn = this.eventAggregator.subscribe('justloggedIn', (payload:any) => { this.justLoggedIn(payload); });
    }
    restart(){
        this.close();
        this.start();
    }
    justLoggedIn(params:any=null) {
        if(this.ws) { 
            // In case we want to send a msg after login?
        }
    }
    close(){
        if(!this.ws) { return; }

        trace("Closing ws");
        this.ws.onopen = function () {};    
        this.ws.onmessage = function () {}; 
        this.ws.onerror = function () {}; 
        this.ws.onclose = function () {}; 
        this.ws.close();

        this.ws = null;
        this.closed = true;
    }
    sendMessage(o = {}) {
        o = JSON.stringify(o);
        if(this.ws && !this.closed) { this.ws.send(o); }
    }
    start() {
        trace("Starting ws");
        if(!this.closed) return;

        this.ws = new WebSocket(this.socketUrl);

        this.ws.onopen = () => {
            this.closed = false;
            trace("Socket Opened!");

            //If the conection restarted or sth
            if (this.user.isAuthenticatedOrRemembered) {
                this.justLoggedIn();
            }

            this.socketId++;
        };

        this.ws.onmessage = (evt) => {
            this.closed = false;
            trace("Message: " + evt.data);
            const o = typeof evt.data === "string" ? JSON.parse(evt.data) : evt.data;
            if (!o) return;
            this.eventAggregator.publish('socketUpdate', o);
        };

        this.ws.onclose = () => {
            this.closed = true;
            trace("Closed!");
        };

        this.ws.onerror = (err) => {
            trace("Error: " + err);
        };
    }
}