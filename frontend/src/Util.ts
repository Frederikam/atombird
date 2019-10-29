import {AxiosResponse} from "axios";
const Navigo = require('navigo');

class Util {
    extractMessageFromAxiosError(error: any) {
        if (error.response != null) {
            let response = error.response.data as any;
            return `${response.status} ${response.error}: ${response.message}`;
        }

        return error.toString();
    };
}

Navigo.prototype.forceNavigate = function(path: string, absolute?: boolean) {
    if (absolute === undefined) {
        absolute = false;
    }
    if (this._lastRouteResolved) {
        this._lastRouteResolved.query = '_=' + Number(new Date());
    }
    this.navigate(path, absolute);
};

export default new Util()