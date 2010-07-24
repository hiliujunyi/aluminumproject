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
package com.googlecode.aluminumproject.libraries;

import com.googlecode.aluminumproject.Logger;
import com.googlecode.aluminumproject.annotations.FunctionClass;
import com.googlecode.aluminumproject.annotations.Ignored;
import com.googlecode.aluminumproject.configuration.Configuration;
import com.googlecode.aluminumproject.configuration.ConfigurationException;
import com.googlecode.aluminumproject.configuration.ConfigurationParameters;
import com.googlecode.aluminumproject.libraries.actions.Action;
import com.googlecode.aluminumproject.libraries.actions.ActionContribution;
import com.googlecode.aluminumproject.libraries.actions.ActionContributionFactory;
import com.googlecode.aluminumproject.libraries.actions.ActionFactory;
import com.googlecode.aluminumproject.libraries.actions.DefaultActionContributionFactory;
import com.googlecode.aluminumproject.libraries.actions.DefaultActionFactory;
import com.googlecode.aluminumproject.libraries.common.actions.CallFunction;
import com.googlecode.aluminumproject.libraries.common.actions.CallFunctionFactory;
import com.googlecode.aluminumproject.libraries.common.actions.FunctionArgumentFactory;
import com.googlecode.aluminumproject.libraries.functions.Function;
import com.googlecode.aluminumproject.libraries.functions.FunctionFactory;
import com.googlecode.aluminumproject.libraries.functions.StaticMethodInvokingFunctionFactory;
import com.googlecode.aluminumproject.utilities.ReflectionUtilities;
import com.googlecode.aluminumproject.utilities.Utilities;
import com.googlecode.aluminumproject.utilities.UtilityException;
import com.googlecode.aluminumproject.utilities.finders.MethodFinder;
import com.googlecode.aluminumproject.utilities.finders.TypeFinder;
import com.googlecode.aluminumproject.utilities.finders.MethodFinder.MethodFilter;
import com.googlecode.aluminumproject.utilities.finders.TypeFinder.TypeFilter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * An abstract {@link Library library} implementation that implements the action factories, action contribution
 * factories, and function factories parts of the library contract. All subclasses have to provide is {@link
 * LibraryInformation information about the library} and the names of the packages in which {@link Action actions},
 * {@link ActionContribution action contributions}, and {@link Function functions} can be found. Additionally,
 * {@link #getDynamicActionFactory(String) the getDynamicActionFactory method} should be overridden if the library
 * supports dynamic actions.
 * <p>
 * The library will create {@link DefaultActionFactory default action factories} for each action and {@link
 * DefaultActionContributionFactory default action contribution factories} for each action contribution. Neither actions
 * nor action contributions that are annotated with {@link Ignored &#64;Ignored} will be included in the library.
 * <p>
 * To find functions, the library will scan for classes that are annotated with {@link FunctionClass
 * &#64;FunctionClass}; for every static method in those classes, a {@link StaticMethodInvokingFunctionFactory static
 * method invoking function factory} will be created. Static methods that are annotated with {@link Ignored
 * &#64;Ignored} won't be added to the library.
 * <p>
 * A number of common actions are added to abstract libraries in the {@link #addCommonActions() addCommonActions
 * method}; (abstract) subclasses are free to override this method and add more common actions.
 *
 * @author levi_h
 */
public abstract class AbstractLibrary implements Library {
	private String[] packageNames;

	private Configuration configuration;
	private ConfigurationParameters parameters;

	private List<ActionFactory> actionFactories;
	private List<ActionContributionFactory> actionContributionFactories;
	private List<FunctionFactory> functionFactories;

	/** The logger to use. */
	protected final Logger logger;

	/**
	 * Creates an abstract library.
	 *
	 * @param packageNames the package names to find actions in
	 */
	protected AbstractLibrary(String... packageNames) {
		this.packageNames = packageNames;

		logger = Logger.get(getClass());
	}

	public void initialise(
			Configuration configuration, ConfigurationParameters parameters) throws ConfigurationException {
		this.configuration = configuration;
		this.parameters = parameters;

		actionFactories = new LinkedList<ActionFactory>();
		actionContributionFactories = new LinkedList<ActionContributionFactory>();
		functionFactories = new LinkedList<FunctionFactory>();

		logger.debug("finding actions, action contributions, and functions in packages '", packageNames, "'");

		findActions();
		addCommonActions();
		findActionContributions();
		findFunctions();
	}

	private void findActions() throws ConfigurationException {
		List<Class<? extends Action>> actionClasses;

		try {
			actionClasses = Utilities.typed(TypeFinder.find(new TypeFilter() {
				public boolean accepts(Class<?> type) {
					return Action.class.isAssignableFrom(type) && !ReflectionUtilities.isAbstract(type)
						&& !type.isAnnotationPresent(Ignored.class);
				}
			}, packageNames));
		} catch (UtilityException exception) {
			throw new ConfigurationException(exception, "can't find actions");
		}

		for (Class<? extends Action> actionClass: actionClasses) {
			logger.debug("creating default action factory for action class ", actionClass.getName());

			addActionFactory(new DefaultActionFactory(actionClass));
		}
	}

	/**
	 * Adds the common actions:
	 * <ul>
	 * <li>An action to {@link CallFunction call a function}.
	 * </ul>
	 *
	 * @throws ConfigurationException when one of the common actions can't be configured
	 */
	protected void addCommonActions() throws ConfigurationException {
		addActionFactory(new CallFunctionFactory());
		addActionFactory(new FunctionArgumentFactory());
	}

	/**
	 * Adds an action factory to this library. It will be initialised and its library will be set.
	 *
	 * @param actionFactory the action factory to add
	 * @throws ConfigurationException when the action factory can't be initialised
	 */
	protected void addActionFactory(ActionFactory actionFactory) throws ConfigurationException {
		initialiseLibraryElement(actionFactory);

		actionFactories.add(actionFactory);
	}

	private void findActionContributions() throws ConfigurationException {
		List<Class<? extends ActionContribution>> actionContributionClasses;

		try {
			actionContributionClasses = Utilities.typed(TypeFinder.find(new TypeFilter() {
				public boolean accepts(Class<?> type) {
					return ActionContribution.class.isAssignableFrom(type) && !ReflectionUtilities.isAbstract(type)
						&& !type.isAnnotationPresent(Ignored.class);
				}
			}, packageNames));
		} catch (UtilityException exception) {
			throw new ConfigurationException(exception, "can't find action contributions");
		}

		for (Class<? extends ActionContribution> actionContributionClass: actionContributionClasses) {
			logger.debug("creating default action contribution factory ",
				"for action contribution class ", actionContributionClass.getName());

			addActionContributionFactory(new DefaultActionContributionFactory(actionContributionClass));
		}
	}

	/**
	 * Adds an action contribution factory to this library.
	 *
	 * @param actionContributionFactory the action contribution factory to add
	 */
	protected void addActionContributionFactory(ActionContributionFactory actionContributionFactory) {
		initialiseLibraryElement(actionContributionFactory);

		actionContributionFactories.add(actionContributionFactory);
	}

	private void findFunctions() throws ConfigurationException {
		List<Class<?>> functionClasses;

		try {
			functionClasses = Utilities.typed(TypeFinder.find(new TypeFilter() {
				public boolean accepts(Class<?> type) {
					return type.isAnnotationPresent(FunctionClass.class) && !ReflectionUtilities.isAbstract(type);
				}
			}, packageNames));
		} catch (UtilityException exception) {
			throw new ConfigurationException(exception, "can't find function classes");
		}

		for (Class<?> functionClass: functionClasses) {
			List<Method> methods = Utilities.typed(MethodFinder.find(new MethodFilter() {
				public boolean accepts(Method method) {
					int modifiers = method.getModifiers();

					return Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)
						&& !method.isAnnotationPresent(Ignored.class);
				}
			}, functionClass));

			for (Method method: methods) {
				logger.debug("creating static method invoking function factory for method ",
					method.getDeclaringClass().getName(), "#", method.getName());

				addFunctionFactory(new StaticMethodInvokingFunctionFactory(method));
			}
		}
	}

	/**
	 * Adds a function factory to this library. It will be initialised and its library will be set.
	 *
	 * @param functionFactory the function factory to add
	 * @throws ConfigurationException when the function factory can't be initialised
	 */
	protected void addFunctionFactory(FunctionFactory functionFactory) throws ConfigurationException {
		initialiseLibraryElement(functionFactory);

		functionFactories.add(functionFactory);
	}

	/**
	 * Initialises a library element by providing it with both the configuration and this library.
	 *
	 * @param libraryElement the library element to initialise
	 */
	protected void initialiseLibraryElement(LibraryElement libraryElement) {
		libraryElement.initialise(configuration, parameters);
		libraryElement.setLibrary(this);
	}

	public List<ActionFactory> getActionFactories() {
		return Collections.unmodifiableList(actionFactories);
	}

	public ActionFactory getDynamicActionFactory(String name) throws LibraryException {
		LibraryInformation information = getInformation();

		if (information.supportsDynamicActions()) {
			throw new LibraryException("dynamic action factories can't be created by the abstract library");
		} else {
			throw new LibraryException("library '", information.getUrl(), "' does not support dynamic actions");
		}
	}

	public ActionContributionFactory getDynamicActionContributionFactory(String name) throws LibraryException {
		LibraryInformation information = getInformation();

		if (information.supportsDynamicActionContributions()) {
			throw new LibraryException(
				"dynamic action contribution factories can't be created by the abstract library");
		} else {
			throw new LibraryException(
				"library '", information.getUrl(), "' does not support dynamic action contributions");
		}
	}

	public FunctionFactory getDynamicFunctionFactory(String name) throws LibraryException {
		LibraryInformation information = getInformation();

		if (information.supportsDynamicFunctions()) {
			throw new LibraryException("dynamic function factories can't be created by the abstract library");
		} else {
			throw new LibraryException("library '", information.getUrl(), "' does not support dynamic functions");
		}
	}

	public List<ActionContributionFactory> getActionContributionFactories() {
		return Collections.unmodifiableList(actionContributionFactories);
	}

	public List<FunctionFactory> getFunctionFactories() {
		return Collections.unmodifiableList(functionFactories);
	}
}