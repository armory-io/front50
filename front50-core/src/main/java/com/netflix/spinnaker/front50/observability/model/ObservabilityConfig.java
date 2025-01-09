/*
 * Copyright 2025 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.front50.observability.model;

import com.netflix.spinnaker.kork.observability.model.ObservabilityConfigurationProperites;
import com.netflix.spinnaker.kork.observability.prometheus.PrometheusRegistrySupplier;
import com.netflix.spinnaker.kork.observability.registry.*;
import com.netflix.spinnaker.kork.observability.service.MeterFilterService;
import com.netflix.spinnaker.kork.observability.service.TagsService;
import com.netflix.spinnaker.kork.version.SpringPackageVersionResolver;
import com.netflix.spinnaker.kork.version.VersionResolver;
import com.netflix.spinnaker.kork.web.controllers.ObservabilityPrometheusConfig;
import com.netflix.spinnaker.kork.web.controllers.PrometheusScrapeEndpoint;
import io.micrometer.core.instrument.Clock;
import io.prometheus.client.CollectorRegistry;
import java.util.Collection;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ConfigurationProperties(prefix = "observability")
public class ObservabilityConfig {

  @Bean
  public ObservabilityConfigurationProperites observabilityConfigurationProperites() {
    return new ObservabilityConfigurationProperites();
  }

  @Bean
  @ConditionalOnMissingBean(VersionResolver.class)
  public static VersionResolver versionResolver(ApplicationContext applicationContext) {
    return new SpringPackageVersionResolver(applicationContext);
  }

  @Bean
  public TagsService tagsService(
      ObservabilityConfigurationProperites observabilityConfigurationProperites,
      VersionResolver versionResolver,
      @Value("${spring.application.name:#{null}}") String springInjectedApplicationName) {
    return new TagsService(
        observabilityConfigurationProperites, versionResolver, springInjectedApplicationName);
  }

  @Bean
  public AddDefaultTagsRegistryCustomizer addDefaultTagsRegistryCustomizer(
      TagsService tagsService) {
    return new AddDefaultTagsRegistryCustomizer(tagsService);
  }

  @Bean
  public MeterFilterService meterFilterService() {
    return new MeterFilterService();
  }

  @Bean
  public AddFiltersRegistryCustomizer addFiltersRegistryCustomizer(
      MeterFilterService meterFilterService) {
    return new AddFiltersRegistryCustomizer(meterFilterService);
  }

  @Bean
  public CollectorRegistry collectorRegistry() {
    return new CollectorRegistry();
  }

  @Bean
  public PrometheusRegistrySupplier prometheusRegistrySupplier(
      ObservabilityConfigurationProperites pluginConfig,
      CollectorRegistry collectorRegistry,
      Clock clock) {
    return new PrometheusRegistrySupplier(pluginConfig, collectorRegistry, clock);
  }

  @Bean
  @Primary
  public ArmoryObservabilityCompositeRegistry armoryObservabilityCompositeRegistry(
      Clock clock,
      Collection<Supplier<RegistryConfigWrapper>> registrySuppliers,
      Collection<RegistryCustomizer> meterRegistryCustomizers) {
    return new ArmoryObservabilityCompositeRegistry(
        clock, registrySuppliers, meterRegistryCustomizers);
  }

  @Bean
  public PrometheusScrapeEndpoint prometheusScrapeEndpoint(CollectorRegistry collectorRegistry) {
    return new PrometheusScrapeEndpoint(collectorRegistry);
  }

  @Bean
  public ObservabilityPrometheusConfig observabilityPrometheusConfig(
      ObservabilityConfigurationProperites pluginConfig) {
    return new ObservabilityPrometheusConfig(pluginConfig);
  }
}
