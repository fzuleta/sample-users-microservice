import constants from "./../../_/data/constants";
import {trace, sanitize} from './../../_/common/functions';

export class TrimValueConverter {
    toView(value, spaces = 15, addDots=true) {
        if (typeof value === "undefined" || ! value) { value = ""; }
        let newValue = value.slice(0, spaces) + (addDots && value.length > spaces ? "..." : "")
        return sanitize(newValue);
    } 
}