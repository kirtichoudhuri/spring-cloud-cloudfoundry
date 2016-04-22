/*
 * Copyright 2013-2015 the original author or authors.
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

import javax.annotation.PostConstruct;

import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.IClientConfig;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import com.netflix.loadbalancer.ServerList;

/**
 * @author Josh Long
 */
@Configuration
public class CloudFoundryRibbonClientConfiguration {

	protected static final String DEFAULT_NAMESPACE = "ribbon";
	protected static final String VALUE_NOT_SET = "__not__set__";

	@Value("${ribbon.client.name}")
	private String serviceId;

	public CloudFoundryRibbonClientConfiguration() {
	}

	public CloudFoundryRibbonClientConfiguration(String svcId) {
		this.serviceId = svcId;
	}

	@Bean
	@ConditionalOnMissingBean
	public ServerList<?> ribbonServerList(CloudFoundryClient cloudFoundryClient,
			IClientConfig config) {
		CloudFoundryServerList cloudFoundryServerList = new CloudFoundryServerList(
				cloudFoundryClient);
		cloudFoundryServerList.initWithNiwsConfig(config);
		return cloudFoundryServerList;
	}

	@PostConstruct
	public void postConstruct() {
		// FIXME: what should this be?
		setProp(this.serviceId,
				CommonClientConfigKey.DeploymentContextBasedVipAddresses.key(),
				this.serviceId);
		setProp(this.serviceId, CommonClientConfigKey.EnableZoneAffinity.key(), "true");
	}

	protected void setProp(String serviceId, String suffix, String value) {
		// how to set the namespace properly?
		String key = getKey(serviceId, suffix);
		DynamicStringProperty property = getProperty(key);
		if (property.get().equals(VALUE_NOT_SET)) {
			ConfigurationManager.getConfigInstance().setProperty(key, value);
		}
	}

	protected DynamicStringProperty getProperty(String key) {
		return DynamicPropertyFactory.getInstance().getStringProperty(key, VALUE_NOT_SET);
	}

	protected String getKey(String serviceId, String suffix) {
		return serviceId + "." + DEFAULT_NAMESPACE + "." + suffix;
	}
}