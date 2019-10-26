import {html} from 'lit-html';
import './auth.sass'
import globals from "../globals";

function doRegister(event: Event) {
    console.log(event);
    event.preventDefault()
}

function doLogin(event: Event) {
    console.log(event);
    event.preventDefault()
}

const register = html`
<div id="auth-box-outer">
    <div id="auth-box">
        <h2 class="auth-header">Sign up for Atombird</h2>
        <form @submit=${(e: Event) => { doLogin(e) }}>
            <input class="auth-input" type="email" required="true" placeholder="you@yourdomain.tld">
            <input class="auth-input" type="password" required="true" placeholder="Password">
            <input class="auth-submit" type="submit" value="Sign up">
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
            <input class="auth-input" type="email" required="true" placeholder="you@yourdomain.tld">
            <input class="auth-input" type="password" required="true" placeholder="Password">
            <input class="auth-submit" type="submit" value="Login">
        </form>
    </div>
    <p class="auth-switch" @click=${() => {globals.router.navigate("/")}}>
        Need an account?
    </p>
</div>`;

export default (showRegister: boolean) => showRegister ? register : auth;