"use strict";
try {
    require('babelify/polyfill');
} catch (e) {
    // This will throw an error if polyfill is
    // already required.
}

const SERVER_URL = 'http://localhost:8080/dolphin';

var connector = require('../bower_components/dolphin-js/dist/dolphin.min.js');

var dolphin = global.dolphin = connector.connect(SERVER_URL, {serverPush: false});

dolphin.onAdded('WorkflowViewModel',
    data => document.querySelector('workflow-main-view').data = data
);

dolphin.send('WorkflowController:init');
