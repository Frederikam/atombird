class StreamManager {
    startedLoading = false;
    isLoading = false;

    startLoading() {
        this.startedLoading = true;
        this.isLoading = true;
    }
}

export default new StreamManager()