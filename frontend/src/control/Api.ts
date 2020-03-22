import axios, {AxiosResponse} from "axios";
import globals from "../globals";

class Api {

    getStatus(): Promise<AxiosResponse<any>> {
        return axios.get(globals.accountStatusUrl, {
            headers: {Authorization: localStorage.getItem("token")}
        });
    }

    async getEntries(): Promise<Array<Entry>> {
        const r = await axios.get(globals.entriesUrl, { headers: {
                Authorization: localStorage.getItem("token"),
            }});

        return r.data;
    }

}

interface Entry {
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
