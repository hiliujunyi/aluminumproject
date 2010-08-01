/*
 * Copyright 2009-2010 Levi Hoogenberg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.aluminumproject.libraries.actions;

import com.googlecode.aluminumproject.configuration.ConfigurationParameters;
import com.googlecode.aluminumproject.configuration.test.TestConfiguration;
import com.googlecode.aluminumproject.context.Context;
import com.googlecode.aluminumproject.context.DefaultContext;
import com.googlecode.aluminumproject.converters.DefaultConverterRegistry;
import com.googlecode.aluminumproject.libraries.test.actions.AnnotatedAction;
import com.googlecode.aluminumproject.libraries.test.actions.DynamicallyParameterisableAction;
import com.googlecode.aluminumproject.libraries.test.actions.ParameterAction;
import com.googlecode.aluminumproject.libraries.test.actions.TestAction;
import com.googlecode.aluminumproject.libraries.test.actions.TestActionParameter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("all")
@Test(groups = {"core", "fast"})
public class DefaultActionFactoryTest {
	private ActionFactory unannotatedActionFactory;
	private ActionFactory annotatedActionFactory;
	private ActionFactory parameterActionFactory;
	private ActionFactory dynamicallyParameterisableActionFactory;

	private Context context;

	@BeforeMethod
	public void createActionFactories() {
		ConfigurationParameters parameters = new ConfigurationParameters();
		TestConfiguration configuration = new TestConfiguration(parameters);

		configuration.setConverterRegistry(new DefaultConverterRegistry());
		configuration.getConverterRegistry().initialise(configuration, parameters);

		unannotatedActionFactory = new DefaultActionFactory(TestAction.class);
		unannotatedActionFactory.initialise(configuration, parameters);

		annotatedActionFactory = new DefaultActionFactory(AnnotatedAction.class);
		annotatedActionFactory.initialise(configuration, parameters);

		parameterActionFactory = new DefaultActionFactory(ParameterAction.class);
		parameterActionFactory.initialise(configuration, parameters);

		dynamicallyParameterisableActionFactory = new DefaultActionFactory(DynamicallyParameterisableAction.class);
		dynamicallyParameterisableActionFactory.initialise(configuration, parameters);

		context = new DefaultContext();
	}

	public void unannotatedActionShouldResultInHumanisedClassNameAsActionName() {
		String actionName = unannotatedActionFactory.getInformation().getName();
		assert actionName != null;
		assert actionName.equals("test action");
	}

	public void annotatedActionShouldResultInAnnotationAttributeAsActionName() {
		String actionName = annotatedActionFactory.getInformation().getName();
		assert actionName != null;
		assert actionName.equals("test");
	}

	public void unannotatedParameterShouldBeRecognised() {
		List<ActionParameterInformation> parameterInformation =
			unannotatedActionFactory.getInformation().getParameterInformation();
		assert parameterInformation != null;
		assert parameterInformation.size() == 1;
	}

	public void annotatedParameterShouldBeRecognised() {
		List<ActionParameterInformation> parameterInformation =
			annotatedActionFactory.getInformation().getParameterInformation();
		assert parameterInformation != null;
		assert parameterInformation.size() == 1;
	}

	public void parameterOfTypeActionParameterShouldBeRecognised() {
		List<ActionParameterInformation> parameterInformation =
			annotatedActionFactory.getInformation().getParameterInformation();
		assert parameterInformation != null;
		assert parameterInformation.size() == 1;
	}

	@Test(dependsOnMethods = "unannotatedParameterShouldBeRecognised")
	public void unannotatedParameterShouldResultInHumanisedPropertyNameAsParameterName() {
		String parameterName = unannotatedActionFactory.getInformation().getParameterInformation().get(0).getName();
		assert parameterName != null;
		assert parameterName.equals("description");
	}

	@Test(dependsOnMethods = "annotatedParameterShouldBeRecognised")
	public void annotatedParameterShouldResultInAnnotationAttributeAsParameterName() {
		String parameterName = annotatedActionFactory.getInformation().getParameterInformation().get(0).getName();
		assert parameterName != null;
		assert parameterName.equals("desc");
	}

	@Test(dependsOnMethods = {"unannotatedParameterShouldBeRecognised", "annotatedParameterShouldBeRecognised"})
	public void parameterTypeShouldBecomePropertyTypeByDefault() {
		Class<?> unannotatedParameterType =
			unannotatedActionFactory.getInformation().getParameterInformation().get(0).getType();
		assert unannotatedParameterType == String.class;

		Class<?> annotatedParameterType =
			annotatedActionFactory.getInformation().getParameterInformation().get(0).getType();
		assert annotatedParameterType == String.class;
	}

	@Test(dependsOnMethods = {
		"parameterOfTypeActionParameterShouldBeRecognised",
		"parameterTypeShouldBecomePropertyTypeByDefault"
	})
	public void annotatedParameterShouldResultInAnnotationAttributeAsParameterType() {
		assert parameterActionFactory.getInformation().getParameterInformation().get(0).getType() == String.class;
	}

	@Test(dependsOnMethods = "unannotatedParameterShouldBeRecognised")
	public void unannotatedParameterShouldBeOptional() {
		assert !unannotatedActionFactory.getInformation().getParameterInformation().get(0).isRequired();
	}

	@Test(dependsOnMethods = "annotatedParameterShouldBeRecognised")
	public void annotatedParameterShouldTakeBeingRequiredFromAnnotationAttribute() {
		assert annotatedActionFactory.getInformation().getParameterInformation().get(0).isRequired();
	}

	@Test(dependsOnMethods = "unannotatedParameterShouldBeOptional")
	public void omittingOptionalParametersShouldBePossible() {
		unannotatedActionFactory.create(Collections.<String, ActionParameter>emptyMap(), context);
	}

	@Test(expectedExceptions = ActionException.class,
		dependsOnMethods = "annotatedParameterShouldTakeBeingRequiredFromAnnotationAttribute")
	public void omittingRequiredParametersShouldCauseException() {
		annotatedActionFactory.create(Collections.<String, ActionParameter>emptyMap(), context);
	}

	@Test(dependsOnMethods = "unannotatedParameterShouldResultInHumanisedPropertyNameAsParameterName")
	public void supplyingOptionalParametersShouldCreateAction() {
		Map<String, ActionParameter> parameters =
			Collections.<String, ActionParameter>singletonMap("description", new TestActionParameter("Test action."));

		assert unannotatedActionFactory.create(parameters, context) instanceof TestAction;
	}

	@Test(dependsOnMethods = "annotatedParameterShouldResultInAnnotationAttributeAsParameterType")
	public void supplyingOptionalParametersOfTypeActionParameterShouldCreateAction() {
		Map<String, ActionParameter> parameters = new HashMap<String, ActionParameter>();
		parameters.put("description parameter", new TestActionParameter("Test action."));

		assert parameterActionFactory.create(parameters, context) instanceof ParameterAction;
	}

	@Test(dependsOnMethods = "annotatedParameterShouldResultInAnnotationAttributeAsParameterName")
	public void supplyingRequiredParametersWithAnnotatedNamesShouldCreateAction() {
		Map<String, ActionParameter> parameters =
			Collections.<String, ActionParameter>singletonMap("desc", new TestActionParameter("Test action."));

		assert annotatedActionFactory.create(parameters, context) instanceof AnnotatedAction;
	}

	@Test(expectedExceptions = ActionException.class)
	public void supplyingSuperfluousParametersShouldCauseException() {
		Map<String, ActionParameter> parameters =
			Collections.<String, ActionParameter>singletonMap("superfluous", new TestActionParameter("true"));

		unannotatedActionFactory.create(parameters, context);
	}

	public void superfluousParametersShouldBeSetAsDynamicParametersWhenActionIsDynamicallyParameterisable() {
		Map<String, ActionParameter> parameters =
			Collections.<String, ActionParameter>singletonMap("dynamic", new TestActionParameter("true"));

		Action action = dynamicallyParameterisableActionFactory.create(parameters, context);
		assert action instanceof DynamicallyParameterisableAction;

		Map<String, ActionParameter> dynamicParameters =
			((DynamicallyParameterisableAction) action).getDynamicParameters();
		assert dynamicParameters != null;
		assert dynamicParameters.size() == 1;
		assert dynamicParameters.containsKey("dynamic");

		Object parameterValue = dynamicParameters.get("dynamic").getValue(String.class, context);
		assert parameterValue instanceof String;
		assert Boolean.parseBoolean((String) parameterValue);
	}

	@Test(dependsOnMethods = {
		"unannotatedParameterShouldResultInHumanisedPropertyNameAsParameterName",
		"superfluousParametersShouldBeSetAsDynamicParametersWhenActionIsDynamicallyParameterisable"
	})
	public void regularParametersShouldBePreferredOverDynamicOnes() {
		Map<String, ActionParameter> parameters =
			Collections.<String, ActionParameter>singletonMap("regular parameter", new TestActionParameter("value"));

		Action action = dynamicallyParameterisableActionFactory.create(parameters, context);
		assert action instanceof DynamicallyParameterisableAction;

		DynamicallyParameterisableAction dynamicallyParameterisableAction = (DynamicallyParameterisableAction) action;

		Map<String, ActionParameter> dynamicParameters = dynamicallyParameterisableAction.getDynamicParameters();
		assert dynamicParameters != null;
		assert dynamicParameters.isEmpty();

		String regularParameter = dynamicallyParameterisableAction.getRegularParameter();
		assert regularParameter != null;
		assert regularParameter.equals("value");
	}
}