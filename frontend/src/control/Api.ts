import axios from "axios";
import globals from "../globals";
import {AccountStatus} from "./AuthManager";

class Api {

    async getStatus(): Promise<AccountStatus> {
        const res = await axios.get(globals.apiBaseUrl + "account/status", {
            headers: {Authorization: localStorage.getItem("token")}
        });
        return new AccountStatus(true, res.data.email);
    }

    async getEntries(): Promise<Array<Entry>> {
        const r = await axios.get(globals.apiBaseUrl + "stream", {
            headers: {Authorization: localStorage.getItem("token")}
        });

        return r.data;
    }

}

export interface Entry {
    id: Number
    feedId: Number
    nativeId: String
    time: String
    url: String
    title: String
    summary: String
    content: String
    titleType: String
    summaryType: String
    contentType: String
    imageUrl: String
    authorName: String
    authorEmail: String
    authorUrl: String
    read: Boolean
}

export default new Api();
