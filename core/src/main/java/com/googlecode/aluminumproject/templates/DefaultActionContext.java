/*
 * Copyright 2009-2012 Aluminum project
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
package com.googlecode.aluminumproject.templates;

import com.googlecode.aluminumproject.AluminumException;
import com.googlecode.aluminumproject.configuration.Configuration;
import com.googlecode.aluminumproject.context.Context;
import com.googlecode.aluminumproject.interceptors.ActionInterceptor;
import com.googlecode.aluminumproject.libraries.actions.Action;
import com.googlecode.aluminumproject.libraries.actions.ActionContributionFactory;
import com.googlecode.aluminumproject.libraries.actions.ActionFactory;
import com.googlecode.aluminumproject.libraries.actions.ActionParameter;
import com.googlecode.aluminumproject.utilities.Logger;
import com.googlecode.aluminumproject.writers.Writer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@link ActionContext action context} implementation that is used by the {@link DefaultActionElement default
 * action element}.
 */
public class DefaultActionContext implements ActionContext {
	private Configuration configuration;

	private ActionDescriptor actionDescriptor;
	private ActionFactory actionFactory;

	private Map<String, ActionParameter> parameters;
	private Map<ActionContributionDescriptor, ActionContributionFactory> actionContributionFactories;

	private Context context;
	private Writer writer;

	private Action action;

	private Map<ActionPhase, List<ActionInterceptor>> interceptors;

	private ActionPhase phase;
	private int indexOfNextInterceptor;

	private final Logger logger;

	/**
	 * Creates a default action context.
	 *
	 * @param configuration the current configuration
	 * @param actionDescriptor the descriptor of the action
	 * @param actionFactory the action factory that will create the action
	 * @param context the context that the action will use
	 * @param writer the writer that the writer will use
	 */
	public DefaultActionContext(Configuration configuration,
			ActionDescriptor actionDescriptor, ActionFactory actionFactory, Context context, Writer writer) {
		this.configuration = configuration;

		this.actionDescriptor = actionDescriptor;
		this.actionFactory = actionFactory;

		parameters = new HashMap<String, ActionParameter>();
		actionContributionFactories = new LinkedHashMap<ActionContributionDescriptor, ActionContributionFactory>();

		this.context = context;
		this.writer = writer;

		interceptors = new EnumMap<ActionPhase, List<ActionInterceptor>>(ActionPhase.class);

		for (ActionPhase phase: ActionPhase.values()) {
			interceptors.put(phase, new ArrayList<ActionInterceptor>());
		}

		logger = Logger.get(getClass());
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public ActionDescriptor getActionDescriptor() {
		return actionDescriptor;
	}

	public ActionFactory getActionFactory() {
		return actionFactory;
	}

	public Map<String, ActionParameter> getParameters() {
		return Collections.unmodifiableMap(parameters);
	}

	public void addParameter(String name, ActionParameter parameter) throws AluminumException {
		if (action != null) {
			throw new AluminumException("can't add parameter '", name, "': an action has already been created");
		}

		parameters.put(name, parameter);
	}

	public Map<ActionContributionDescriptor, ActionContributionFactory> getActionContributionFactories() {
		return Collections.unmodifiableMap(actionContributionFactories);
	}

	public void addActionContribution(ActionContributionDescriptor descriptor,
			ActionContributionFactory contributionFactory) throws AluminumException {
		if (EnumSet.of(ActionPhase.CREATION, ActionPhase.EXECUTION).contains(phase)) {
			throw new AluminumException("can't add action contribution: all contributions have been made");
		}

		actionContributionFactories.put(descriptor, contributionFactory);
	}

	public Context getContext() {
		return context;
	}

	public Writer getWriter() {
		return writer;
	}

	public void setWriter(Writer writer) {
		this.writer = writer;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) throws AluminumException {
		if (this.action != null) {
			throw new AluminumException("the action context already contains an action");
		}

		this.action = action;
	}

	/**
	 * Returns all action interceptors that should run for a certain phase.
	 *
	 * @param phase the intercepted phase
	 * @return the interceptors that intercept the given phase
	 */
	public List<ActionInterceptor> getInterceptors(ActionPhase phase) {
		return interceptors.get(phase);
	}

	public void addInterceptor(ActionInterceptor interceptor) throws AluminumException {
		for (ActionPhase phase: interceptor.getPhases()) {
			if ((this.phase != null) && (this.phase.compareTo(phase) > 0)) {
				throw new AluminumException("can't add interceptor for past phase ", phase);
			}

			logger.debug("adding interceptor ", interceptor, " for phase ", phase);

			interceptors.get(phase).add(0, interceptor);
		}
	}

	public ActionPhase getPhase() {
		return phase;
	}

	/**
	 * Sets the current phase.
	 *
	 * @param phase the current interception phase
	 */
	public void setPhase(ActionPhase phase) {
		logger.debug("next phase: ", phase);

		this.phase = phase;
		indexOfNextInterceptor = 0;
	}

	public void proceed() throws AluminumException {
		List<ActionInterceptor> interceptorsForCurrentPhase = getInterceptors(phase);

		if (indexOfNextInterceptor < interceptorsForCurrentPhase.size()) {
			ActionInterceptor interceptor = interceptorsForCurrentPhase.get(indexOfNextInterceptor++);

			logger.debug("running interceptor ", interceptor);

			interceptor.intercept(this);
		}
	}
}