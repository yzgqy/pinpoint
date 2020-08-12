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
import com.navercorp.pinpoint.collector.service.StringMetaDataService;
import com.navercorp.pinpoint.common.server.bo.StringMetaDataBo;
import com.navercorp.pinpoint.io.request.ServerRequest;
import com.navercorp.pinpoint.io.request.ServerResponse;
import com.navercorp.pinpoint.thrift.dto.TResult;
import com.navercorp.pinpoint.thrift.dto.TStringMetaData;
import org.apache.thrift.TBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author emeroad
 */
@Service
public class ThriftStringMetaDataHandler implements RequestResponseHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StringMetaDataService stringMetaDataService;

    @Override
    public void handleRequest(ServerRequest serverRequest, ServerResponse serverResponse) {
        final Object data = serverRequest.getData();
        if (data instanceof TBase<?, ?>) {
            TBase<?, ?> tBase = handleRequest((TBase<?, ?>) data);
            serverResponse.write(tBase);
        } else {
            logger.warn("invalid serverRequest:{}", serverRequest);
            return;
        }
    }

    private TBase<?, ?> handleRequest(TBase<?, ?> tbase) {
        if (!(tbase instanceof TStringMetaData)) {
            logger.error("invalid tbase:{}", tbase);
            return null;
        }

        TStringMetaData stringMetaData = (TStringMetaData) tbase;
        // because api data is important, logging it at info level
        if (logger.isInfoEnabled()) {
            logger.info("Received StringMetaData={}", stringMetaData);
        }

        try {
            final StringMetaDataBo stringMetaDataBo = new StringMetaDataBo(stringMetaData.getAgentId(), stringMetaData.getAgentStartTime(), stringMetaData.getStringId());
            stringMetaDataBo.setStringValue(stringMetaData.getStringValue());
            stringMetaDataService.insert(stringMetaDataBo);
        } catch (Exception e) {
            logger.warn("{} handler error. Caused:{}", this.getClass(), e.getMessage(), e);
            final TResult result = new TResult(false);
            result.setMessage(e.getMessage());
            return result;
        }
        return new TResult(true);
    }
}