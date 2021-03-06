/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.cloudfoundry.discovery;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.cloudfoundry.CloudFoundryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Josh Long
 */
@Configuration
@ConditionalOnClass(CloudFoundryOperations.class)
@ConditionalOnProperty(value = "spring.cloud.cloudfoundry.discovery.enabled", matchIfMissing = true)
@EnableConfigurationProperties(CloudFoundryDiscoveryProperties.class)
public class CloudFoundryDiscoveryClientConfiguration {

	@Autowired
	private CloudFoundryDiscoveryProperties discovery;

	@Bean
	@ConditionalOnMissingBean(CloudFoundryDiscoveryClient.class)
	public CloudFoundryDiscoveryClient cloudFoundryDiscoveryClient(
			CloudFoundryOperations cf, CloudFoundryService svc) {
		return new CloudFoundryDiscoveryClient(cf, svc);
	}

	@Bean
	public CloudFoundryHeartbeatSender cloudFoundryHeartbeatSender(CloudFoundryDiscoveryClient client) {
		return new CloudFoundryHeartbeatSender(client);
	}
}