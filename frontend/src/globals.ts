const Navigo = require('navigo');

let apiBaseUrl = "http://localhost:9090/";

export default {
    router: new Navigo(),
    apiBaseUrl: apiBaseUrl,
    accountRegisterUrl: apiBaseUrl + "account/register",
    accountLoginUrl: apiBaseUrl + "account/login"
}