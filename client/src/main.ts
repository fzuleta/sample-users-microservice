import {Aurelia} from 'aurelia-framework'
import environment from './environment';
import * as Backend from 'i18next-xhr-backend';

export function configure(aurelia: Aurelia) {
  // Api root we can change after release
  const thedomain = location.hostname;
  console.log("THE DOMAIN: " + thedomain);
  
  aurelia.use.instance("apiRoot",   `https://${thedomain}:11190`);
  aurelia.use.instance("socketUrl", `wss://${thedomain}:11190/socket`);

  aurelia.use
    .standardConfiguration()
    .feature('resources')
    .plugin('aurelia-dialog', config => {
      config.useDefaults();
      config.settings.lock = false;
      config.settings.centerHorizontalOnly = false;
      config.settings.startingZIndex = 1005;
    }) 
    .plugin('aurelia-i18n', (instance) => {
      // register backend plugin
      instance.i18next.use(Backend);
      var lang = localStorage.getItem("lang") || "en";

      return instance.setup({
        backend: {                                  
          loadPath: './locales/{{lng}}/{{ns}}.json', 
        },
        lng : lang,
        ns: ['translation', 'home', 'loginreg', 'profile'],
        defaultNS: 'translation',
        attributes : ['t','i18n'],
        fallbackLng : 'en',
        debug : false
      });
    });

  if (environment.debug) {
    aurelia.use.developmentLogging();
  }

  if (environment.testing) {
    aurelia.use.plugin('aurelia-testing');
  }

  aurelia.start().then(() => aurelia.setRoot());
}
