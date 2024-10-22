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
package org.operaton.bpm.model.bpmn.impl.instance.operaton;

import java.util.Collection;
import org.operaton.bpm.model.bpmn.impl.instance.BpmnModelElementInstanceImpl;
import org.operaton.bpm.model.bpmn.instance.operaton.OperatonInputOutput;
import org.operaton.bpm.model.bpmn.instance.operaton.OperatonInputParameter;
import org.operaton.bpm.model.bpmn.instance.operaton.OperatonOutputParameter;
import org.operaton.bpm.model.xml.ModelBuilder;
import org.operaton.bpm.model.xml.impl.instance.ModelTypeInstanceContext;
import org.operaton.bpm.model.xml.type.ModelElementTypeBuilder;
import org.operaton.bpm.model.xml.type.ModelElementTypeBuilder.ModelTypeInstanceProvider;
import org.operaton.bpm.model.xml.type.child.ChildElementCollection;
import org.operaton.bpm.model.xml.type.child.SequenceBuilder;

import static org.operaton.bpm.model.bpmn.impl.BpmnModelConstants.CAMUNDA_ELEMENT_INPUT_OUTPUT;
import static org.operaton.bpm.model.bpmn.impl.BpmnModelConstants.OPERATON_NS;

/**
 * The BPMN inputOutput operaton extension element
 *
 * @author Sebastian Menski
 */
public class OperatonInputOutputImpl extends BpmnModelElementInstanceImpl implements OperatonInputOutput {

  protected static ChildElementCollection<OperatonInputParameter> operatonInputParameterCollection;
  protected static ChildElementCollection<OperatonOutputParameter> operatonOutputParameterCollection;

  public static void registerType(ModelBuilder modelBuilder) {
    ModelElementTypeBuilder typeBuilder = modelBuilder.defineType(OperatonInputOutput.class, CAMUNDA_ELEMENT_INPUT_OUTPUT)
      .namespaceUri(OPERATON_NS)
      .instanceProvider(new ModelTypeInstanceProvider<OperatonInputOutput>() {
        public OperatonInputOutput newInstance(ModelTypeInstanceContext instanceContext) {
          return new OperatonInputOutputImpl(instanceContext);
        }
      });

    SequenceBuilder sequenceBuilder = typeBuilder.sequence();

    operatonInputParameterCollection = sequenceBuilder.elementCollection(OperatonInputParameter.class)
      .build();

    operatonOutputParameterCollection = sequenceBuilder.elementCollection(OperatonOutputParameter.class)
      .build();

    typeBuilder.build();
  }

  public OperatonInputOutputImpl(ModelTypeInstanceContext instanceContext) {
    super(instanceContext);
  }

  public Collection<OperatonInputParameter> getOperatonInputParameters() {
    return operatonInputParameterCollection.get(this);
  }

  public Collection<OperatonOutputParameter> getOperatonOutputParameters() {
    return operatonOutputParameterCollection.get(this);
  }
}