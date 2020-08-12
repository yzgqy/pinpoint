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

package com.navercorp.pinpoint.collector.handler.thrift;

import com.navercorp.pinpoint.collector.handler.RequestResponseHandler;
import com.navercorp.pinpoint.collector.handler.SimpleHandler;
import com.navercorp.pinpoint.collector.mapper.thrift.ThriftBoMapper;
import com.navercorp.pinpoint.collector.service.AgentInfoService;
import com.navercorp.pinpoint.common.server.bo.AgentInfoBo;
import com.navercorp.pinpoint.io.request.ServerRequest;
import com.navercorp.pinpoint.io.request.ServerResponse;
import com.navercorp.pinpoint.thrift.dto.TAgentInfo;
import com.navercorp.pinpoint.thrift.dto.TResult;
import org.apache.thrift.TBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author emeroad
 * @author koo.taejin
 */
@Service
public class ThriftAgentInfoHandler implements SimpleHandler, RequestResponseHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private AgentInfoService agentInfoService;

    @Autowired
    @Qualifier("agentInfoBoMapper")
    private ThriftBoMapper<AgentInfoBo, TAgentInfo> agentInfoBoMapper;

    @Override
    public void handleSimple(ServerRequest serverRequest) {
        final Object data = serverRequest.getData();
        if (data instanceof TBase<?, ?>) {
            handleRequest((TBase<?, ?>) data);
        } else {
            throw new UnsupportedOperationException("data is not support type : " + data);
        }
    }


    @Override
    public void handleRequest(ServerRequest serverRequest, ServerResponse serverResponse) {
        final Object data = serverRequest.getData();
        if (data instanceof TBase<?, ?>) {
            final TBase<?, ?> tBase = handleRequest((TBase<?, ?>) data);
            serverResponse.write(tBase);
        } else {
            logger.warn("invalid serverRequest:{}", serverRequest);
        }
    }

    private TBase<?, ?> handleRequest(TBase<?, ?> tbase) {
        if (!(tbase instanceof TAgentInfo)) {
            logger.warn("invalid tbase:{}", tbase);
            // it happens to return null  not only at this BO(Business Object) but also at other BOs.
            return null;
        }

        try {
            final TAgentInfo agentInfo = (TAgentInfo) tbase;
            logger.debug("Received AgentInfo={}", agentInfo);

            // agent info
            final AgentInfoBo agentInfoBo = this.agentInfoBoMapper.map(agentInfo);
            this.agentInfoService.insert(agentInfoBo);
            return new TResult(true);
        } catch (Exception e) {
            logger.warn("AgentInfo handle error. Caused:{}", e.getMessage(), e);
            final TResult result = new TResult(false);
            result.setMessage(e.getMessage());
            return result;
        }
    }
}