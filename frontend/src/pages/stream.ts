import {html} from "lit-html";
import StreamManager from "../control/StreamManager";
import "./stream.sass"

function streamMain() {
    if (StreamManager.isLoading) {
        return html`
        <div class="stream-main">
            <h1>Loading...</h1>
        </div>`
    }

    return html`
    <div class="stream-main">
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
    if (!StreamManager.startedLoading) StreamManager.startLoading();

    return html`
    <div class="stream">
        ${streamSidebar()}
        ${streamMain()}
        <div class="stream-margin-right"></div>
    </div>`
}