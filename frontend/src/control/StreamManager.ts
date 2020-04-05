import Api, {Entry} from "./Api";
import globals from "../globals";

class StreamManager {
    startedLoading = false;
    isLoading = false;
    loadError: String | null = null;
    entries: Array<Entry> = [];

    async load() {
        this.startedLoading = true;
        this.isLoading = true;
        try {
            this.entries = await Api.getEntries();
        } catch (e) {
            this.loadError = e;
        }
        this.isLoading = false;
        globals.router.forceNavigate();
    }

    private onLoad(entries: Array<Entry>) {
        this.entries = entries;
    }

    private onLoadFail(error: String) {
        this.loadError = error;
    }
}

export default new StreamManager()
