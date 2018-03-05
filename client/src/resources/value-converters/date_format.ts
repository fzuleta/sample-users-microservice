import * as moment from 'moment';

export class DateFormatValueConverter {
   toView(value, format) {
        if (format == "epoch") {
           return moment.unix(value).format('M/D/YYYY');
        }
      return moment(value).format('M/D/YYYY');
   }
}