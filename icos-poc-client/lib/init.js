var SERVER_URL      = "http://localhost:8080/dolphin";
var dolphin         = opendolphin.dolphin(SERVER_URL, true);

var questionnaire = {
    sections: []
};
var classes = {};
var dolphinBeans = {};
//var questions = [];
var FIELD_TYPE_UNKNOWN = 0,
    FIELD_TYPE_BASIC_TYPE = 1,
    FIELD_TYPE_ENUM = 2,
    FIELD_TYPE_DOLPHIN_BEAN = 3;

dolphin.getClientModelStore().onModelStoreChange(function (event) {
    if (event.eventType == opendolphin.Type.ADDED) {
        if (event.clientPresentationModel.presentationModelType === '@@@ DOLPHIN_CLASS_TYPE_ID @@@') {
            registerClass(event.clientPresentationModel);
        } else {
            addObject(event.clientPresentationModel);
        }
    //    switch (event.clientPresentationModel.presentationModelType) {
    //        case '@@@ DOLPHIN_CLASS_TYPE_ID @@@':
    //            registerClass(event.clientPresentationModel);
    //            break;
    //        case 'com.canoo.dolphin.icos.model.Questionnaire':
    //            addQuestionnaire(event.clientPresentationModel);
    //            break;
    //        case 'com.canoo.dolphin.icos.model.Section':
    //            addSection(event.clientPresentationModel);
    //            break;
    //        case 'com.canoo.dolphin.icos.model.Question':
    //            addQuestion(event.clientPresentationModel);
    //            break;
    //        case 'com.canoo.dolphin.icos.model.Option':
    //            addOption(event.clientPresentationModel);
    //            break;
    //    }
    //} else if (event.eventType == opendolphin.Type.REMOVED) {
    //    switch (event.clientPresentationModel.presentationModelType) {
    //        case 'com.canoo.dolphin.icos.model.Questionnaire':
    //            removeQuestionnaire(event.clientPresentationModel);
    //            break;
    //        case 'com.canoo.dolphin.icos.model.Section':
    //            removeSection(event.clientPresentationModel);
    //            break;
    //        case 'com.canoo.dolphin.icos.model.Question':
    //            removeQuestion(event.clientPresentationModel);
    //            break;
    //        case 'com.canoo.dolphin.icos.model.Option':
    //            removeOption(event.clientPresentationModel);
    //            break;
    //    }
    }
});

dolphin.send('COMMAND_INIT', {
    onFinished: function () {
        //dolphin.startPushListening("push_command","release");
    }
});

function registerClass(presentationModel) {
    var classData = {
    };
    _.each(presentationModel.attributes, function(attribute) {
        classData[attribute.propertyName] = attribute.value;
        dolphin.getAt(presentationModel.id).getAt(attribute.propertyName).onValueChange(function (event) {
            classData[attribute.propertyName] = event.newValue;
        });
    });
    classes[classData.name] = classData;
}

function getFieldType(className, propertyName) {
    return classes[className] && classes[className][propertyName]? classes[className][propertyName] : FIELD_TYPE_UNKNOWN;
}

function addObject(presentationModel) {
    var object = {
        dolphinId: presentationModel.id
    };
    var className = presentationModel.presentationModelType;

    _.each(presentationModel.attributes, function(attribute) {
        var fieldType = getFieldType(className, attribute.propertyName);
        switch (fieldType) {
            case FIELD_TYPE_UNKNOWN:
            case FIELD_TYPE_BASIC_TYPE:
            // TODO Implement enums
            case FIELD_TYPE_ENUM:
                object[attribute.propertyName] = attribute.value;
                dolphin.getAt(presentationModel.id).getAt(attribute.propertyName).onValueChange(function (event) {
                    object[attribute.propertyName] = event.newValue;
                });
                break;
            case FIELD_TYPE_DOLPHIN_BEAN:
                object[attribute.propertyName] = attribute.value == null ? null : dolphinBeans[attribute.value];
                dolphin.getAt(presentationModel.id).getAt(attribute.propertyName).onValueChange(function (event) {
                    object[attribute.propertyName] = event.newValue == null ? null : dolphinBeans[event.newValue];
                });
                break;
        }
    });

    dolphinBeans[object.dolphinId] = object;

    hackMissing1ToNRelations(presentationModel, object);
}

function hackMissing1ToNRelations(presenationModel, object) {
    switch (presenationModel.presentationModelType) {
        case 'com.canoo.dolphin.icos.model.Questionnaire':
            questionnaire = object;
            var element = document.querySelector('icos-questionnaire');
            element.data = questionnaire;
            break;
        case 'com.canoo.dolphin.icos.model.Section':
            if (object.questionnaire) {
                object.questionnaire.sections || (object.questionnaire.sections = []);
                object.questionnaire.sections.push(object);
            }
            break;
        case 'com.canoo.dolphin.icos.model.Question':
            if (object.section) {
                object.section.questions || (object.section.questions = []);
                object.section.questions.push(object);
            }
            break;
        case 'com.canoo.dolphin.icos.model.Option':
            if (object.question) {
                object.question.options || (object.question.options = []);
                object.question.options.push(object);
            }
            break;

    }
}

//function addQuestionnaire(presentationModel) {
//    questionnaire.dolphinId = presentationModel.id;
//    _.each(presentationModel.attributes, function(attribute) {
//        if (attribute.tag === 'VALUE') {
//            questionnaire[attribute.propertyName] = attribute.value;
//        }
//    });
//}
//
//function removeQuestionnaire(presentationModel) {
//    console.log("removeQuestionnaire not implemented yet");
//}
//
//function addSection(presentationModel) {
//    var section = {
//        dolphinId: presentationModel.id
//    };
//
//    _.each(presentationModel.attributes, function(attribute) {
//        if (attribute.tag === 'VALUE' && attribute.propertyName !== 'questionnaire') {
//            section[attribute.propertyName] = attribute.value;
//        }
//    });
//    questionnaire.sections.push(section);
//}
//
//function removeSection(presentationModel) {
//    console.log("removeSection not implemented yet");
//}
//
//function addQuestion(presentationModel) {
//    var question = {
//        dolphinId: presentationModel.id
//    };
//    questions.push(question);
//
//    _.each(presentationModel.attributes, function(attribute) {
//        if (attribute.tag === 'VALUE') {
//            if (attribute.propertyName === 'section') {
//                var section = _.find(questionnaire.sections, function (elem) {
//                    return elem.dolphinId === attribute.value
//                });
//                if (section) {
//                    section.questions || (section.questions = []);
//                    section.questions.push(question);
//                }
//            } else {
//                question[attribute.propertyName] = attribute.value;
//                dolphin.getAt(presentationModel.id).getAt(attribute.propertyName).onValueChange(function (event) {
//                    question[attribute.propertyName] = event.newValue;
//                })
//            }
//        }
//    });
//}
//
//function removeQuestion(presentationModel) {
//    console.log("removeQuestion not implemented yet");
//}
//
//function addOption(presentationModel) {
//    var option = {
//        dolphinId: presentationModel.id
//    };
//
//    _.each(presentationModel.attributes, function(attribute) {
//        if (attribute.tag === 'VALUE') {
//            if (attribute.propertyName === 'question') {
//                var question = _.find(questions, function (elem) {
//                    return elem.dolphinId === attribute.value
//                });
//                if (question) {
//                    question.options || (question.options = []);
//                    question.options.push(option);
//                }
//            } else {
//                option[attribute.propertyName] = attribute.value;
//            }
//        }
//    });
//}
//
//function removeOption(presentationModel) {
//    console.log("removeOption not implemented yet");
//}


$.ready(new function() {
    var element = document.createElement('icos-questionnaire');
    element.data = questionnaire;
    document.querySelector('body').appendChild(element);
});
