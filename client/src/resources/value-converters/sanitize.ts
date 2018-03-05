import constants from "./../../_/data/constants";
import {trace, sanitize} from './../../_/common/functions';

export class SanitizeValueConverter {
    toView(value) {
        if (typeof value === "undefined" || ! value) { value = ""; }
        return sanitize(value);
    } 
}