import {html} from "lit-html";
import StreamManager from "../control/StreamManager";
import "./stream.sass"

function streamMain() {
    if (StreamManager.isLoading) {
        return html`
        <div class="stream-main">
            <h1>Loading...</h1>
        </div>`
    } else if (StreamManager.loadError != null) {
        return html`
        <div class="stream-main">
            <h1>${StreamManager.loadError}</h1>
        </div>`
    }

    return html`
    <div class="stream-main">
        ${StreamManager.entries.map(e => html`<p>${e.url}</p>`)}
    </div>`
}

function streamSidebar() {
    return html`
    <div class="stream-margin-left">
        <div class="stream-sidebar">
        </div>
    </div>
    `
}

export default () => {
    // noinspection JSIgnoredPromiseFromCall
    if (!StreamManager.startedLoading) StreamManager.load();

    return html`
    <div class="stream">
        ${streamSidebar()}
        ${streamMain()}
        <div class="stream-margin-right"></div>
    </div>`
}