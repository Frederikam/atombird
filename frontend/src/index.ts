import {html, render} from 'lit-html';
import layout from './layout';
import login from './pages/login';
import 'normalize.css'
const Navigo = require('navigo');

const router = new Navigo();

router.on("/login", () => {
    render(layout(login(true)), document.body);
}).on("/register", () => {
    render(layout(login(false)), document.body);
}).notFound(() => {
    // @ts-ignore
    render(layout("Nothing's here"), document.body);
}).resolve();