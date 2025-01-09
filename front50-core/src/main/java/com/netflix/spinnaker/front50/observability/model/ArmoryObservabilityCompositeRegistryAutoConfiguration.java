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

import com.netflix.spinnaker.kork.observability.registry.ArmoryObservabilityCompositeRegistry;
import com.netflix.spinnaker.kork.observability.registry.RegistryConfigWrapper;
import com.netflix.spinnaker.kork.observability.registry.RegistryCustomizer;
import io.micrometer.core.instrument.Clock;
import java.util.Collection;
import java.util.function.Supplier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({ObservabilityConfig.class})
@ConditionalOnClass({ArmoryObservabilityCompositeRegistry.class})
public class ArmoryObservabilityCompositeRegistryAutoConfiguration {

  @Bean
  public ArmoryObservabilityCompositeRegistry armoryObservabilityCompositeRegistry(
      Clock clock,
      Collection<Supplier<RegistryConfigWrapper>> registrySuppliers,
      Collection<RegistryCustomizer> meterRegistryCustomizers) {
    return new ArmoryObservabilityCompositeRegistry(
        clock, registrySuppliers, meterRegistryCustomizers);
  }
}
