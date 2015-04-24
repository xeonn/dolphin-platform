"use strict";
try {
    require('babelify/polyfill');
} catch (e) {
    // This will throw an error if polyfill is
    // already required.
}

const SERVER_URL = 'http://localhost:8080/dolphin';

var connector = require('../../javascript-client/dist/dolphin.min.js');

var dolphin = connector.connect(SERVER_URL, {serverPush: false});

dolphin.onAdded('Questionnaire',
    data => document.querySelector('icos-questionnaire').data = data
);

dolphin.send('COMMAND_INIT');
