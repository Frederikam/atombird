import {html, render} from 'lit-html';
import layout from './layout';
import index from './pages/index';
import 'normalize.css'
import globals from "./globals"

globals.router.on("/", () => {
    render(layout(index(true)), document.body);
}).on("/login", () => {
    render(layout(index(false)), document.body);
}).notFound(() => {
    // @ts-ignore
    render(layout("Nothing's here"), document.body);
}).resolve();
