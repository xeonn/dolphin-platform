/*
 * Copyright 2015-2016 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opendolphin.core.comm

import org.opendolphin.LogConfig
import org.opendolphin.core.ModelStoreConfig
import org.opendolphin.core.ModelStoreEvent
import org.opendolphin.core.ModelStoreListener
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.client.comm.OnFinishedHandler
import org.opendolphin.core.server.*
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry
import org.opendolphin.core.server.comm.CommandHandler
import org.opendolphin.core.server.comm.NamedCommandHandler

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Functional tests for the server-side state changes.
 */

class ServerPresentationModelTests extends GroovyTestCase {

    volatile TestInMemoryConfig context
    DefaultServerDolphin serverDolphin
    ClientDolphin clientDolphin

    @Override
    protected void setUp() {
        context = new TestInMemoryConfig()
        serverDolphin = context.serverDolphin
        clientDolphin = context.clientDolphin
        LogConfig.noLogs()
    }

    @Override
    protected void tearDown() {
        assert context.done.await(10, TimeUnit.SECONDS)
    }

    void testServerModelStoreAcceptsConfig() {
        new ServerModelStore(new ModelStoreConfig())
        context.assertionsDone()
    }

    void registerAction(ServerDolphin serverDolphin, String name, NamedCommandHandler handler) {
        serverDolphin.register(new DolphinServerAction() {

            @Override
            void registerIn(ActionRegistry registry) {
                registry.register(name, handler);
            }
        });
    }


    void testServerPresentationModelDoesNotRejectAutoId() {
        // re-enable the shouldFail once we have proper Separation of commands and notifications
//        shouldFail IllegalArgumentException, {
        assert new ServerPresentationModel("1${ServerPresentationModel.AUTO_ID_SUFFIX}", [], new ServerModelStore())
//        }
        context.assertionsDone()
    }

    void testSecondServerActionCanRelyOnAttributeValueChange() {
        def model = clientDolphin.presentationModel("PM1", ["att1"] )

        registerAction serverDolphin, "setValue", { cmd, response ->
            serverDolphin.getPresentationModel("PM1").getAttribute("att1").value = 1
        }

        registerAction serverDolphin, "assertValue", { cmd, response ->
            assert 1 == serverDolphin.getPresentationModel("PM1").getAttribute("att1").value
        }

        clientDolphin.send "setValue"
        clientDolphin.send "assertValue", new OnFinishedHandler() {

            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                assert 1 == model.getAttribute("att1").value
                context.assertionsDone()
            }
        }
    }

    void testServerSideValueChangesUseQualifiers() {
        def model = clientDolphin.presentationModel("PM1", att1:'base', att2:'base')
        model.getAttribute("att1").qualifier = 'qualifier'
        model.getAttribute("att2").qualifier = 'qualifier'

        registerAction serverDolphin, "changeValue", { cmd, response ->
            def at1 = serverDolphin.getPresentationModel("PM1").getAttribute("att1")
            assert at1.value == 'base'
            at1.value = 'changed'
            assert serverDolphin.getPresentationModel("PM1").getAttribute("att2").value == 'changed'
        }

        clientDolphin.send "changeValue"

        clientDolphin.sync {
            context.assertionsDone()
        }
    }

    void testServerSideEventListenerCanChangeSelfValue() {
        def model = clientDolphin.presentationModel("PM1", att1:'base')

        registerAction serverDolphin, "attachListener", { cmd, response ->
            ServerAttribute at1 = serverDolphin.getPresentationModel("PM1").getAttribute("att1")
            at1.addPropertyChangeListener("value") { event ->
                at1.setValue("changed from PCL")
            }
        }

        registerAction serverDolphin, "changeBaseValue", { cmd, response ->
            def at1 = serverDolphin.getPresentationModel("PM1").getAttribute("att1")
            assert at1.baseValue == 'base'
            at1.baseValue = 'changedBase'
            assert serverDolphin.getPresentationModel("PM1").getAttribute("att2").baseValue == 'changedBase'
        }

        clientDolphin.send "attachListener"

        clientDolphin.sync {
            model.getAttribute("att1").setValue("changed")
            clientDolphin.sync {
                assert model.getAttribute("att1").getValue() == "changed from PCL"
                context.assertionsDone()
            }
        }
    }


    void testSecondServerActionCanRelyOnPmCreate() {

        def pmWithNullId

        registerAction serverDolphin, "create", { cmd, response ->
            def dto = new DTO(new Slot("att1", 1))
            def pm = serverDolphin.presentationModel("PM1", null, dto)
            pmWithNullId = serverDolphin.presentationModel(null, "pmType", dto)
            assert pm
            assert pmWithNullId
            assert serverDolphin.getPresentationModel("PM1")
            assert serverDolphin.findAllPresentationModelsByType("pmType").first() == pmWithNullId
        }

        registerAction serverDolphin, "assertVisible", { cmd, response ->
            assert serverDolphin.getPresentationModel("PM1")
            assert serverDolphin.findAllPresentationModelsByType("pmType").first() == pmWithNullId
        }

        clientDolphin.send "create"
        clientDolphin.send "assertVisible"

        clientDolphin.sync {
            assert clientDolphin.getPresentationModel("PM1")
            assert clientDolphin.findAllPresentationModelsByType("pmType").size() == 1
            println clientDolphin.findAllPresentationModelsByType("pmType").first().id
            context.assertionsDone()
        }
    }

    void testServerCreatedAttributeChangesValueOnTheClientSide() {

        AtomicBoolean pclReached = new AtomicBoolean(false)

        registerAction serverDolphin, "create", { cmd, response ->
            def dto = new DTO(new Slot("att1", 1))
            serverDolphin.presentationModel("PM1", null, dto)
            serverDolphin.getPresentationModel("PM1").getAttribute("att1").addPropertyChangeListener("value",{ pclReached.set(true) })
        }

        registerAction serverDolphin, "assertValueChange", { cmd, response ->
            assert pclReached.get()
            assert serverDolphin.getPresentationModel("PM1").getAttribute("att1").value == 2
            context.assertionsDone()
        }

        clientDolphin.send "create", new OnFinishedHandler() {

            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                assert clientDolphin.getPresentationModel("PM1").getAttribute("att1").value == 1
                clientDolphin.getPresentationModel("PM1").getAttribute("att1").value = 2

                clientDolphin.send "assertValueChange"
            }
        }

    }

    void testServerSideModelStoreListener() {

        List<CreatePresentationModelCommand> receivedCommands = new ArrayList<>();
        List<ModelStoreEvent> receivedEvents = new ArrayList<>();

        registerAction serverDolphin, "registerMSL", { cmd, response ->
            serverDolphin.addModelStoreListener(new ModelStoreListener() {
                @Override void modelStoreChanged(ModelStoreEvent event) {
                    receivedEvents << event
                }
            })
        }

        serverDolphin.register(new DolphinServerAction() {
            @Override
            void registerIn(ActionRegistry registry) {
                registry.register(CreatePresentationModelCommand.class, new CommandHandler<CreatePresentationModelCommand>() {

                    @Override
                    void handleCommand(CreatePresentationModelCommand command, List<Command> response) {
                        receivedCommands << command
                    }
                });
            }
        })

        registerAction serverDolphin, "create", { cmd, response ->
            def dto = new DTO(new Slot("att1", 1))
            serverDolphin.presentationModel("server-side-with-id", null, dto)
            // will lead to log: [INFO] There already is a PM with id server-side-with-id. Create PM ignored.
            serverDolphin.presentationModel(null, "server-side-without-id", dto)
            // will lead to log: [INFO] Cannot create PM '0-AUTO-SRV' with forbidden suffix. Create PM ignored.
            // at this point, the MSL has been triggered
        }

        clientDolphin.send "registerMSL"

        clientDolphin.presentationModel("client-side-with-id", null, attr1:1)

        clientDolphin.send "create", new OnFinishedHandler() {

            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                assert receivedCommands.get(0).pmId == "client-side-with-id"
                assert receivedCommands.get(1).pmId == "server-side-with-id"
                assert receivedCommands.get(2).pmId == "0-AUTO-SRV"
                assert receivedEvents.get(0).presentationModel.id == "client-side-with-id"
                assert receivedEvents.get(1).presentationModel.id == "server-side-with-id"
                assert receivedEvents.get(2).presentationModel.id == "0-AUTO-SRV"
                context.assertionsDone()
            }
        }
    }

    void testServerSidePmRemoval() {

        clientDolphin.presentationModel("client-side-with-id", null, attr1:1)

        registerAction serverDolphin, "remove", { cmd, response ->
            def pm = serverDolphin.getPresentationModel("client-side-with-id")
            assert pm
            serverDolphin.remove(pm)
            assert null == serverDolphin.getPresentationModel("client-side-with-id") // immediately removed on server
        }

        assert clientDolphin.getPresentationModel("client-side-with-id")

        clientDolphin.send "remove", new OnFinishedHandler() {

            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                assert null == clientDolphin.getPresentationModel("client-side-with-id") // removed from client before callback
                context.assertionsDone()
            }
        }
    }

    void testServerSideBaseValueChange() {
        def source = clientDolphin.presentationModel("source", null, attr1:"sourceValue")

        registerAction serverDolphin, "changeBaseValue", { cmd, response ->
            def attribute = serverDolphin.getPresentationModel("source").getAttribute("attr1")
        }

        clientDolphin.send "changeBaseValue", new OnFinishedHandler() {

            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                assert source.getAttribute("attr1").value     == "sourceValue"
                context.assertionsDone()
            }
        }
    }

    void testServerSideQualifierChange() {
        def source = clientDolphin.presentationModel("source", null, attr1:"sourceValue")

        source.getAttribute("attr1").qualifier = "qualifier"

        registerAction serverDolphin, "changeBaseValue", { cmd, response ->
            def attribute = serverDolphin.getPresentationModel("source").getAttribute("attr1")
            attribute.qualifier = "changed"
            // immediately applied on server
            assert attribute.qualifier == "changed"
        }

        clientDolphin.send "changeBaseValue", new OnFinishedHandler() {

            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                assert source.getAttribute("attr1").value     == "sourceValue"
                assert source.getAttribute("attr1").qualifier == "changed"
                context.assertionsDone()
            }
        }
    }

    void testServerSideAddingOfAttributesToAnExistingModel() {
        def source = clientDolphin.presentationModel("source", null, attr1:"sourceValue")

        registerAction serverDolphin, "addAttribute", { cmd, response ->
            def pm = serverDolphin.getPresentationModel("source")
            def attr2 = new ServerAttribute("attr2","initial")
            pm.addAttribute(attr2)
            // immediately applied on server
            assert pm.getAttribute("attr2").value == "initial"
        }

        clientDolphin.send "addAttribute", new OnFinishedHandler() {

            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                assert source.getAttribute("attr2").value == "initial"
                context.assertionsDone()
            }
        }
    }

    // todo dk: think about these use cases:
    // dolphin.copy(pm) on client and server (done) - todo: make js version use the same approach
    // server-side tagging

}