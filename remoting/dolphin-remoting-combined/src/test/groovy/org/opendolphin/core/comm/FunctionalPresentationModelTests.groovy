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
import core.client.comm.InMemoryClientConnector
import org.opendolphin.core.PresentationModel
import org.opendolphin.core.client.comm.SynchronousInMemoryClientConnector
import org.opendolphin.LogConfig
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.client.comm.*
import org.opendolphin.core.server.*
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry
import org.opendolphin.core.server.comm.NamedCommandHandler

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.logging.Level
/**
 * Showcase for how to test an application without the GUI by
 * issuing the respective commands and model changes against the
 * ClientModelStore
 */

class FunctionalPresentationModelTests extends GroovyTestCase {

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

    void testQualifiersInClientPMs() {
        PresentationModel modelA = clientDolphin.presentationModel("1", new ClientAttribute("a", 0, "QUAL"))
        PresentationModel modelB = clientDolphin.presentationModel("2", new ClientAttribute("b", 0, "QUAL"))

        modelA.getAt("a").setValue(1)

        assert modelB.getAt("b").getValue() == 1
        context.assertionsDone() // make sure the assertions are really executed
    }

    void testPerformanceWithStandardCommandBatcher() {
        doTestPerformance()
    }

    void testPerformanceWithBlindCommandBatcher() {
        def batcher = new BlindCommandBatcher(mergeValueChanges:true, deferMillis: 100)
        def connector = new InMemoryClientConnector(context.clientDolphin, serverDolphin.serverConnector, batcher)
        connector.uiThreadHandler = new RunLaterUiThreadHandler()
        context.clientDolphin.clientConnector = connector
        doTestPerformance()
    }

    void testPerformanceWithSynchronousConnector() {
        def connector = new SynchronousInMemoryClientConnector(context.clientDolphin, serverDolphin.serverConnector)
        connector.uiThreadHandler = { fail "should not reach here! " } as UiThreadHandler
        context.clientDolphin.clientConnector = connector
        doTestPerformance()
    }

    void doTestPerformance() {
        long id = 0
        registerAction serverDolphin, "performance", { cmd, response ->
            100.times { attr ->
                serverDolphin.presentationModelCommand(response, "id_${id++}".toString(), null, new DTO(new Slot("attr_$attr", attr)))
            }
        }
        def start = System.nanoTime()
        100.times { soOften ->
            clientDolphin.send "performance", { List<ClientPresentationModel> pms ->
                assert pms.size() == 100
                pms.each { pm -> clientDolphin.delete(pm) }
            }
        }
        clientDolphin.send "performance", { List<ClientPresentationModel> pms ->
            assert pms.size() == 100
            println ((System.nanoTime() - start).intdiv(1_000_000))
            context.assertionsDone() // make sure the assertions are really executed
        }
    }

    void testCreationRoundtripDefaultBehavior() {
        registerAction serverDolphin, "create", { cmd, response ->
            serverDolphin.presentationModelCommand(response, "id".toString(), null, new DTO(new Slot("attr", 'attr')))
        }
        registerAction serverDolphin, "checkNotificationReached", { cmd, response ->
            assert 1 == serverDolphin.listPresentationModels().size()
            assert serverDolphin.getAt("id")
        }

        clientDolphin.send "create", { List<ClientPresentationModel> pms ->
            assert pms.size() == 1
            assert 'attr' == pms.first().getAt("attr").value
            clientDolphin.send "checkNotificationReached", { List<ClientPresentationModel> xxx ->
                context.assertionsDone() // make sure the assertions are really executed
            }
        }
    }

    void testCreationRoundtripForTags() {
        registerAction serverDolphin, "create", { cmd, response ->
            def NO_TYPE = null
            def NO_QUALIFIER = null
            serverDolphin.presentationModelCommand(response, "id".toString(), NO_TYPE, new DTO(new Slot("attr", true, NO_QUALIFIER)))
        }
        registerAction serverDolphin, "checkTagIsKnownOnServerSide", { cmd, response ->
        }

        clientDolphin.send "create", { List<ClientPresentationModel> pms ->
            clientDolphin.send "checkTagIsKnownOnServerSide", { List<ClientPresentationModel> xxx ->
                context.assertionsDone()
            }
        }
    }

    void testFetchingAnInitialListOfData() {
        registerAction serverDolphin, "fetchData", { cmd, response ->
            ('a'..'z').each {
                DTO dto = new DTO(new Slot('char',it))
                // sending CreatePresentationModelCommand _without_ adding the pm to the server model store
                serverDolphin.presentationModelCommand(response, it, null, dto)
            }
        }
        clientDolphin.send "fetchData", { List<ClientPresentationModel> pms ->
            assert pms.size() == 26
            assert pms.collect { it.id }.sort(false) == pms.collect { it.id }   // pmIds from a single action should come in sequence
            assert 'a' == context.clientDolphin.findPresentationModelById('a').getAt("char").value
            assert 'z' == context.clientDolphin.findPresentationModelById('z').getAt("char").value
            context.assertionsDone() // make sure the assertions are really executed
        }
    }

    void registerAction(ServerDolphin serverDolphin, String name, Closure handler) {
        serverDolphin.register(new DolphinServerAction() {

            @Override
            void registerIn(ActionRegistry registry) {
                registry.register(name, handler);
            }
        });
    }

    void registerAction(ServerDolphin serverDolphin, String name, NamedCommandHandler handler) {
        serverDolphin.register(new DolphinServerAction() {

            @Override
            void registerIn(ActionRegistry registry) {
                registry.register(name, handler);
            }
        });
    }

    void testLoginUseCase() {
        registerAction serverDolphin, "loginCmd", { cmd, response ->
            def user = context.serverDolphin.findPresentationModelById('user')
            if (user.getAt("name").value == 'Dierk' && user.getAt("password").value == 'Koenig') {
                DefaultServerDolphin.changeValueCommand(response, user.getAt("loggedIn"), 'true')
            }
        }
        def user = clientDolphin.presentationModel 'user', name: null, password: null, loggedIn: null
        clientDolphin.send "loginCmd", {
            assert !user.getAt("loggedIn").value
        }
        user.getAt("name").value = "Dierk"
        user.getAt("password").value = "Koenig"

        clientDolphin.send "loginCmd", {
            assert user.getAt("loggedIn").value
            context.assertionsDone()
        }
    }

    void testAsynchronousExceptionOnTheServer() {
        LogConfig.logCommunication()
        def count = 0
        clientDolphin.clientConnector.onException = { count++ }

        registerAction serverDolphin, "someCmd", { cmd, response ->
            throw new RuntimeException("EXPECTED: some arbitrary exception on the server")
        }

        clientDolphin.send "someCmd", {
            fail "the onFinished handler will not be reached in this case"
        }
        clientDolphin.sync {
            assert count == 1
        }

        // provoke a second exception
        clientDolphin.send "someCmd", {
            fail "the onFinished handler will not be reached either"
        }
        clientDolphin.sync {
            assert count == 2
        }
        clientDolphin.sync {
            context.assertionsDone()
        }
    }

    void testAsynchronousExceptionInOnFinishedHandler() {

        clientDolphin.clientConnector.uiThreadHandler = { it() } as UiThreadHandler // not "run later" we need it immediately here
        clientDolphin.clientConnector.onException = { context.assertionsDone() }

        registerAction serverDolphin, "someCmd", { cmd, response ->
            // nothing to do
        }
        clientDolphin.send "someCmd", {
            throw new RuntimeException("EXPECTED: some arbitrary exception in the onFinished handler")
        }
    }

    void testUnregisteredCommandWithLog() {
        serverDolphin.serverConnector.setLogLevel(Level.ALL);
        clientDolphin.send "no-such-action-registered", {
            // unknown actions are silently ignored and logged as warnings on the server side.
        }
        context.assertionsDone()
    }
    void testUnregisteredCommandWithoutLog() {
        serverDolphin.serverConnector.setLogLevel(Level.OFF);
        clientDolphin.send "no-such-action-registered"
        context.assertionsDone()
    }

    // silly and only for the coverage, we test behavior when id is wrong ...
    void testIdNotFoundInVariousCommands() {
        clientDolphin.clientConnector.send new ValueChangedCommand(attributeId: 0)
        DefaultServerDolphin.changeValueCommand(null, null, null)
        DefaultServerDolphin.changeValueCommand(null, new ServerAttribute('a',42), 42)
        context.assertionsDone()
    }

    void testDataRequest() {
        registerAction serverDolphin,"myData", { cmd, resp ->
            resp << new DataCommand([a:1, b:2])
        }
        clientDolphin.send "myData", OnFinishedDataAdapter.withAction({ data ->
            assert data.size() == 1
            assert data[0].a == 1
            assert data[0].b == 2
            context.assertionsDone()
        });
    }

    void testActionAndSendJavaLike() {
        boolean reached = false
        registerAction(serverDolphin, "java", new NamedCommandHandler() {
            @Override
            void handleCommand(NamedCommand command, List<Command> response) {
                reached = true
            }
        });
        clientDolphin.send("java", new OnFinishedHandlerAdapter() {
            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                assert reached
                context.assertionsDone()
            }
        })
    }

    void testRemovePresentationModel() {
        clientDolphin.presentationModel('pm', attr: 1)

        registerAction serverDolphin,'delete', { cmd, response ->
//            serverDolphin.delete(response, serverDolphin['pm']) // deprecated
            serverDolphin.remove(serverDolphin['pm'])
            assert serverDolphin['pm'] == null
        }
        assert clientDolphin['pm']

        clientDolphin.send 'delete', {
            assert clientDolphin['pm'] == null
            context.assertionsDone()
        }
    }

    void testDeleteAllPresentationModelsOfTypeFromClient() {

        registerAction serverDolphin,'assert1', { cmd, response ->
            assert 1 == serverDolphin.findAllPresentationModelsByType('PmType').size()
        }
        registerAction serverDolphin,'assert0', { cmd, response ->
            assert 0 == serverDolphin.findAllPresentationModelsByType('PmType').size()
        }

        clientDolphin.send 'assert0'

        clientDolphin.presentationModel('pm', 'PmType', attr: 1)
        assert clientDolphin['pm']

        clientDolphin.send 'assert1'

        clientDolphin.deleteAllPresentationModelsOfType('PmType')
        assert new DeletedAllPresentationModelsOfTypeNotification('PmType') =~ /pmType PmType/


        clientDolphin.send 'assert0', {
            assert clientDolphin['pm'] == null
            context.assertionsDone()
        }

    }

    void testWithNullResponses() {
        clientDolphin.presentationModel('pm', attr: 1)

        registerAction serverDolphin,'arbitrary', { cmd, response ->
            serverDolphin.deleteCommand([], (ServerPresentationModel) null)
            serverDolphin.deleteCommand([], '')
            serverDolphin.deleteCommand(null, '')
            serverDolphin.presentationModelCommand(null, null,null,null)
            serverDolphin.changeValueCommand([], null, null)
        }
        clientDolphin.send('arbitrary'){
            context.assertionsDone()
        }
    }

    void testStateConflictBetweenClientAndServer() {
        LogConfig.logCommunication()
        def latch = new CountDownLatch(1)
        def pm = clientDolphin.presentationModel('pm', attr: 1)
        def attr = pm.getAt('attr')

        registerAction serverDolphin,'set2', { cmd, response ->
            latch.await() // mimic a server delay such that the client has enough time to change the value concurrently
            serverDolphin.getAt('pm').getAt('attr').value == 1
            serverDolphin.getAt('pm').getAt('attr').value  = 2
            serverDolphin.getAt('pm').getAt('attr').value == 2 // immediate change of server state
        }
        registerAction serverDolphin,'assert3', { cmd, response ->
            assert serverDolphin.getAt('pm').getAt('attr').value == 3
        }

        clientDolphin.send('set2') // a conflict could arise when the server value is changed ...
        attr.value = 3            // ... while the client value is changed concurrently
        latch.countDown()
        clientDolphin.send('assert3') // since from the client perspective, the last change was to 3, server and client should both see the 3

        // in between these calls a conflicting value change could be transferred, setting both value to 2

        clientDolphin.send('assert3'){
            assert attr.value == 3
            context.assertionsDone()
        }


    }

}