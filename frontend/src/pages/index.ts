import {html} from "lit-html";
import auth from "../components/auth";
import "./index.sass"

export default (showRegister: boolean) => html`
<div class="index-flex">
    <div class="flex-left">
        
    </div>
    <div class="flex-right">
        ${auth(showRegister)}
    </div>
</div>
`