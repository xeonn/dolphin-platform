"use strict";
require("babelify/polyfill");

import * as dolphin from './dolphin_client.es6';

var SERVER_URL = "http://localhost:8080/dolphin";

dolphin.connect(SERVER_URL)

    .onAdded('Questionnaire',
        (data) => {
            document.querySelector('icos-questionnaire').data = data;
        }
    )

    .send('COMMAND_INIT');
