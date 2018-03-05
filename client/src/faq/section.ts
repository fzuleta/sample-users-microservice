import {bindable, inject, customElement} from 'aurelia-framework';
import {EventAggregator} from 'aurelia-event-aggregator';
import {I18N} from 'aurelia-i18n'; 
import {range} from "../_/common/functions";

@inject(EventAggregator, I18N)
export default class Section {
    eventAggregator:EventAggregator;
    public questions = [];
    public tokenQuestions = [];
    public i18n:I18N;
    public evt_updatelang;

    constructor(EventAggregator, i18n) { 
        this.eventAggregator = EventAggregator
        this.i18n = i18n;
    }
    attached() { 
        this.evt_updatelang = this.eventAggregator.subscribe('change_lang', payload => {
            this.questions = [];
            this.tokenQuestions = [];
            setTimeout(()=>this.refresh(), 16);
        });
        this.refresh();
    }
    deactivate() { 
        if(this.evt_updatelang)    { this.evt_updatelang.dispose(); } 
    }
    refresh() {
        this.questions = range(10); 
    }
    getTitle(section, q) {
        return this.i18n.tr(`faq:${section}.q${q}.title`);
    }
    getBody(section, q) {
        return this.i18n.tr(`faq:${section}.q${q}.body`);
    }
}