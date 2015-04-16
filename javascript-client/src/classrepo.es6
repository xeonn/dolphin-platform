"use strict";
import '../bower_components/observe-js/src/observe.js'
import {exists} from './helpers.es6'

const UNKNOWN = 0, BASIC_TYPE = 1, ENUM = 2, DOLPHIN_BEAN = 3;

const identity = value => value;

export class ClassRepository {

    constructor() {
        this.classes = new Map();
        this.beanFromDolphin = new Map();
        this.beanToDolphin = new Map();
        this.enums = new Map();
        this.classInfos = new Map();
    }

    setValueType(property, value) {
        if (!exists(value)) {
            return;
        }
        switch (value) {
            case ENUM:
                property.fromDolphin = (ord) => {
                    const enumInfo = this.enums.get(property.type);
                    return exists(enumInfo) ? enumInfo.fromDolphin[ord] : null;
                };
                property.toDolphin = (value) => {
                    const enumInfo = this.enums.get(property.type);
                    return exists(enumInfo) ? enumInfo.toDolphin.get(value) : null;
                };
                break;
            case DOLPHIN_BEAN:
                property.fromDolphin = (id) => this.beanFromDolphin.get(id);
                property.toDolphin = (bean) => this.beanToDolphin.get(bean);
                break;
        }
    }

    static setValue(property, value) {
        if (exists(value)) {
            property.type = value;
        }
    }

    registerClass(model) {
        if (this.classes.has(model.id)) {
            return;
        }
        console.debug('ClassRepository.registerClass', model);

        const classInfo = {};
        model.attributes.forEach(attribute => {
            let property = classInfo[attribute.propertyName];
            if (!exists(property)) {
                property = classInfo[attribute.propertyName] = {
                    fromDolphin: identity,
                    toDolphin: identity
                }
            }

            switch (attribute.tag) {
                case opendolphin.Tag.valueType():
                    attribute.onValueChange(event => {
                        if (event.oldValue !== event.newValue) {
                            this.setValueType(property, event.newValue);
                        }
                    });
                    break;
                case opendolphin.Tag.value():
                    attribute.onValueChange(event => {
                        if (event.oldValue !== event.newValue) {
                            ClassRepository.setValue(property, event.newValue);
                        }
                    });
                    break;
            }
        });
        this.classes.set(model.id, classInfo);
    }

    registerEnum(model) {
        if (this.enums.has(model.id)) {
            return;
        }
        console.debug('ClassRepository.registerEnum', model);

        const enumInfoFromDolphin = [];
        const enumInfoToDolphin = new Map();
        model.attributes.forEach(attribute => {
            const index = parseInt(attribute.propertyName);
            enumInfoFromDolphin[index] = attribute.value;
            enumInfoToDolphin.set(attribute.value, index);
        });
        this.enums.set(model.id, {fromDolphin: enumInfoFromDolphin, toDolphin: enumInfoToDolphin});
    }

    unregisterClass(model) {
        console.debug('ClassRepository.unregisterClass', model);
        this.classes.delete(model.id);
    }

    unregisterEnum(model) {
        console.debug('ClassRepository.unregisterEnum', model);
        this.enums.delete(model.id);
    }

    load(model) {
        console.debug('ClassRepository.load():', model);
        const classInfo = this.classes.get(model.presentationModelType);
        const bean = {};
        model.attributes
            .filter(attribute => attribute.tag === opendolphin.Tag.value())
            .forEach(attribute => {
                attribute.onValueChange(event => {
                    if (event.oldValue !== event.newValue) {
                        bean[attribute.propertyName] = classInfo[attribute.propertyName].fromDolphin(event.newValue);
                    }
                });
            });
        const observer = new ObjectObserver(bean);
        observer.open((added, removed, changed, getOldValueFn) => {
            Object.keys(added).forEach(property => {
                const attribute = model.findAttributeByPropertyName(property);
                if (exists(attribute)) {
                    const value = classInfo[property].toDolphin(added[property]);
                    attribute.setValue(value);
                }
            });
            Object.keys(removed).forEach(property => {
                const attribute = model.findAttributeByPropertyName(property);
                if (exists(attribute)) {
                    attribute.setValue(null);
                }
            });
            Object.keys(changed).forEach(property => {
                const attribute = model.findAttributeByPropertyName(property);
                if (exists(attribute)) {
                    const value = classInfo[property].toDolphin(changed[property]);
                    attribute.setValue(value);
                }
            });
        });
        this.beanFromDolphin.set(model.id, bean);
        this.beanToDolphin.set(bean, model.id);
        this.classInfos.set(model.id, classInfo);
        return bean;
    }

    unload(model) {
        console.debug('ClassRepository.unload():', model);
        const bean = this.beanFromDolphin.get(model.id);
        this.beanFromDolphin.delete(model.id);
        this.beanToDolphin.delete(bean);
        this.classInfos.delete(model.id);
        return bean;
    }

    modifyList(source, attribute, from, count, newElements) {
        const bean = this.beanFromDolphin.get(source);
        if (exists(bean)) {
            let list = bean[attribute];
            if (!exists(list)) {
                bean[attribute] = list = [];
            } else if (!Array.isArray(list)) {
                bean[attribute] = list = [list];
            }
            list.splice(from, count, newElements);
        }
    }

    addListEntry(model) {
        console.debug('ClassRepository.addListEntry', model);
        const source    = model.findAttributeByPropertyName('source').value;
        const attribute = model.findAttributeByPropertyName('attribute').value;
        const pos       = model.findAttributeByPropertyName('pos').value;
        const element   = model.findAttributeByPropertyName('element').value;
        const classInfo = this.classInfos.get(source);
        if (exists(classInfo)) {
            const entry = classInfo[attribute].fromDolphin(element);
            if (exists(entry)) {
                this.modifyList(source, attribute, pos, 0, entry);
            }
        }
    }

    delListEntry(model) {
        console.debug('ClassRepository.delListEntry', model);
        const source    = model.findAttributeByPropertyName('source').value;
        const attribute = model.findAttributeByPropertyName('attribute').value;
        const from      = model.findAttributeByPropertyName('from').value;
        const to        = model.findAttributeByPropertyName('to').value;
        this.modifyList(source, attribute, from, to-from);

    }

    setListEntry(model) {
        console.debug('ClassRepository.setListEntry', model);
        const source    = model.findAttributeByPropertyName('source').value;
        const attribute = model.findAttributeByPropertyName('attribute').value;
        const pos       = model.findAttributeByPropertyName('pos').value;
        const element   = model.findAttributeByPropertyName('element').value;
        const classInfo = this.classInfos.get(source);
        if (exists(classInfo)) {
            const entry = classInfo[attribute].fromDolphin(element);
            if (exists(entry)) {
                this.modifyList(source, attribute, pos, 1, entry);
            }
        }
    }
}
