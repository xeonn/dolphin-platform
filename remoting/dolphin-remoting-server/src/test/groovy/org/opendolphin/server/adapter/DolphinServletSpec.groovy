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
package org.opendolphin.server.adapter

import org.opendolphin.core.comm.Codec
import org.opendolphin.core.server.DefaultServerDolphin
import org.opendolphin.core.server.ServerConnector
import org.opendolphin.core.server.ServerDolphin
import org.opendolphin.core.server.ServerModelStore
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import java.util.logging.Level

class DolphinServletSpec extends Specification {

    private Codec codec = Mock()
    private ServerModelStore serverModelStore = Mock()
    private HttpServletRequest request = Mock()
    private HttpServletResponse response = Mock()
    private HttpSession session = Mock(HttpSession)

    def setup() {
        BufferedReader reader = new BufferedReader(new StringReader(' '))
        Writer writer = new PrintWriter(new StringWriter())

        codec.encode(_) >> ''
        codec.decode(_) >> []

        request.getSession() >> { session }
        request.getReader() >> reader

        response.getWriter() >> writer
    }

    void "calling doPost in new session must reach registration of custom actions - no logging"() {
        given:
        DolphinServlet.log.level = Level.OFF // for full branch coverage
        def servlet = mockServlet(null)

        when:
        servlet.doPost(request, response)

        then:
        servlet.registerReached
    }

    void "calling doPost in new session must reach registration of custom actions - all logging for branch coverage"() {
        given:
        DolphinServlet.log.level = Level.ALL // for full branch coverage
        def servlet = mockServlet(null)

        when:
        servlet.doPost(request, response)

        then:
        servlet.registerReached
    }

    void "calling doPost in existing session must not reach registration of custom actions"() {
        given:
        DolphinServlet.log.level = Level.ALL // for full branch coverage
        def connector = new ServerConnector(serverModelStore: serverModelStore, codec: codec)
        def servlet = mockServlet(new DefaultServerDolphin(serverModelStore, connector))

        when:
        servlet.doPost(request, response)

        then:
        !servlet.registerReached
    }

    TestDolphinServlet mockServlet(DefaultServerDolphin serverDolphin) {
        def servlet = new TestDolphinServlet()
        session.getAttribute(_) >> serverDolphin
        return servlet
    }

    class TestDolphinServlet extends DolphinServlet {
        boolean registerReached

        @Override
        protected Codec createCodec() {
            DolphinServletSpec.this.codec
        }

        @Override
        protected ServerModelStore createServerModelStore() {
            DolphinServletSpec.this.serverModelStore
        }

        @Override
        protected void registerApplicationActions(ServerDolphin serverDolphin) {
            registerReached = true
        }
    }
}