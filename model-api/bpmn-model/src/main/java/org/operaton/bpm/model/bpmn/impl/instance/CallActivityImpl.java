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
package org.operaton.bpm.model.bpmn.impl.instance;

import static org.operaton.bpm.model.bpmn.impl.BpmnModelConstants.BPMN20_NS;
import static org.operaton.bpm.model.bpmn.impl.BpmnModelConstants.BPMN_ATTRIBUTE_CALLED_ELEMENT;
import static org.operaton.bpm.model.bpmn.impl.BpmnModelConstants.BPMN_ELEMENT_CALL_ACTIVITY;
import static org.operaton.bpm.model.bpmn.impl.BpmnModelConstants.CAMUNDA_ATTRIBUTE_ASYNC;
import static org.operaton.bpm.model.bpmn.impl.BpmnModelConstants.CAMUNDA_ATTRIBUTE_CALLED_ELEMENT_BINDING;
import static org.operaton.bpm.model.bpmn.impl.BpmnModelConstants.CAMUNDA_ATTRIBUTE_CALLED_ELEMENT_TENANT_ID;
import static org.operaton.bpm.model.bpmn.impl.BpmnModelConstants.CAMUNDA_ATTRIBUTE_CALLED_ELEMENT_VERSION;
import static org.operaton.bpm.model.bpmn.impl.BpmnModelConstants.CAMUNDA_ATTRIBUTE_CALLED_ELEMENT_VERSION_TAG;
import static org.operaton.bpm.model.bpmn.impl.BpmnModelConstants.CAMUNDA_ATTRIBUTE_CASE_BINDING;
import static org.operaton.bpm.model.bpmn.impl.BpmnModelConstants.CAMUNDA_ATTRIBUTE_CASE_REF;
import static org.operaton.bpm.model.bpmn.impl.BpmnModelConstants.CAMUNDA_ATTRIBUTE_CASE_TENANT_ID;
import static org.operaton.bpm.model.bpmn.impl.BpmnModelConstants.CAMUNDA_ATTRIBUTE_CASE_VERSION;

import org.operaton.bpm.model.bpmn.BpmnModelInstance;
import org.operaton.bpm.model.bpmn.builder.CallActivityBuilder;
import static org.operaton.bpm.model.bpmn.impl.BpmnModelConstants.CAMUNDA_ATTRIBUTE_VARIABLE_MAPPING_CLASS;
import static org.operaton.bpm.model.bpmn.impl.BpmnModelConstants.CAMUNDA_ATTRIBUTE_VARIABLE_MAPPING_DELEGATE_EXPRESSION;
import static org.operaton.bpm.model.bpmn.impl.BpmnModelConstants.OPERATON_NS;
import org.operaton.bpm.model.bpmn.instance.Activity;
import org.operaton.bpm.model.bpmn.instance.CallActivity;
import org.operaton.bpm.model.xml.ModelBuilder;
import org.operaton.bpm.model.xml.impl.instance.ModelTypeInstanceContext;
import org.operaton.bpm.model.xml.type.ModelElementTypeBuilder;
import org.operaton.bpm.model.xml.type.ModelElementTypeBuilder.ModelTypeInstanceProvider;
import org.operaton.bpm.model.xml.type.attribute.Attribute;

/**
 * The BPMN callActivity element
 *
 * @author Sebastian Menski
 */
public class CallActivityImpl extends ActivityImpl implements CallActivity {

  protected static Attribute<String> calledElementAttribute;


  /** operaton extensions */

  protected static Attribute<Boolean> operatonAsyncAttribute;
  protected static Attribute<String> operatonCalledElementBindingAttribute;
  protected static Attribute<String> operatonCalledElementVersionAttribute;
  protected static Attribute<String> operatonCalledElementVersionTagAttribute;
  protected static Attribute<String> operatonCalledElementTenantIdAttribute;

  protected static Attribute<String> operatonCaseRefAttribute;
  protected static Attribute<String> operatonCaseBindingAttribute;
  protected static Attribute<String> operatonCaseVersionAttribute;
  protected static Attribute<String> operatonCaseTenantIdAttribute;
  protected static Attribute<String> operatonVariableMappingClassAttribute;
  protected static Attribute<String> operatonVariableMappingDelegateExpressionAttribute;

  public static void registerType(ModelBuilder modelBuilder) {
    ModelElementTypeBuilder typeBuilder = modelBuilder.defineType(CallActivity.class, BPMN_ELEMENT_CALL_ACTIVITY)
      .namespaceUri(BPMN20_NS)
      .extendsType(Activity.class)
      .instanceProvider(new ModelTypeInstanceProvider<CallActivity>() {
        public CallActivity newInstance(ModelTypeInstanceContext instanceContext) {
          return new CallActivityImpl(instanceContext);
        }
      });

    calledElementAttribute = typeBuilder.stringAttribute(BPMN_ATTRIBUTE_CALLED_ELEMENT)
      .build();

    /** operaton extensions */

    operatonAsyncAttribute = typeBuilder.booleanAttribute(CAMUNDA_ATTRIBUTE_ASYNC)
      .namespace(OPERATON_NS)
      .defaultValue(false)
      .build();

    operatonCalledElementBindingAttribute = typeBuilder.stringAttribute(CAMUNDA_ATTRIBUTE_CALLED_ELEMENT_BINDING)
      .namespace(OPERATON_NS)
      .build();

    operatonCalledElementVersionAttribute = typeBuilder.stringAttribute(CAMUNDA_ATTRIBUTE_CALLED_ELEMENT_VERSION)
      .namespace(OPERATON_NS)
      .build();

    operatonCalledElementVersionTagAttribute = typeBuilder.stringAttribute(CAMUNDA_ATTRIBUTE_CALLED_ELEMENT_VERSION_TAG)
      .namespace(OPERATON_NS)
      .build();

    operatonCaseRefAttribute = typeBuilder.stringAttribute(CAMUNDA_ATTRIBUTE_CASE_REF)
       .namespace(OPERATON_NS)
       .build();

    operatonCaseBindingAttribute = typeBuilder.stringAttribute(CAMUNDA_ATTRIBUTE_CASE_BINDING)
        .namespace(OPERATON_NS)
        .build();

    operatonCaseVersionAttribute = typeBuilder.stringAttribute(CAMUNDA_ATTRIBUTE_CASE_VERSION)
        .namespace(OPERATON_NS)
        .build();

    operatonCalledElementTenantIdAttribute = typeBuilder.stringAttribute(CAMUNDA_ATTRIBUTE_CALLED_ELEMENT_TENANT_ID)
        .namespace(OPERATON_NS)
        .build();

    operatonCaseTenantIdAttribute = typeBuilder.stringAttribute(CAMUNDA_ATTRIBUTE_CASE_TENANT_ID)
        .namespace(OPERATON_NS)
        .build();

    operatonVariableMappingClassAttribute = typeBuilder.stringAttribute(CAMUNDA_ATTRIBUTE_VARIABLE_MAPPING_CLASS)
      .namespace(OPERATON_NS)
      .build();

    operatonVariableMappingDelegateExpressionAttribute = typeBuilder.stringAttribute(CAMUNDA_ATTRIBUTE_VARIABLE_MAPPING_DELEGATE_EXPRESSION)
      .namespace(OPERATON_NS)
      .build();


    typeBuilder.build();
  }

  public CallActivityImpl(ModelTypeInstanceContext context) {
    super(context);
  }

  @Override
  public CallActivityBuilder builder() {
    return new CallActivityBuilder((BpmnModelInstance) modelInstance, this);
  }

  public String getCalledElement() {
    return calledElementAttribute.getValue(this);
  }

  public void setCalledElement(String calledElement) {
    calledElementAttribute.setValue(this, calledElement);
  }

  /**
   * @deprecated use isOperatonAsyncBefore() instead.
   */
  @Deprecated
  public boolean isOperatonAsync() {
    return operatonAsyncAttribute.getValue(this);
  }

  /**
   * @deprecated use setOperatonAsyncBefore() instead.
   */
  @Deprecated
  public void setOperatonAsync(boolean isOperatonAsync) {
    operatonAsyncAttribute.setValue(this, isOperatonAsync);
  }

  public String getOperatonCalledElementBinding() {
    return operatonCalledElementBindingAttribute.getValue(this);
  }

  public void setOperatonCalledElementBinding(String operatonCalledElementBinding) {
    operatonCalledElementBindingAttribute.setValue(this, operatonCalledElementBinding);
  }

  public String getOperatonCalledElementVersion() {
    return operatonCalledElementVersionAttribute.getValue(this);
  }

  public void setOperatonCalledElementVersion(String operatonCalledElementVersion) {
    operatonCalledElementVersionAttribute.setValue(this, operatonCalledElementVersion);
  }

  public String getOperatonCalledElementVersionTag() {
    return operatonCalledElementVersionTagAttribute.getValue(this);
  }

  public void setOperatonCalledElementVersionTag(String operatonCalledElementVersionTag) {
    operatonCalledElementVersionTagAttribute.setValue(this, operatonCalledElementVersionTag);
  }

  public String getOperatonCaseRef() {
    return operatonCaseRefAttribute.getValue(this);
  }

  public void setOperatonCaseRef(String operatonCaseRef) {
    operatonCaseRefAttribute.setValue(this, operatonCaseRef);
  }

  public String getOperatonCaseBinding() {
    return operatonCaseBindingAttribute.getValue(this);
  }

  public void setOperatonCaseBinding(String operatonCaseBinding) {
    operatonCaseBindingAttribute.setValue(this, operatonCaseBinding);
  }

  public String getOperatonCaseVersion() {
    return operatonCaseVersionAttribute.getValue(this);
  }

  public void setOperatonCaseVersion(String operatonCaseVersion) {
    operatonCaseVersionAttribute.setValue(this, operatonCaseVersion);
  }

  public String getOperatonCalledElementTenantId() {
    return operatonCalledElementTenantIdAttribute.getValue(this);
  }

  public void setOperatonCalledElementTenantId(String tenantId) {
    operatonCalledElementTenantIdAttribute.setValue(this, tenantId);
  }

  public String getOperatonCaseTenantId() {
    return operatonCaseTenantIdAttribute.getValue(this);
  }

  public void setOperatonCaseTenantId(String tenantId) {
    operatonCaseTenantIdAttribute.setValue(this, tenantId);
  }

  @Override
  public String getOperatonVariableMappingClass() {
    return operatonVariableMappingClassAttribute.getValue(this);
  }

  @Override
  public void setOperatonVariableMappingClass(String operatonClass) {
    operatonVariableMappingClassAttribute.setValue(this, operatonClass);
  }

  @Override
  public String getOperatonVariableMappingDelegateExpression() {
    return operatonVariableMappingDelegateExpressionAttribute.getValue(this);
  }

  @Override
  public void setOperatonVariableMappingDelegateExpression(String operatonExpression) {
    operatonVariableMappingDelegateExpressionAttribute.setValue(this, operatonExpression);
  }
}