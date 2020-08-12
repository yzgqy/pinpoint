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

package com.navercorp.pinpoint.bootstrap.plugin.request;

import com.navercorp.pinpoint.bootstrap.config.Filter;
import com.navercorp.pinpoint.bootstrap.config.SkipFilter;
import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.SpanEventRecorder;
import com.navercorp.pinpoint.bootstrap.context.SpanRecorder;
import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.bootstrap.plugin.http.HttpStatusCodeRecorder;
import com.navercorp.pinpoint.bootstrap.plugin.proxy.ProxyHttpHeaderRecorder;
import com.navercorp.pinpoint.bootstrap.plugin.request.method.ServletSyncMethodDescriptor;
import com.navercorp.pinpoint.bootstrap.plugin.request.util.ParameterRecorder;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.common.util.Assert;

/**
 * @author jaehong.kim
 */
public class ServletRequestListenerInterceptorHelper<T> {
    private static final MethodDescriptor SERVLET_SYNC_METHOD_DESCRIPTOR = new ServletSyncMethodDescriptor();

    private PLogger logger = PLoggerFactory.getLogger(this.getClass());
    private final boolean isDebug = logger.isDebugEnabled();
    private final boolean isTrace = logger.isTraceEnabled();

    private final TraceContext traceContext;
    private final ServiceType serviceType;
    private final RequestAdaptor<T> requestAdaptor;

    private final Filter<String> excludeUrlFilter;
    private final RequestTraceReader<T> requestTraceReader;
    private final ProxyHttpHeaderRecorder<T> proxyHttpHeaderRecorder;
    private final ServerRequestRecorder<T> serverRequestRecorder;
    private final HttpStatusCodeRecorder httpStatusCodeRecorder;

    private final ParameterRecorder<T> parameterRecorder;

    public ServletRequestListenerInterceptorHelper(final ServiceType serviceType, final TraceContext traceContext, RequestAdaptor<T> requestAdaptor, final Filter<String> excludeUrlFilter, ParameterRecorder<T> parameterRecorder) {
        this.serviceType = Assert.requireNonNull(serviceType, "serviceType must not be null");
        this.traceContext = Assert.requireNonNull(traceContext, "traceContext must not be null");
        this.requestAdaptor = Assert.requireNonNull(requestAdaptor, "requestAdaptor must not be null");
        this.requestTraceReader = new RequestTraceReader<T>(traceContext, requestAdaptor, true);
        this.proxyHttpHeaderRecorder = new ProxyHttpHeaderRecorder<T>(traceContext.getProfilerConfig().isProxyHttpHeaderEnable(), requestAdaptor);
        this.excludeUrlFilter = defaultFilter(excludeUrlFilter);
        this.parameterRecorder = Assert.requireNonNull(parameterRecorder, "parameterRecorder must not be null");
        this.serverRequestRecorder = new ServerRequestRecorder<T>(requestAdaptor);
        this.httpStatusCodeRecorder = new HttpStatusCodeRecorder(traceContext.getProfilerConfig().getHttpStatusCodeErrors());

        this.traceContext.cacheApi(SERVLET_SYNC_METHOD_DESCRIPTOR);
    }

    private <T> Filter<T> defaultFilter(Filter<T> excludeUrlFilter) {
        if (excludeUrlFilter == null) {
            return new SkipFilter<T>();
        }
        return excludeUrlFilter;
    }

    public void initialized(T request, final ServiceType serviceType, final MethodDescriptor methodDescriptor) {
        Assert.requireNonNull(request, "request must not be null");
        Assert.requireNonNull(serviceType, "serviceType must not be null");
        Assert.requireNonNull(methodDescriptor, "methodDescriptor must not be null");

        if (isDebug) {
            logger.debug("Initialized requestEvent. request={}, serviceType={}, methodDescriptor={}", request, serviceType, methodDescriptor);
        }

        final Trace trace = createTrace(request);
        if (trace == null) {
            return;
        }

        if (!trace.canSampled()) {
            return;
        }

        final SpanEventRecorder recorder = trace.traceBlockBegin();
        recorder.recordServiceType(serviceType);
        recorder.recordApi(methodDescriptor);
    }

    private Trace createTrace(T request) {
        final String requestURI = requestAdaptor.getRpcName(request);
        if (this.excludeUrlFilter.filter(requestURI)) {
            if (isTrace) {
                logger.trace("Filter requestURI={}", requestURI);
            }
            return null;
        }

        final Trace trace = this.requestTraceReader.read(request);
        if (trace.canSampled()) {
            final SpanRecorder recorder = trace.getSpanRecorder();
            // record root span
            recorder.recordServiceType(this.serviceType);
            recorder.recordApi(SERVLET_SYNC_METHOD_DESCRIPTOR);
            this.serverRequestRecorder.record(recorder, request);
            // record proxy HTTP header.
            this.proxyHttpHeaderRecorder.record(recorder, request);
        }
        return trace;
    }

    public void destroyed(T request, final Throwable throwable, final int statusCode) {
        if (isDebug) {
            logger.debug("Destroyed requestEvent. request={}, throwable={}, statusCode={}", request, throwable, statusCode);
        }

        final Trace trace = this.traceContext.currentRawTraceObject();
        if (trace == null) {
            return;
        }

        // TODO STATDISABLE this logic was added to disable statistics tracing
        if (!trace.canSampled()) {
            traceContext.removeTraceObject();
            trace.close();
            return;
        }

        try {
            final SpanEventRecorder recorder = trace.currentSpanEventRecorder();
            recorder.recordException(throwable);
            this.httpStatusCodeRecorder.record(trace.getSpanRecorder(), statusCode);
            // Must be executed in destroyed()
            this.parameterRecorder.record(recorder, request, throwable);
        } finally {
            trace.traceBlockEnd();
            this.traceContext.removeTraceObject();
            trace.close();
        }
    }
}