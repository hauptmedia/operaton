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
package org.operaton.bpm.engine.test.api.optimize;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.operaton.bpm.engine.ManagementService;
import org.operaton.bpm.engine.ProcessEngineConfiguration;
import org.operaton.bpm.engine.RuntimeService;
import org.operaton.bpm.engine.impl.OptimizeService;
import org.operaton.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.operaton.bpm.engine.impl.persistence.entity.HistoricIncidentEntity;
import org.operaton.bpm.engine.impl.util.ClockUtil;
import org.operaton.bpm.engine.runtime.ProcessInstance;
import org.operaton.bpm.engine.test.ProcessEngineRule;
import org.operaton.bpm.engine.test.RequiredHistoryLevel;
import org.operaton.bpm.engine.test.api.runtime.FailingDelegate;
import org.operaton.bpm.engine.test.util.ProcessEngineTestRule;
import org.operaton.bpm.engine.test.util.ProvidedProcessEngineRule;
import org.operaton.bpm.model.bpmn.Bpmn;
import org.operaton.bpm.model.bpmn.BpmnModelInstance;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@RequiredHistoryLevel(ProcessEngineConfiguration.HISTORY_FULL)
public class GetOpenHistoricIncidentsForOptimizeTest {

  private static final String PROCESS_DEFINITION_KEY = "oneFailingServiceTaskProcess";
  private static final BpmnModelInstance FAILING_SERVICE_TASK_MODEL =
    Bpmn.createExecutableProcess(PROCESS_DEFINITION_KEY)
      .startEvent("start")
      .serviceTask("task")
        .operatonAsyncBefore()
        .operatonClass(FailingDelegate.class.getName())
      .endEvent("end")
      .done();

  public ProcessEngineRule engineRule = new ProvidedProcessEngineRule();
  protected ProcessEngineTestRule testHelper = new ProcessEngineTestRule(engineRule);

  @Rule
  public RuleChain ruleChain = RuleChain.outerRule(engineRule).around(testHelper);

  private OptimizeService optimizeService;
  private RuntimeService runtimeService;
  private ManagementService managementService;

  @Before
  public void init() {
    ProcessEngineConfigurationImpl config =
      engineRule.getProcessEngineConfiguration();
    optimizeService = config.getOptimizeService();
    runtimeService = engineRule.getRuntimeService();
    managementService = engineRule.getManagementService();
  }

  @After
  public void cleanUp() {
    ClockUtil.reset();
  }

  @Test
  public void getOpenHistoricIncidents() {
     // given
    testHelper.deploy(FAILING_SERVICE_TASK_MODEL);
    startProcessInstanceAndFailWithIncident();

    // when
    List<HistoricIncidentEntity> openIncidents =
      optimizeService.getOpenHistoricIncidents(pastDate(), null, 10);

    // then
    assertThat(openIncidents).hasSize(1);
    assertThatInstanceHasAllImportantInformation(openIncidents.get(0));
  }

  @Test
  public void fishedAfterParameterWorks() {
     // given
    testHelper.deploy(FAILING_SERVICE_TASK_MODEL);
    Date now = new Date();
    ClockUtil.setCurrentTime(now);
    startProcessInstanceAndFailWithIncident();

    Date nowPlus2Seconds = new Date(now.getTime() + 2000L);
    ClockUtil.setCurrentTime(nowPlus2Seconds);
    final ProcessInstance processInstance = startProcessInstanceAndFailWithIncident();

    // when
    List<HistoricIncidentEntity> openIncidents =
      optimizeService.getOpenHistoricIncidents(now, null, 10);

    // then
    assertThat(openIncidents).hasSize(1);
    assertThat(openIncidents.get(0).getProcessInstanceId()).isEqualTo(processInstance.getId());

  }

  @Test
  public void fishedAtParameterWorks() {
     // given
    testHelper.deploy(FAILING_SERVICE_TASK_MODEL);
    Date now = new Date();
    ClockUtil.setCurrentTime(now);
    final ProcessInstance processInstance = startProcessInstanceAndFailWithIncident();
    Date nowPlus2Seconds = new Date(now.getTime() + 2000L);
    ClockUtil.setCurrentTime(nowPlus2Seconds);
    startProcessInstanceAndFailWithIncident();

    // when
    List<HistoricIncidentEntity> openIncidents =
      optimizeService.getOpenHistoricIncidents(null, now, 10);

    // then
    assertThat(openIncidents).hasSize(1);
    assertThat(openIncidents.get(0).getProcessInstanceId()).isEqualTo(processInstance.getId());
  }

  @Test
  public void fishedAfterAndFinishedAtParameterWorks() {
     // given
    testHelper.deploy(FAILING_SERVICE_TASK_MODEL);
    Date now = new Date();
    ClockUtil.setCurrentTime(now);
    startProcessInstanceAndFailWithIncident();
    Date nowPlus2Seconds = new Date(now.getTime() + 2000L);
    ClockUtil.setCurrentTime(nowPlus2Seconds);
    startProcessInstanceAndFailWithIncident();

    // when
    List<HistoricIncidentEntity> openIncidents =
      optimizeService.getOpenHistoricIncidents(now, now, 10);

    // then
    assertThat(openIncidents).isEmpty();
  }

  @Test
  public void maxResultsParameterWorks() {
     // given
    testHelper.deploy(FAILING_SERVICE_TASK_MODEL);
    for (int i = 0; i < 5; i++) {
      startProcessInstanceAndFailWithIncident();
    }

    // when
    List<HistoricIncidentEntity> openIncidents =
      optimizeService.getOpenHistoricIncidents(pastDate(), null, 3);

    // then
    assertThat(openIncidents).hasSize(3);
  }

  @Test
  public void resultIsSortedByEndTime() {
     // given
    testHelper.deploy(FAILING_SERVICE_TASK_MODEL);
    Date now = new Date();
    Date nowPlus1Second = new Date(now.getTime() + 1000L);
    ClockUtil.setCurrentTime(nowPlus1Second);
    ProcessInstance processInstance1 = startProcessInstanceAndFailWithIncident();

    Date nowPlus2Seconds = new Date(now.getTime() + 2000L);
    ClockUtil.setCurrentTime(nowPlus2Seconds);
    ProcessInstance processInstance2 = startProcessInstanceAndFailWithIncident();

    Date nowPlus4Seconds = new Date(nowPlus2Seconds.getTime() + 2000L);
    ClockUtil.setCurrentTime(nowPlus4Seconds);
    final ProcessInstance processInstance3 = startProcessInstanceAndFailWithIncident();

    // when
    List<HistoricIncidentEntity> openIncidents =
      optimizeService.getOpenHistoricIncidents(now, null, 10);

    // then
    assertThat(openIncidents).hasSize(3);
    assertThat(openIncidents.get(0).getProcessInstanceId()).isEqualTo(processInstance1.getId());
    assertThat(openIncidents.get(1).getProcessInstanceId()).isEqualTo(processInstance2.getId());
    assertThat(openIncidents.get(2).getProcessInstanceId()).isEqualTo(processInstance3.getId());
  }

  @Test
  public void fetchOnlyOpenIncidents() {
    // given
    testHelper.deploy(FAILING_SERVICE_TASK_MODEL);
    final ProcessInstance processInstanceWithCompletedIncident = startProcessInstanceAndFailWithIncident();
    retryAndSucceed(processInstanceWithCompletedIncident);
    final ProcessInstance processInstanceWithOpenIncident = startProcessInstanceAndFailWithIncident();

    // when
    List<HistoricIncidentEntity> openIncidents =
      optimizeService.getOpenHistoricIncidents(pastDate(), null, 10);

    // then
    assertThat(openIncidents).hasSize(1);
    assertThat(openIncidents.get(0).getProcessInstanceId()).isEqualTo(processInstanceWithOpenIncident.getId());
  }

  private Date pastDate() {
    return new Date(2L);
  }

  private void assertThatInstanceHasAllImportantInformation(HistoricIncidentEntity historicIncidentEntity) {
    assertThat(historicIncidentEntity).isNotNull();
    assertThat(historicIncidentEntity.getId()).isNotNull();
    assertThat(historicIncidentEntity.getProcessDefinitionKey()).isEqualTo(PROCESS_DEFINITION_KEY);
    assertThat(historicIncidentEntity.getProcessDefinitionVersion()).isNull();
    assertThat(historicIncidentEntity.getProcessDefinitionId()).isNotNull();
    assertThat(historicIncidentEntity.getCreateTime()).isNotNull();
    assertThat(historicIncidentEntity.getEndTime()).isNull();
  }

  private void retryAndSucceed(final ProcessInstance processInstance) {
    runtimeService.setVariable(processInstance.getId(), "fail", false);
    String jobId = managementService.createJobQuery().processInstanceId(processInstance.getId()).singleResult().getId();
    managementService.setJobRetries(jobId, 1);
  }

  private ProcessInstance startProcessInstanceAndFailWithIncident() {
    final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
    String jobId = managementService.createJobQuery().processInstanceId(processInstance.getId()).singleResult().getId();
    managementService.setJobRetries(jobId, 0); // creates incident
    return processInstance;
  }

}
