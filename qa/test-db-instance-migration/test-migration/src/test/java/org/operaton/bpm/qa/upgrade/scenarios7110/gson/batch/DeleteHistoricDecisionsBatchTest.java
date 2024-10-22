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
package org.operaton.bpm.qa.upgrade.scenarios7110.gson.batch;

import org.operaton.bpm.engine.batch.Batch;
import org.operaton.bpm.engine.history.HistoricDecisionInstance;
import org.operaton.bpm.engine.runtime.Job;
import org.operaton.bpm.engine.test.ProcessEngineRule;
import org.operaton.bpm.qa.upgrade.Origin;
import org.operaton.bpm.qa.upgrade.ScenarioUnderTest;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Tassilo Weidner
 */
@ScenarioUnderTest("DeleteHistoricDecisionsBatchScenario")
@Origin("7.11.0")
public class DeleteHistoricDecisionsBatchTest {

  @Rule
  public ProcessEngineRule engineRule = new ProcessEngineRule("operaton.cfg.xml");

  @ScenarioUnderTest("initDeleteHistoricDecisionsBatch.1")
  @Test
  public void testDeleteHistoricDecisionsBatch() {
    List<HistoricDecisionInstance> historicDecisionInstances = engineRule.getHistoryService()
      .createHistoricDecisionInstanceQuery()
      .decisionDefinitionKey("decision_710")
      .list();

    // assume
    assertThat(historicDecisionInstances.size(), is(10));

    Batch batch = engineRule.getManagementService().createBatchQuery()
      .type(Batch.TYPE_HISTORIC_DECISION_INSTANCE_DELETION)
      .singleResult();

    String jobId = engineRule.getManagementService().createJobQuery()
      .jobDefinitionId(batch.getSeedJobDefinitionId())
      .singleResult()
      .getId();

    engineRule.getManagementService().executeJob(jobId);

    List<Job> jobs = engineRule.getManagementService().createJobQuery()
      .jobDefinitionId(batch.getBatchJobDefinitionId())
      .list();

    // when
    for (Job job : jobs) {
      engineRule.getManagementService().executeJob(job.getId());
    }

    historicDecisionInstances = engineRule.getHistoryService()
      .createHistoricDecisionInstanceQuery()
      .list();

    // then
    assertThat(historicDecisionInstances.size(), is(0));
  }

}