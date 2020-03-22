const Navigo = require('navigo');

let apiBaseUrl = "http://localhost:9090/";

export default {
    router: new Navigo(location.origin),
    apiBaseUrl: apiBaseUrl,
    accountRegisterUrl: apiBaseUrl + "account/register",
    accountLoginUrl: apiBaseUrl + "account/login",
    accountStatusUrl: apiBaseUrl + "account/status",
    entriesUrl: apiBaseUrl + "stream",
}