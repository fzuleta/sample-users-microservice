import {bindable, inject, customElement} from 'aurelia-framework';
import {DialogController} from 'aurelia-dialog';

@inject(DialogController)
export class GenericError {
    dialogController:DialogController;
    css_warn = 'font-size:24px;';
    css_success = 'font-size:24px; color: green;';
    css_message = 'font-size:16px; text-align: center; white-space: pre; color:black;';
    error = { 
        title: '', 
        message: '',
        warn: false,
        error: false,
        success: true,
        cancel: true,
        ok: true
    };

    constructor(DialogController) {
        this.dialogController = DialogController;
        this.css_warn = 'font-size:24px;';
        this.css_success = 'font-size:24px; color: green;';
        this.css_message = 'font-size:16px; text-align: center; white-space: pre; color:black;';
    }

    activate(error){
        this.error = error;

        if (error.warning) {
            this.css_warn += 'color: orange;';
        }
        if (error.error) {
            this.css_warn += 'color: red;';
        }
    }
}