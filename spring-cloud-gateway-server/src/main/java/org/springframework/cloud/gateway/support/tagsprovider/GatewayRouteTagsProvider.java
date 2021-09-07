/*
 * Copyright 2013-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.gateway.support.tagsprovider;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.web.server.ServerWebExchange;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 * Create route-related tags. By default, routeId & routeUri. A set of metadata keys can
 * be supplied to filter route metadata and create GatewayTags of the matched values.
 * Non-String metadata items will be ignored to avoid adding high-cardinality tags.
 *
 * @author Ingyu Hwang
 */
public class GatewayRouteTagsProvider implements GatewayTagsProvider {

	private final Set<String> metadataKeys;

	public GatewayRouteTagsProvider(Set<String> metadataKeys) {
		this.metadataKeys = metadataKeys;
	}

	@Override
	public Tags apply(ServerWebExchange exchange) {
		Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);

		if (route != null) {
			final Map<String, Object> metadata = route.getMetadata();
			return Tags.of(metadataKeys.stream()
							.filter(key -> metadata.containsKey(key) && metadata.get(key) instanceof String)
							.map(key -> Tag.of(key, metadata.get(key).toString()))
					.collect(Collectors.toList()))
					.and("routeId", route.getId(), "routeUri", route.getUri().toString());
		}

		return Tags.empty();
	}

}
