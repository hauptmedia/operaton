/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
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
package org.operaton.bpm.quarkus.engine.test.config;

import io.quarkus.test.QuarkusUnitTest;
import jakarta.inject.Inject;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.operaton.bpm.quarkus.engine.extension.OperatonEngineConfig;
import org.operaton.bpm.quarkus.engine.test.helper.ProcessEngineAwareExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class OperatonEngineJobExecutorConfigTest {

  @RegisterExtension
  static final QuarkusUnitTest unitTest = new ProcessEngineAwareExtension()
      .withConfigurationResource("org/operaton/bpm/quarkus/engine/test/config/" +
                                     "job-executor-application.properties")
      .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class));

  @Inject
  OperatonEngineConfig config;

  @Test
  public void shouldLoadJobExecutorThreadPoolProperties() {
    // given a custom application.properties file

    // then
    assertThat(config.jobExecutor().threadPool().maxPoolSize()).isEqualTo(12);
    assertThat(config.jobExecutor().threadPool().queueSize()).isEqualTo(5);
  }

  @Test
  public void shouldLoadJobAcquisitionProperties() {
    // given a custom application.properties file

    // then
    assertThat(config.jobExecutor().genericConfig().get("max-jobs-per-acquisition")).isEqualTo("5");
    assertThat(config.jobExecutor().genericConfig().get("lock-time-in-millis")).isEqualTo("500000");
    assertThat(config.jobExecutor().genericConfig().get("wait-time-in-millis")).isEqualTo("7000");
    assertThat(config.jobExecutor().genericConfig().get("max-wait")).isEqualTo("65000");
    assertThat(config.jobExecutor().genericConfig().get("backoff-time-in-millis")).isEqualTo("5");
    assertThat(config.jobExecutor().genericConfig().get("max-backoff")).isEqualTo("5");
    assertThat(config.jobExecutor().genericConfig().get("backoff-decrease-threshold")).isEqualTo("120");
    assertThat(config.jobExecutor().genericConfig().get("wait-increase-factor")).isEqualTo("3");
  }
}
