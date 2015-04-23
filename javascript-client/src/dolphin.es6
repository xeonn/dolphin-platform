"use strict";
try {
    require('babelify/polyfill');
} catch (e) {
    // This will throw an error if polyfill is
    // already required.
}

const exists = require('./utils.js').exists;

import {ClassRepository} from './classrepo.js'

const DOLPHIN_BEAN = '@@@ DOLPHIN_BEAN @@@';
const DOLPHIN_ENUM = '@@@ DOLPHIN_ENUM @@@';
const DOLPHIN_LIST_ADD_FROM_SERVER = '@@@ LIST_ADD_FROM_SERVER @@@';
const DOLPHIN_LIST_DEL_FROM_SERVER = '@@@ LIST_DEL_FROM_SERVER @@@';
const DOLPHIN_LIST_SET_FROM_SERVER = '@@@ LIST_SET_FROM_SERVER @@@';
const DOLPHIN_LIST_ADD_FROM_CLIENT = '@@@ LIST_ADD_FROM_CLIENT @@@';
const DOLPHIN_LIST_DEL_FROM_CLIENT = '@@@ LIST_DEL_FROM_CLIENT @@@';
const DOLPHIN_LIST_SET_FROM_CLIENT = '@@@ LIST_SET_FROM_CLIENT @@@';

export function connect(url, config) {
    console.debug('connect called', url);
    return new Dolphin(url, config);
}

class Dolphin {
    constructor(url, config) {
        this.dolphin = opendolphin.dolphin(url, true, 4);
        if (exists(config)) {
            if (config.serverPush) {
                this.dolphin.startPushListening('ServerPushController:longPoll', 'ServerPushController:release');
            }
        }
        this.classRepository = new ClassRepository();
        this.addedHandlers = new Map();
        this.removedHandlers = new Map();
        this.allAddedHandlers = [];
        this.allRemovedHandlers = [];

        this.dolphin.getClientModelStore().onModelStoreChange(event => {
            const model = event.clientPresentationModel;
            const type = model.presentationModelType;
            if (event.eventType === opendolphin.Type.ADDED) {
                switch (type) {
                    case DOLPHIN_BEAN:
                        this.classRepository.registerClass(model);
                        break;
                    case DOLPHIN_ENUM:
                        this.classRepository.registerEnum(model);
                        break;
                    case DOLPHIN_LIST_ADD_FROM_SERVER:
                        this.classRepository.addListEntry(model);
                        this.dolphin.getClientModelStore().deletePresentationModel(model);
                        break;
                    case DOLPHIN_LIST_DEL_FROM_SERVER:
                        this.classRepository.delListEntry(model);
                        this.dolphin.getClientModelStore().deletePresentationModel(model);
                        break;
                    case DOLPHIN_LIST_SET_FROM_SERVER:
                        this.classRepository.setListEntry(model);
                        this.dolphin.getClientModelStore().deletePresentationModel(model);
                        break;
                    case DOLPHIN_LIST_ADD_FROM_CLIENT:
                    case DOLPHIN_LIST_DEL_FROM_CLIENT:
                    case DOLPHIN_LIST_SET_FROM_CLIENT:
                        // do nothing
                        break;
                    default:
                        const bean = this.classRepository.load(model);
                        const handlerList = this.addedHandlers.get(type);
                        if (exists(handlerList)) {
                            for (let handler of handlerList) {
                                handler(bean);
                            }
                        }
                        for (let handler of this.allAddedHandlers) {
                            handler(bean);
                        }
                        break;
                }
            } else if (event.eventType === opendolphin.Type.REMOVED) {
                switch (type) {
                    case DOLPHIN_BEAN:
                        this.classRepository.unregisterClass(model);
                        break;
                    case DOLPHIN_ENUM:
                        this.classRepository.unregisterEnum(model);
                        break;
                    case DOLPHIN_LIST_ADD_FROM_SERVER:
                    case DOLPHIN_LIST_DEL_FROM_SERVER:
                    case DOLPHIN_LIST_SET_FROM_SERVER:
                    case DOLPHIN_LIST_ADD_FROM_CLIENT:
                    case DOLPHIN_LIST_DEL_FROM_CLIENT:
                    case DOLPHIN_LIST_SET_FROM_CLIENT:
                        // do nothing
                        break;
                    default:
                        const bean = this.classRepository.unload(model);
                        if (exists(bean)) {
                            const handlerList = this.removedHandlers.get(type);
                            if (exists(handlerList)) {
                                for (let handler of handlerList) {
                                    handler(bean);
                                }
                            }
                            for (let handler of this.allRemovedHandlers) {
                                handler(bean);
                            }
                        }
                        break;
                }
            }
        });
    }

    onAdded(type, eventHandler) {
        if (!exists(eventHandler)) {
            this.allAddedHandlers.push(type);
        } else {
            let handlerList = this.addedHandlers.get(type);
            if (!exists(handlerList)) {
                handlerList = [];
                this.addedHandlers.set(type, handlerList);
            }
            handlerList.push(eventHandler);
        }

        return this;
    }

    onRemoved(type, eventHandler) {
        if (!exists(eventHandler)) {
            this.allRemovedHandlers.push(type);
        } else {
            let handlerList = this.removedHandlers.get(type);
            if (!exists(handlerList)) {
                handlerList = [];
                this.removedHandlers.set(type, handlerList);
            }
            handlerList.push(eventHandler);
        }

        return this;
    }

    send(command, params) {
        if (exists (params)) {
            const attributes = [];
            for (let prop in params) {
                if (params.hasOwnProperty(prop)) {
                    const value = params[prop];
                    if (typeof value === 'object') {
                        attributes.push(this.dolphin.attribute(prop, null, this.classRepository.beanToDolphin.get(value), 'VALUE'));
                        attributes.push(this.dolphin.attribute(prop, null, 'DOLPHIN_BEAN', 'VALUE_TYPE'))
                    } else {
                        attributes.push(this.dolphin.attribute(prop, null, value, 'VALUE'));
                        attributes.push(this.dolphin.attribute(prop, null, 'BASIC_TYPE', 'VALUE_TYPE'))
                    }
                }
            }
            this.dolphin.presentationModel(null, '@@@ DOLPHIN_PARAMETER @@@', ...attributes);
        }
        this.dolphin.send(command);

        return this;
    }
}
