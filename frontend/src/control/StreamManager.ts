import Api from "./Api";

class StreamManager {
    startedLoading = false;
    isLoading = false;

    startLoading() {
        this.startedLoading = true;
        this.isLoading = true;
        Api.getEntries().then(value => console.log(value))
    }
}

export default new StreamManager()