import {html, render, TemplateResult} from 'lit-html';
import layout from './layout';
import index from './pages/index';
import 'normalize.css'
import globals from "./globals"
import auth from "./control/AuthManager"

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
            console.log("Navigating to root / because we are logged out");
            globals.router.navigate("/", true)
        } else {
            done()
        }
    }
};

globals.router.on(() => {
    console.log("/");
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
    .notFound(() => {
        console.log("Landed on 404 page");
        renderLayout("404. Nothing here.")
    })
    .resolve();

function renderLayout(body: TemplateResult | string | null) {
    render(layout(auth.getStatusIfKnown(), body), document.body);
}

auth.getStatus(status => {
    globals.router.forceNavigate(window.location.pathname, true)
});
