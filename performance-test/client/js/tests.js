"use strict";
var SERVER_URL = 'http://localhost:8080/dolphin';
var dolphin = opendolphin.dolphin(SERVER_URL, true, 4);

var listeners = [];

dolphin.getClientModelStore().onModelStoreChange(function(event) {
    listeners.forEach(function(listener) {
        (typeof listener === 'function') && listener(event);
    });
});


function testPmCreateDelete() {
    var modelType = 'PM_CREATE_DELETE';
    var done = false;
    var counter = 0;
    var start = performance.now();
    var func = function(event) {
        if (event.eventType === opendolphin.Type.REMOVED && event.clientPresentationModel.presentationModelType === modelType) {
            counter++;
            if (done) {
                var end = performance.now();
                console.log('Avg. throughput: ' + Math.round(1000 * counter / (end - start)) + ' round trips / second');
                console.log('Avg. response time: ' + Math.round((end - start) / counter) + ' ms');
                listeners.splice(listeners.indexOf(func), 1);
                dolphin.send('PMCreateDeleteController:tearDown');
            } else {
                dolphin.presentationModel(null, 'PM_CREATE_DELETE');
            }
        }
    };
    listeners.push(func);
    setTimeout(function() {
        done = true;
        dolphin.send('PMCreateDeleteController:tearDown');
    }, 20 * 1000);
    dolphin.send('PMCreateDeleteController:setUp');
    dolphin.presentationModel(null, 'PM_CREATE_DELETE');
}