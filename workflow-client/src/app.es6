"use strict";
try {
    require('babelify/polyfill');
} catch (e) {
    // This will throw an error if polyfill is
    // already required.
}

import * as dolphin from '../../javascript-client/dist/dolphin.min.js';

const SERVER_URL = 'http://localhost:8080/dolphin';

global.dolphin = dolphin.connect(SERVER_URL, {serverPush: true});

global.dolphin
    .onAdded('WorkflowViewModel',
        (data) => {
            document.querySelector('workflow-main-view').data = data;
        }
    )
    .send('WorkflowController:init');
