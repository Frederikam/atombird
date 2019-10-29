import {html, render, TemplateResult} from 'lit-html';
import layout from './layout';
import index from './pages/index';
import 'normalize.css'
import globals from "./globals"
import auth from "./control/AuthManager"

globals.router.on("/", () => {
    if (auth.isStatusKnown()) {
        renderLayout(index(auth.getStatusIfKnown(), true));
    } else {
        renderLayout(null);
    }
}).on("/login", () => {
    if (auth.isStatusKnown()) {
        renderLayout(index(auth.getStatusIfKnown(), false));
    } else {
        renderLayout(null);
    }
}).notFound(() => {
    renderLayout("404. Nothing here.")
}).resolve();

function renderLayout(body: TemplateResult | string | null) {
    render(layout(body), document.body);
}

auth.getStatus(status => {
    globals.router.forceNavigate(window.location.pathname)
});
