import {html} from "lit-html";
import auth from "../components/auth";
import "./index.sass"
import {AccountStatus} from "../control/AuthManager";
import globals from "../globals";

export default (authStatus: AccountStatus | null, showRegister: boolean) => {
    if (authStatus == null) {
        return html`<h1>Unable to check login status</h1>` // TODO: Proper error handling
    } else if (authStatus.isLoggedIn) {
        globals.router.navigate("/stream");
        return null;
    } else {
        return html`
        <div class="index-flex">
            <div class="flex-left">
                
            </div>
            <div class="flex-right">
                ${auth(showRegister)}
            </div>
        </div>
        `;
    }
}