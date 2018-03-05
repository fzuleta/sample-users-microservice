import {bindable, inject, customElement} from 'aurelia-framework';
import {DialogService} from 'aurelia-dialog';
import {GenericError} from './../../resources/elements/dialogs/generic_error';
import {EventAggregator} from 'aurelia-event-aggregator';

@inject(EventAggregator, DialogService)
export default class Alerts {
    eventAggregator;
    dialogService;
    constructor(eventAggregator, DialogService) {
        this.eventAggregator = eventAggregator;
        this.dialogService = DialogService;
    }
    showGenericError(payload){
        return this.dialogService.open({ viewModel: GenericError, model: payload});
    }

}