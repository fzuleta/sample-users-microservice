import * as $ from "jquery";

export function isNumeric(str) {
    if (str == null){
        return false;
    }
    var regx = /^[-+]?\d*\.?\d+(?:[eE][-+]?\d+)?$/;
    return regx.test(str);
}

export function trimString(str, n) {
    if (str.length>n) { str = str.substr(0,n) + "..."; }
    return str;
}
export function sanitize(value) { 
    return window["xssFilters"].inHTMLData(value);
}

export function validateEmail(email) {
    if (email.length < 4) { return false; }
    let re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}
export function validPassword(p0, p1) {
    if (p0 !== p1) { return false; } 
    if (p0.length < 6) { return false; } 
    // if (!(/[A-Z]/.test(p0))) { return false; } 
    // if (!(/[a-z]/.test(p0))) { return false; } 
    // if (!(/\d/.test(p0))) { return false; } 
    return true;
}

export function validateTeamName(name) {
    let error = "";
    if (name == "") {                                error = "Team must have a name"
    } else if (name.substring(0, 1) == " ") {        error = "Team must not start with a space";
    // } else if (this.new_teamName.indexOf(" ") > -1) {    approve = "Team must not have spaces";
    } else if (name.length < 3) {                    error = "Team must have at least 3 characters";
    }

    return error;
}

export function randRange(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}
export function rangeBetween(a, b) {
    const list = [];
    for (var i = a; i <= b; i++) {
        list.push(i);
    }
    return list;
}
export function generateString(char=5) {
    var text = "";
    var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    for( var i=0; i < char; i++ )
        text += possible.charAt(Math.floor(Math.random() * possible.length));

    return text;
}

export function isMobile() {
    const isAndroid = this.isAndroid();
    const isIOS = this.isIOS();
    const isBlackberry = this.isBlackberry();
    return isAndroid || isIOS || isBlackberry;
}
export function isAndroid() { return /android/i.test(navigator.userAgent.toLowerCase()); }
export function isIOS() { return /iphone|ipad|ipod/i.test(navigator.userAgent.toLowerCase()); }
export function isBlackberry() { return /blackBerry/i.test(navigator.userAgent.toLowerCase()); }
    


let countries = null;
export function getCountriesThatHaveFlag(){
    if (countries != null) { return countries;}
    const str = "Abkhazia; Afghanistan; Aland; Albania; Algeria; American-Samoa; Andorra; Angola; Anguilla; Antarctica; Antigua-and-Barbuda; Argentina; Armenia; Aruba; Australia; Austria; Azerbaijan; Bahamas; Bahrain; Bangladesh; Barbados; Basque-Country; Belarus; Belgium; Belize; Benin; Bermuda; Bhutan; Bolivia; Bosnia-and-Herzegovina; Botswana; Brazil; British-Antarctic-Territory; British-Virgin-Islands; Brunei; Bulgaria; Burkina-Faso; Burundi; Cambodia; Cameroon; Canada; Canary-Islands; Cape-Verde; Cayman-Islands; Central-African-Republic; Chad; Chile; China; Christmas-Island; Cocos-Keeling-Islands; Colombia; Commonwealth; Comoros; Cook-Islands; Costa-Rica; Cote-dIvoire; Croatia; Cuba; Curacao; Cyprus; Czech-Republic; Democratic-Republic-of-the-Congo; Denmark; Djibouti; Dominica; Dominican-Republic; East-Timor; Ecuador; Egypt; El-Salvador; England; Equatorial-Guinea; Eritrea; Estonia; Ethiopia; European-Union; Falkland-Islands; Faroes; Fiji; Finland; France; French-Polynesia; French-Southern-Territories; Gabon; Gambia; Georgia; Germany; Ghana; Gibraltar; GoSquared; Greece; Greenland; Grenada; Guam; Guatemala; Guernsey; Guinea-Bissau; Guinea; Guyana; Haiti; Honduras; Hong-Kong; Hungary; Iceland; India; Indonesia; Iran; Iraq; Ireland; Isle-of-Man; Israel; Italy; Jamaica; Japan; Jersey; Jordan; Kazakhstan; Kenya; Kiribati; Kosovo; Kuwait; Kyrgyzstan; Laos; Latvia; Lebanon; Lesotho; Liberia; Libya; Liechtenstein; Lithuania; Luxembourg; Macau; Macedonia; Madagascar; Malawi; Malaysia; Maldives; Mali; Malta; Mars; Marshall-Islands; Martinique; Mauritania; Mauritius; Mayotte; Mexico; Micronesia; Moldova; Monaco; Mongolia; Montenegro; Montserrat; Morocco; Mozambique; Myanmar; NATO; Nagorno-Karabakh; Namibia; Nauru; Nepal; Netherlands-Antilles; Netherlands; New-Caledonia; New-Zealand; Nicaragua; Niger; Nigeria; Niue; Norfolk-Island; North-Korea; Northern-Cyprus; Northern-Mariana-Islands; Norway; Olympics; Oman; Pakistan; Palau; Palestine; Panama; Papua-New-Guinea; Paraguay; Peru; Philippines; Pitcairn-Islands; Poland; Portugal; Puerto-Rico; Qatar; Red-Cross; Republic-of-the-Congo; Romania; Russia; Rwanda; Saint-Barthelemy; Saint-Helena; Saint-Kitts-and-Nevis; Saint-Lucia; Saint-Martin; Saint-Vincent-and-the-Grenadines; Samoa; San-Marino; Sao-Tome-and-Principe; Saudi-Arabia; Scotland; Senegal; Serbia; Seychelles; Sierra-Leone; Singapore; Slovakia; Slovenia; Solomon-Islands; Somalia; Somaliland; South-Africa; South-Georgia-and-the-South-Sandwich-Islands; South-Korea; South-Ossetia; South-Sudan; Spain; Sri-Lanka; Sudan; Suriname; Swaziland; Sweden; Switzerland; Syria; Taiwan; Tajikistan; Tanzania; Thailand; Togo; Tokelau; Tonga; Trinidad-and-Tobago; Tunisia; Turkey; Turkmenistan; Turks-and-Caicos-Islands; Tuvalu; US-Virgin-Islands; Uganda; Ukraine; United-Arab-Emirates; United-Kingdom; United-Nations; United-States; Unknown; Uruguay; Uzbekistan; Vanuatu; Vatican-City; Venezuela; Vietnam; Wales; Wallis-And-Futuna; Western-Sahara; Yemen; Zambia; Zimbabwe";
    // const str = "Colombia";    
    let list = str.split("; ");
    let o = [];
    list.forEach(item => {
        o.push({name: item, value:item})
    });

    return o;
}

export function findCountryModel(n){
    countries = getCountriesThatHaveFlag();
    for (let i = 0; i<countries.length; i++) {
        if (n == countries[i].name) {
            return countries[i];
        }
    } 
    return null;
}
export function findCountry_i(n){
    countries = getCountriesThatHaveFlag();
    for (let i = 0; i<countries.length; i++) {
        if (n == countries[i].name) {
            return i;
        }
    } 
    return;
}
/** Compare function for sorting i.e   dates.sort(dynamicSort("date")) */
export function dynamicSort(property, sortOrder = 1) {
    if(property[0] === "-") {
        sortOrder = -1;
        property = property.substr(1);
    }
    return function (a,b) {
        const ar = Number(a[property]);
        const br = Number(b[property]);
        var result = (ar < br) ? -1 : (ar > br) ? 1 : 0;
        // trace("ar: " + ar);
        return result * sortOrder;
    }
}


export function trace(o, bgcolor="#f8eafc", color="#302207") {
    if (typeof console !== "undefined") {
        if (typeof o === "string") {
            console.log("%c"+o, `background: ${bgcolor}; color: ${color}`);
        } else {
            console.log(o);
        }
    }
}

export function range(n) {
    return Array.from(Array(n).keys());
}


export function elegibleCountries() {
    return [ 
    `Algeria`,
    `Argentina`,
    `Australia`,
    `Austria`,
    `Bangladesh`,
    `Belgium`,
    `Belarus`,
    `Bolivia`,
    `Brazil`,
    `Brunei`,
    `Bulgaria`,
    `Canada`,
    `Chile`,
    `China`,
    `Hong Kong`,
    `Colombia`,
    `Croatia`,
    `Cyprus`,
    `Czech Republic`,
    `Denmark`,
    `Dominican Republic`,
    `Egypt`,
    `Estonia`,
    `Finland`,
    `France & territories`,
    `Germany`,
    `Ghana`,
    `Greece`,
    `Honduras`,
    `Hungary`,
    `Iceland`,
    `India`,
    `Indonesia`,
    `Ireland`,
    `Israel`,
    `Italy`,
    `Kazakhstan`,
    `Japan`,
    `Kuwait`,
    `Latvia`,
    `Liechtenstein`,
    `Lithuania`,
    `Luxembourg`,
    `Macao`,
    `Malaysia`,
    `Malta`,
    `Mexico`,
    `Monaco`,
    `Morocco`,
    `Namibia`,
    `Netherlands & territories`,
    `New Zealand`,
    `Nicaragua `,
    `Nigeria`,
    `Norway`,
    `Paraguay`,
    `Pakistan`,
    `Peru`,
    `Philippines`,
    `Poland`,
    `Portugal`,
    `Qatar`,
    `Republic of Korea`,
    `Romania`,
    `Russia`,
    `San Marino`,
    `Saudi Arabia`,
    `Senegal`,
    `Serbia`,
    `Singapore`,
    `Slovakia`,
    `Slovenia`,
    `South Africa`,
    `Spain`,
    `Sweden`,
    `Switzerland`,
    `Taiwan`,
    `Thailand`,
    `Tunesia`,
    `Turkey`,
    // `U.S.A.`,
    `UAE`,
    `Ukraine`,
    `United Kingdom & U.K. other overseas territories`,
    `Venezuela`,
    `Vietnam`,].map(it=>{
        return {
            name: it,
            value: it.toLowerCase(),
        }
    })
}