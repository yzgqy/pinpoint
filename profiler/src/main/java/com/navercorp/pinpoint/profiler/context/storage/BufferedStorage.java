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

package com.navercorp.pinpoint.profiler.context.storage;

import com.navercorp.pinpoint.common.util.Assert;
import com.navercorp.pinpoint.common.util.CollectionUtils;
import com.navercorp.pinpoint.profiler.context.*;
import com.navercorp.pinpoint.profiler.context.id.TraceRoot;
import com.navercorp.pinpoint.profiler.sender.DataSender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author emeroad
 * @author jaehong.kim
 */
public class BufferedStorage implements Storage {
    private static final Logger logger = LoggerFactory.getLogger(BufferedStorage.class);
    private static final boolean isDebug = logger.isDebugEnabled();

    private static final int DEFAULT_BUFFER_SIZE = 20;

    private final int bufferSize;

    private final TraceRoot traceRoot;
    private List<SpanEvent> storage;
    private final DataSender<Object> dataSender;



    public BufferedStorage(TraceRoot traceRoot, DataSender<Object> dataSender, int bufferSize) {
        this.traceRoot = Assert.requireNonNull(traceRoot, "traceRoot must not be null");
        this.dataSender = Assert.requireNonNull(dataSender, "dataSender must not be null");
        this.bufferSize = bufferSize;
        this.storage = allocateBuffer();
    }

    @Override
    public void store(SpanEvent spanEvent) {
        final List<SpanEvent> storage = getBuffer();
        storage.add(spanEvent);

        if (overflow(storage)) {
            final List<SpanEvent> flushData = clearBuffer();
            final SpanChunk spanChunk = wrapSpanCHunk(flushData);
            final boolean success = this.dataSender.send(spanChunk);
            if (isDebug) {
                flushLog(success, spanChunk);
            }
        }
    }

    private boolean overflow(List<SpanEvent> storage) {
        return storage.size() >= bufferSize;
    }


    private List<SpanEvent> allocateBuffer() {
        return new ArrayList<SpanEvent>(this.bufferSize);
    }

    private List<SpanEvent> getBuffer() {
        List<SpanEvent> copy = this.storage;
        if (copy == null) {
            copy = allocateBuffer();
            this.storage = copy;
        }
        return copy;
    }

    private List<SpanEvent> clearBuffer() {
        final List<SpanEvent> copy = this.storage;
        this.storage = null;
        return copy;
    }

    @Override
    public void store(Span span) {
        final List<SpanEvent> spanEventList = clearBuffer();
        span.setSpanEventList(spanEventList);
        span.finish();

        final boolean success = this.dataSender.send(span);
        if (isDebug) {
            flushLog(success, span);
        }
    }

    public void flush() {
        final List<SpanEvent> spanEventList = clearBuffer();
        if (CollectionUtils.hasLength(spanEventList)) {
            final SpanChunk spanChunk = wrapSpanCHunk(spanEventList);

            final boolean success = this.dataSender.send(spanChunk);
            if (isDebug) {
                flushLog(success, spanChunk);
            }
        }
    }

    private void flushLog(boolean success, Object message) {
        if (success) {
            logger.debug("Flush {}", message);
        } else {
            logger.debug("Flush fail {}", message);
        }
    }

    private SpanChunk wrapSpanCHunk(List<SpanEvent> spanEventList) {
        final SpanChunk spanChunk = new SpanChunk(traceRoot, spanEventList);
        return spanChunk;
    }

    @Override
    public void close() {
    }

    @Override
    public String toString() {
        return "BufferedStorage{" + "bufferSize=" + bufferSize + ", dataSender=" + dataSender + '}';
    }
}