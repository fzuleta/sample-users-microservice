import {bindable, inject, customElement} from 'aurelia-framework'; 
import {trace} from "./../../../_/common/functions";
import * as $ from "jquery"

@customElement('button-0') // Define the name of our custom element
@inject(Element) // Inject the instance of this element
export class Button_0 {
    element;


    btnclass="";

    @bindable iconsrc="";
    @bindable iconwidth="30";
    @bindable iconheight="30";
    @bindable text="";
    @bindable width="100%";
    @bindable height="50px";
    @bindable btntype="";
    @bindable extra_class=""; 

    div;
    constructor(element) {
        this.element = element;
    }

    // this is called by aurelia on the bindeable changed
    btntypeChanged(){ 
        switch (this.btntype) {
            case "blue":
                this.btnclass = "button-0-flex-center button-0-blue-custom-element " + this.extra_class;
                break;
            
            default: 
                this.btnclass = "button-0-flex-center button-0-empty-custom-element " + this.extra_class;
                break;
        }
    }
    attached(){ 
        this.div.style.width = this.width;
        this.div.style.height = this.height;
        
        this.btntypeChanged();
    }
}