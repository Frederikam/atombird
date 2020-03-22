import {html, render, TemplateResult} from 'lit-html';
import layout from './layout';
import index from './pages/index';
import 'normalize.css'
import globals from "./globals"
import auth from "./control/AuthManager"
import stream from "./pages/stream";

const requireLoggedOut = {
    before: function (done: any, params: any) {
        if (auth.isStatusKnown() && auth.getStatusIfKnown().isLoggedIn) {
            done(false);
            console.log("Navigating to /stream because we are not logged in");
            globals.router.navigate("/stream", true)
        } else {
            done()
        }
    }
};

const requireLoggedIn = {
    before: function (done: any, params: any) {
        if (auth.isStatusKnown() && !auth.getStatusIfKnown().isLoggedIn) {
            done(false);
            console.log("Navigating to /login because we are logged out");
            globals.router.navigate("/login", true)
        } else {
            done()
        }
    }
};

globals.router.on("/", () => {
    if (auth.isStatusKnown()) {
        renderLayout(index(auth.getStatusIfKnown(), true));
    } else {
        renderLayout(null);
    }
}, requireLoggedOut)
    .on("/login", () => {
        if (auth.isStatusKnown()) {
            renderLayout(index(auth.getStatusIfKnown(), false));
        } else {
            renderLayout(null);
        }
    }, requireLoggedOut)
    .on("/stream", () => {
        renderLayout(stream());
    }, requireLoggedIn)
    .notFound(() => {
        console.log("Landed on 404 page");
        renderLayout("404. Nothing here.")
    }).resolve();

function renderLayout(body: TemplateResult | string | null) {
    render(layout(auth.getStatusIfKnown(), body), document.body);
}

auth.getStatus(status => {
    globals.router.forceNavigate(window.location.pathname, true)
});
