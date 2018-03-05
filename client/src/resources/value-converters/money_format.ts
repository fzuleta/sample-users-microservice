import * as numeral from 'numeral';
import constants from "./../../_/data/constants";
import {trace} from "./../../_/common/functions";
import {inject} from 'aurelia-framework';

export class MoneyFormatValueConverter {
    toView(value, currency) {
        if (typeof currency === "undefined") { currency = constants.USD_2_cents; }
        if (typeof value === "undefined" || ! value) { value = 0; }
        if (currency == constants.USD_2_cents) {
            return "" + numeral(value).format('$0,0.00');
        } else if (currency == constants.USD_3_cents) {
            return "" + numeral(value).format('$0,0.000');
        } else if (currency == constants.MONEY) {
            return "" + numeral(value).format('0,0.000');
        }
    } 
}