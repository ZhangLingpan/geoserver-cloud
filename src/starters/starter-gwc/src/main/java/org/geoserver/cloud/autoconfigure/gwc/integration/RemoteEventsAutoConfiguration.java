/*
 * (c) 2022 Open Source Geospatial Foundation - all rights reserved This code is licensed under the
 * GPL 2.0 license, available at the root application directory.
 */
package org.geoserver.cloud.autoconfigure.gwc.integration;

import lombok.extern.slf4j.Slf4j;

import org.geoserver.cloud.autoconfigure.bus.ConditionalOnGeoServerRemoteEventsEnabled;
import org.geoserver.cloud.autoconfigure.gwc.ConditionalOnGeoWebCacheEnabled;
import org.geoserver.cloud.gwc.bus.GeoWebCacheRemoteEventsBroker;
import org.geoserver.cloud.gwc.bus.RemoteGeoWebCacheEvent;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.cloud.bus.BusAutoConfiguration;
import org.springframework.cloud.bus.ServiceMatcher;
import org.springframework.cloud.bus.event.RemoteApplicationEvent;
import org.springframework.cloud.bus.jackson.RemoteApplicationEventScan;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;

/**
 * @since 1.0
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(BusAutoConfiguration.class)
@ConditionalOnGeoWebCacheEnabled
@ConditionalOnGeoServerRemoteEventsEnabled
@RemoteApplicationEventScan(basePackageClasses = {RemoteGeoWebCacheEvent.class})
@Slf4j(topic = "org.geoserver.cloud.autoconfigure.gwc.integration")
public class RemoteEventsAutoConfiguration {

    public @PostConstruct void log() {
        log.info("GeoWebCache remote events integration enabled");
    }

    public @Bean GeoWebCacheRemoteEventsBroker tileLayerRemoteEventBroadcaster( //
            ApplicationEventPublisher eventPublisher, ServiceMatcher busServiceMatcher) {

        Supplier<String> originServiceId = busServiceMatcher::getBusId;
        Function<RemoteApplicationEvent, Boolean> selfServiceCheck = busServiceMatcher::isFromSelf;
        Consumer<Object> publisher = eventPublisher::publishEvent;
        return new GeoWebCacheRemoteEventsBroker(originServiceId, selfServiceCheck, publisher);
    }
}