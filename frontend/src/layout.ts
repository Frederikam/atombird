import {html, TemplateResult} from 'lit-html';
import './layout.sass'
import AuthManager, {AccountStatus} from "./control/AuthManager";
const md5 = require("md5");

export default (account: AccountStatus, body: TemplateResult | string | null) => html`
<div class="inner-body">
    <header>
        <h1 class="wordmark">Atombird</h1>
        ${accountDisplay(account)}
    </header>
    <div class="content-container">
        ${body}
    </div>
    <footer></footer>
</div>
`

function accountDisplay(account: AccountStatus) {
    if (!account.isLoggedIn) return html`<div class="account-status"></div>`;

    const hash = md5(account.email);
    const avatarUrl = `https://www.gravatar.com/avatar/${hash}?d=identicon&s=64`;

    // Attribution: SVG icon is licensed under CC-BY 4.0 from Font Awesome
    return html`
    <div class="account-status">
        <img class="avatar" src="${avatarUrl}" alt="avatar"/>
        <svg class="log-out-button" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512" 
                @click=${() => AuthManager.logOut()}>
            <path d="M497 273L329 441c-15 15-41 4.5-41-17v-96H152c-13.3 0-24-10.7-24-24v-96c0-13.3 10.7-24 
            24-24h136V88c0-21.4 25.9-32 41-17l168 168c9.3 9.4 9.3 24.6 0 34zM192 436v-40c0-6.6-5.4-12-12-12H96c-17.7 
            0-32-14.3-32-32V160c0-17.7 14.3-32 32-32h84c6.6 0 12-5.4 12-12V76c0-6.6-5.4-12-12-12H96c-53 0-96 43-96 
            96v192c0 53 43 96 96 96h84c6.6 0 12-5.4 12-12z"/>
        </svg>
    </div>
    `;
}