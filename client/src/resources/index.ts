import {FrameworkConfiguration} from 'aurelia-framework';

export function configure(config: FrameworkConfiguration) {
  config.globalResources([
        "./elements/recaptcha",
        "./elements/buttons/button_0",
        "./value-converters/money_format",
        "./value-converters/date_format",
        "./value-converters/sanitize",
        "./value-converters/trim",
    ]);
}
