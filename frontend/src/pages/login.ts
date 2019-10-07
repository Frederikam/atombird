import {html} from 'lit-html';
import './login.sass'

const login = html`
<div id="login-box">
    TODO Login!
</div>`;

const register = html`
<div id="login-box">
    TODO Register!
</div>`;

export default (isLogin: boolean) => isLogin ? login : register;