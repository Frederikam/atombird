import {html} from 'lit-html';
import globals from "../globals";
import axios from 'axios';
import Util from "../Util";
import './auth.sass'

function doRegister(event: Event) {
    event.preventDefault();
    let reqBody = getFormValues();
    let submitButton = document.getElementById("auth-submit") as HTMLInputElement;
    submitButton.setAttribute("disabled", "true");

    axios.post(globals.accountRegisterUrl, reqBody)
        .then(onAuthenticated, onError)
}

function doLogin(event: Event) {
    // TODO
    event.preventDefault()
}

function onAuthenticated(response: any) {
    localStorage.setItem("token", response.data.token)
}

function onError(payload: any) {
    let submitButton = document.getElementById("auth-submit") as HTMLInputElement;
    submitButton.removeAttribute("disabled");
    // @ts-ignore
    document.getElementById("auth-error").innerText = Util.extractMessageFromAxiosError(payload);
}

function getFormValues() {
    return {
        email: (document.getElementById("auth-email") as HTMLInputElement).value,
        password: (document.getElementById("auth-password") as HTMLInputElement).value
    }
}

const register = html`
<div id="auth-box-outer">
    <div id="auth-box">
        <h2 class="auth-header">Sign up for Atombird</h2>
        <form @submit=${(e: Event) => { doRegister(e) }}>
            <input id="auth-email" type="email" required="true" placeholder="you@yourdomain.tld">
            <input id="auth-password" type="password" required="true" placeholder="Password">
            <p id="auth-error"></p>
            <input id="auth-submit" type="submit" value="Sign up">
        </form>
    </div>
    <p class="auth-switch" @click=${(e: Event) => {globals.router.navigate("/login")}}>
        Already have an account?
    </p>
</div>`;

const auth = html`
<div id="auth-box-outer">
    <div id="auth-box">
        <h2 class="auth-header">Welcome back</h2>
        <form @submit=${(e: Event) => { doLogin(e) }}>
            <input id="auth-email" type="email" required="true" placeholder="you@yourdomain.tld">
            <input id="auth-password" type="password" required="true" placeholder="Password">
            <p id="auth-error"></p>
            <input id="auth-submit" type="submit" value="Login">
        </form>
    </div>
    <p class="auth-switch" @click=${() => {globals.router.navigate("/")}}>
        Need an account?
    </p>
</div>`;

export default (showRegister: boolean) => showRegister ? register : auth;