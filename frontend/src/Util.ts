import {AxiosResponse} from "axios";

class Util {
    extractMessageFromAxiosError(error: any) {
        if (error.response != null) {
            let response = error.response.data as any;
            return `${response.status} ${response.error}: ${response.message}`;
        }

        return error.toString();
    };
}

export default new Util()