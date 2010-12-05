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

import com.googlecode.aluminumproject.annotations.Injected;
import com.googlecode.aluminumproject.annotations.Named;
import com.googlecode.aluminumproject.annotations.Typed;
import com.googlecode.aluminumproject.configuration.Configuration;
import com.googlecode.aluminumproject.configuration.ConfigurationElementFactory;
import com.googlecode.aluminumproject.configuration.ConfigurationException;
import com.googlecode.aluminumproject.libraries.AbstractLibraryElement;
import com.googlecode.aluminumproject.libraries.LibraryException;
import com.googlecode.aluminumproject.utilities.GenericsUtilities;
import com.googlecode.aluminumproject.utilities.StringUtilities;
import com.googlecode.aluminumproject.utilities.UtilityException;

import java.lang.reflect.Type;

/**
 * An {@link ActionContributionFactory action contribution factory} that's based on a {@link ActionContribution action
 * contribution} class and examines it to retrieve information about it. Normally, reflection is used to obtain the
 * action contribution information, though this can be influenced by annotating the action contribution class.
 * <p>
 * When an action contribution class is annotated with {@link Named &#64;Named}, the annotation value will become the
 * name of the action contribution. For unannotated action contribution classes, the lower cased {@link
 * StringUtilities#humanise(String) humanised} class name will become the name (this means the the action contribution
 * {@code OnDays} would get the name <em>on days</em>).
 * <p>
 * Similarly, the parameter type of annotated action contributions will be inferred from the {@link Typed &#64;Typed}
 * annotation. When no such annotation is present on the action contribution's class, {@link Object} will be used as its
 * parameter type.
 * <p>
 * The action contribution will be created by the {@link ConfigurationElementFactory configuration element factory}
 * in the configuration. After its creation, any of the action contribution's fields that are declared to be {@link
 * Injected injected} are filled.
 *
 * @author levi_h
 */
public class DefaultActionContributionFactory extends AbstractLibraryElement implements ActionContributionFactory {
	private Class<? extends ActionContribution> actionContributionClass;

	private ActionContributionInformation information;

	/**
	 * Creates a default action contribution factory.
	 *
	 * @param actionContributionClass the class of the action contribution to create
	 */
	public DefaultActionContributionFactory(Class<? extends ActionContribution> actionContributionClass) {
		this.actionContributionClass = actionContributionClass;
	}

	@Override
	public void initialise(Configuration configuration) throws ConfigurationException {
		super.initialise(configuration);

		String name;

		if (actionContributionClass.isAnnotationPresent(Named.class)) {
			name = actionContributionClass.getAnnotation(Named.class).value();
		} else {
			name = StringUtilities.humanise(actionContributionClass.getSimpleName()).toLowerCase();
		}

		Type parameterType;

		if (actionContributionClass.isAnnotationPresent(Typed.class)) {
			try {
				String parameterTypeName = actionContributionClass.getAnnotation(Typed.class).value();

				parameterType = GenericsUtilities.getType(parameterTypeName, "java.lang", "java.util");
			} catch (UtilityException exception) {
				throw new ConfigurationException(exception,
					"can't get parameter type for action contribution class ", actionContributionClass.getName());
			}
		} else {
			parameterType = Object.class;
		}

		logger.debug("using name '", name, "' and parameter type ", parameterType,
			" for action contribution class ", actionContributionClass.getName());

		information = new ActionContributionInformation(name, parameterType);
	}

	public ActionContributionInformation getInformation() {
		return information;
	}

	public ActionContribution create() throws ActionException {
		ActionContribution actionContribution;

		try {
			ConfigurationElementFactory configurationElementFactory =
				getConfiguration().getConfigurationElementFactory();

			actionContribution =
				configurationElementFactory.instantiate(actionContributionClass.getName(), ActionContribution.class);
		} catch (ConfigurationException exception) {
			throw new ActionException(exception, "can't create action contribution");
		}

		logger.debug("created action contribution ", actionContribution);

		try {
			injectFields(actionContribution);
		} catch (LibraryException exception) {
			throw new ActionException(exception, "can't inject fields of action contribution");
		}

		logger.debug("injected fields");

		return actionContribution;
	}
}