import axios, {AxiosResponse} from "axios";
import globals from "../globals";

class Api {

    getStatus(): Promise<AxiosResponse<any>> {
        return axios.get(globals.accountStatusUrl, {
            headers: {Authorization: localStorage.getItem("token")}
        });
    }

    getEntries(): Promise<Array<Entry>> {
        return new Promise<Array<Entry>>((resolve, reject) => {
            axios.get(globals.entriesUrl, {
                headers: {Authorization: localStorage.getItem("token")}
            })
                .catch((e) => reject(e))
                .then((request) => {
                    // @ts-ignore
                    resolve(request.data)
                })
        });
    }

}

class Entry {
}

export default new Api();