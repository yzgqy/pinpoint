/*
 * Copyright 2017 NAVER Corp.
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
package com.navercorp.pinpoint.collector.cluster.flink;

import com.navercorp.pinpoint.collector.cluster.zookeeper.ZookeeperClusterManager;
import com.navercorp.pinpoint.collector.config.CollectorConfiguration;
import com.navercorp.pinpoint.common.server.cluster.zookeeper.CuratorZookeeperClient;
import com.navercorp.pinpoint.common.server.cluster.zookeeper.ZookeeperClient;
import com.navercorp.pinpoint.common.server.cluster.zookeeper.ZookeeperEventWatcher;
import com.navercorp.pinpoint.common.server.util.concurrent.CommonState;
import com.navercorp.pinpoint.common.server.util.concurrent.CommonStateContext;
import com.navercorp.pinpoint.common.util.Assert;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

/**
 * @author minwoo.jung
 */
public class FlinkClusterService {

    private static final String PINPOINT_FLINK_CLUSTER_PATH = "/pinpoint-cluster/flink";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CommonStateContext serviceState;
    private final CollectorConfiguration config;
    private final FlinkClusterConnectionManager clusterConnectionManager;

    private ZookeeperClient client;
    private ZookeeperClusterManager zookeeperClusterManager;

    public FlinkClusterService(CollectorConfiguration config, FlinkClusterConnectionManager clusterConnectionManager) {
        this.config = Assert.requireNonNull(config, "config must not be null");
        this.serviceState = new CommonStateContext();
        this.clusterConnectionManager = Assert.requireNonNull(clusterConnectionManager, "clusterConnectionManager must not be null");
    }

    @PostConstruct
    public void setUp() throws KeeperException, IOException, InterruptedException {
        if (!config.isFlinkClusterEnable()) {
            logger.info("flink cluster disable.");
            return;
        }

        switch (this.serviceState.getCurrentState()) {
            case NEW:
                if (this.serviceState.changeStateInitializing()) {
                    logger.info("{} initialization started.", this.getClass().getSimpleName());

                    ClusterManagerWatcher watcher = new ClusterManagerWatcher();
                    this.client = new CuratorZookeeperClient(config.getFlinkClusterZookeeperAddress(), config.getFlinkClusterSessionTimeout(), watcher);
                    this.client.connect();

                    this.zookeeperClusterManager = new ZookeeperClusterManager(client, PINPOINT_FLINK_CLUSTER_PATH, clusterConnectionManager);
                    this.zookeeperClusterManager.start();

                    this.serviceState.changeStateStarted();
                    logger.info("{} initialization completed.", this.getClass().getSimpleName());

                    if (client.isConnected()) {
                        watcher.handleConnected();
                    }
                }
                break;
            case INITIALIZING:
                logger.info("{} already initializing.", this.getClass().getSimpleName());
                break;
            case STARTED:
                logger.info("{} already started.", this.getClass().getSimpleName());
                break;
            case DESTROYING:
                throw new IllegalStateException("Already destroying.");
            case STOPPED:
                throw new IllegalStateException("Already stopped.");
            case ILLEGAL_STATE:
                throw new IllegalStateException("Invalid State.");
        }
    }

    @PreDestroy
    public void tearDown() {
        if (!config.isFlinkClusterEnable()) {
            logger.info("flink cluster disable.");
            return;
        }
        if (!(this.serviceState.changeStateDestroying())) {
            CommonState state = this.serviceState.getCurrentState();
            logger.info("{} already {}.", this.getClass().getSimpleName(), state.toString());
            return;
        }

        logger.info("{} destroying started.", this.getClass().getSimpleName());

        if (this.zookeeperClusterManager != null) {
            zookeeperClusterManager.stop();
        }
        if (client != null) {
            client.close();
        }

        this.serviceState.changeStateStopped();
        logger.info("{} destroying completed.", this.getClass().getSimpleName());
    }

    public ZookeeperClusterManager getZookeeperClusterManager() {
        return zookeeperClusterManager;
    }

    class ClusterManagerWatcher implements ZookeeperEventWatcher {

        @Override
        public void process(WatchedEvent event) {
            logger.debug("Process Zookeeper Event({})", event);

            EventType eventType = event.getType();

            if (serviceState.isStarted() && client.isConnected()) {
                // duplicate event possible - but the logic does not change
                if (eventType == EventType.NodeChildrenChanged) {
                    String eventPath = event.getPath();

                    if (PINPOINT_FLINK_CLUSTER_PATH.equals(eventPath)) {
                        zookeeperClusterManager.handleAndRegisterWatcher(eventPath);
                    } else {
                        logger.warn("Unknown Path ChildrenChanged {}.", eventPath);
                    }
                }
            }
        }

        @Override
        public boolean handleConnected() {
            if (serviceState.isStarted()) {
                zookeeperClusterManager.handleAndRegisterWatcher(PINPOINT_FLINK_CLUSTER_PATH);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean handleDisconnected() {
            return true;
        }

    }

}
