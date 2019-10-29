import {html, TemplateResult} from 'lit-html';
import './layout.sass'

export default (body: TemplateResult | string | null) => html`
<div id="inner-body">
    <header>
        <h1 id="title">Atombird</h1>
    </header>
    <div id="content-container">
        ${body}
    </div>
    <footer></footer>
</div>
`