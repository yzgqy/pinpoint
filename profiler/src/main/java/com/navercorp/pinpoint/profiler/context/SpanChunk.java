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

package com.navercorp.pinpoint.profiler.context;


import com.navercorp.pinpoint.common.util.Assert;
import com.navercorp.pinpoint.common.util.CollectionUtils;
import com.navercorp.pinpoint.profiler.context.id.TraceRoot;

import java.util.List;


/**
 * @author emeroad
 */
public class SpanChunk  {

    private final TraceRoot traceRoot;

    private final List<SpanEvent> spanEventList; // required


    public SpanChunk(TraceRoot traceRoot, List<SpanEvent> spanEventList) {
        this.traceRoot = Assert.requireNonNull(traceRoot, "traceRoot must not be null");
        this.spanEventList = Assert.requireNonNull(spanEventList, "spanEventList must not be null");
    }

    public TraceRoot getTraceRoot() {
        return traceRoot;
    }


    public List<SpanEvent> getSpanEventList() {
        return spanEventList;
    }

}
