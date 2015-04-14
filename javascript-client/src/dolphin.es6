"use strict";
if (!global._babelPolyfill) {
    require('babelify/polyfill');
}

import {ClassRepository} from './classrepo.es6'
import {exists} from './helpers.es6'

const DOLPHIN_BEAN = '@@@ DOLPHIN_BEAN @@@';
const DOLPHIN_ENUM = '@@@ DOLPHIN_ENUM @@@';
const DOLPHIN_LIST_ADD_FROM_SERVER = '@@@ LIST_ADD_FROM_SERVER @@@';
const DOLPHIN_LIST_DEL_FROM_SERVER = '@@@ LIST_DEL_FROM_SERVER @@@';
const DOLPHIN_LIST_SET_FROM_SERVER = '@@@ LIST_SET_FROM_SERVER @@@';
const DOLPHIN_LIST_ADD_FROM_CLIENT = '@@@ LIST_ADD_FROM_CLIENT @@@';
const DOLPHIN_LIST_DEL_FROM_CLIENT = '@@@ LIST_DEL_FROM_CLIENT @@@';
const DOLPHIN_LIST_SET_FROM_CLIENT = '@@@ LIST_SET_FROM_CLIENT @@@';

export function connect(url) {
    console.debug('connect called', url);
    return new Dolphin(url);
}

class Dolphin {
    constructor(url) {
        this.dolphin = opendolphin.dolphin(url, true);
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

    send(command) {
        this.dolphin.send(command);

        return this;
    }
}
