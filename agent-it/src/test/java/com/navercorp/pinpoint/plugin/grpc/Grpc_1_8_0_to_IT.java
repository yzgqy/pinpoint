/*
 * Copyright 2018 NAVER Corp.
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

package com.navercorp.pinpoint.plugin.grpc;

import com.navercorp.pinpoint.bootstrap.plugin.test.ExpectedTrace;
import com.navercorp.pinpoint.bootstrap.plugin.test.ExpectedTraceField;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifier;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifierHolder;
import com.navercorp.pinpoint.plugin.AgentPath;
import com.navercorp.pinpoint.test.plugin.Dependency;
import com.navercorp.pinpoint.test.plugin.PinpointAgent;
import com.navercorp.pinpoint.test.plugin.PinpointConfig;
import com.navercorp.pinpoint.test.plugin.PinpointPluginTestSuite;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.navercorp.pinpoint.bootstrap.plugin.test.Expectations.annotation;
import static com.navercorp.pinpoint.bootstrap.plugin.test.Expectations.event;

/**
 * @author Taejin Koo
 */
@RunWith(PinpointPluginTestSuite.class)
@PinpointAgent(AgentPath.PATH)

@Dependency({"io.grpc:grpc-stub:[1.8.0,)", "io.grpc:grpc-netty:[1.8.0]", "io.grpc:grpc-protobuf:[1.8.0]"})
@PinpointConfig("pinpoint-grpc-plugin-test.config")
public class Grpc_1_8_0_to_IT {

    private final Logger logger = Logger.getLogger(Grpc_1_8_0_to_IT.class.getName());

    private final String REQUEST = "hello";

    @Test
    public void requestResponseTest() throws Exception {
        HelloWorldSimpleServer server = null;
        HelloWorldSimpleClient client = null;
        try {
            server = new HelloWorldSimpleServer();
            server.start();

            client = new HelloWorldSimpleClient("127.0.0.1", server.getBindPort());
            String response = client.greet(REQUEST);
            Assert.assertEquals(REQUEST.toUpperCase(), response);

            PluginTestVerifier verifier = getPluginTestVerifier();

            assertTrace(server, verifier);

            verifier.verifyTraceCount(8);
        } finally {
            clearResources(client, server);
        }
    }

    @Test
    public void streamingTest() throws Exception {
        HelloWorldStreamServer server = null;
        HelloWorldStreamClient client = null;

        Random random = new Random(System.currentTimeMillis());
        int requestCount = random.nextInt(5) + 1;

        try {
            server = new HelloWorldStreamServer();
            server.start();

            client = new HelloWorldStreamClient("127.0.0.1", server.getBindPort());
            client.greet(requestCount);
            Assert.assertEquals(requestCount, server.getRequestCount());

            PluginTestVerifier verifier = getPluginTestVerifier();

            assertTrace(server, verifier);
            verifier.verifyTraceCount(6 + (requestCount * 2));
        } finally {
            clearResources(client, server);
        }
    }

    private PluginTestVerifier getPluginTestVerifier() {
        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        if (logger.isLoggable(Level.FINE)) {
            verifier.printCache();
        }
        return verifier;
    }


    private void assertTrace(HelloWorldServer server, PluginTestVerifier verifier) {
        verifier.verifyTrace(clientCallStartEvent(server));
        verifier.verifyTrace(event("GRPC_INTERNAL", "Grpc client listener Result Invocation"));

        verifier.verifyTrace(createServerRootTrace(server));

        String streacmCreatedMethodDescritor = "io.grpc.internal.ServerImpl$ServerTransportListenerImpl.streamCreated(io.grpc.internal.ServerStream, java.lang.String, io.grpc.Metadata)";
        verifier.verifyTrace(event("GRPC_SERVER_INTERNAL", streacmCreatedMethodDescritor));
    }

    private ExpectedTrace clientCallStartEvent(HelloWorldServer server) {
        ExpectedTrace.Builder eventBuilder = ExpectedTrace.createEventBuilder("GRPC");
        eventBuilder.setMethodSignature("io.grpc.internal.ClientCallImpl.start(io.grpc.ClientCall$Listener, io.grpc.Metadata)");

        String remoteAddress = "127.0.0.1:" + server.getBindPort();
        eventBuilder.setEndPoint(remoteAddress);
        eventBuilder.setDestinationId(remoteAddress);
        eventBuilder.setAnnotations(annotation("http.url", "http://" + remoteAddress + "/" + server.getMethodName()));

        return eventBuilder.build();
    }

    private ExpectedTrace createServerRootTrace(HelloWorldServer server) {
        ExpectedTrace.Builder rootBuilder = ExpectedTrace.createRootBuilder("GRPC_SERVER");
        rootBuilder.setMethodSignature("Grpc HTTP Server");
        rootBuilder.setRpc("/" + server.getMethodName());
        rootBuilder.setRemoteAddr(ExpectedTraceField.createStartWith("127.0.0.1:"));
        return rootBuilder.build();
    }

    private void clearResources(HelloWorldClient client, HelloWorldServer server) {
        try {
            if (client != null) {
                client.shutdown();
            }
        } catch (Exception e) {
        }
        try {
            if (server != null) {
                server.stop();
            }
        } catch (Exception e) {
        }
    }

}
