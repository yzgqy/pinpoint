/*
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.flink.receiver;

import com.navercorp.pinpoint.collector.handler.SimpleHandler;
import com.navercorp.pinpoint.collector.receiver.thrift.DispatchHandler;
import com.navercorp.pinpoint.io.header.Header;
import com.navercorp.pinpoint.io.request.ServerRequest;
import com.navercorp.pinpoint.io.request.ServerResponse;
import com.navercorp.pinpoint.thrift.io.FlinkTBaseLocator;

import java.util.Objects;

/**
 * @author minwoo.jung
 */
public class TcpDispatchHandler implements DispatchHandler {

    private AgentStatHandler agentStatHandler;

    private SimpleHandler getSimpleHandler(Header header) {
        if (header.getType() == FlinkTBaseLocator.AGENT_STAT_BATCH) {
            return agentStatHandler;
        }
        throw new UnsupportedOperationException("unsupported header:" + header);
    }

    private SimpleHandler getSimpleHandler(ServerRequest serverRequest) {
        final Header header = serverRequest.getHeader();
        return getSimpleHandler(header);
    }

    public void setAgentStatHandler(AgentStatHandler agentStatHandler) {
        this.agentStatHandler = Objects.requireNonNull(agentStatHandler, "agentStatHandler must not be null");
    }

    @Override
    public void dispatchSendMessage(ServerRequest serverRequest) {
        SimpleHandler simpleHandler = getSimpleHandler(serverRequest);
        simpleHandler.handleSimple(serverRequest);
    }

    @Override
    public void dispatchRequestMessage(ServerRequest serverRequest, ServerResponse serverResponse) {
    }
}
