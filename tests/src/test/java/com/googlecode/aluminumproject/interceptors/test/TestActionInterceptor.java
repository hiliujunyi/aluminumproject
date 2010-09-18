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
package com.googlecode.aluminumproject.interceptors.test;

import com.googlecode.aluminumproject.interceptors.ActionInterceptor;
import com.googlecode.aluminumproject.interceptors.InterceptionException;
import com.googlecode.aluminumproject.templates.ActionContext;
import com.googlecode.aluminumproject.templates.ActionPhase;

import java.util.EnumSet;
import java.util.Set;

/**
 * An action interceptor that can be used in tests.
 *
 * @author levi_h
 */
public class TestActionInterceptor implements ActionInterceptor {
	private Set<ActionPhase> phases;

	/**
	 * Creates a test action interceptor that intercepts all possible action phases.
	 */
	public TestActionInterceptor() {
		this(ActionPhase.CONTRIBUTION, ActionPhase.CREATION, ActionPhase.EXECUTION);
	}

	/**
	 * Creates a test action interceptor that intercepts a number of action phases.
	 *
	 * @param phases the action phases to intercept
	 */
	public TestActionInterceptor(ActionPhase... phases) {
		this.phases = EnumSet.noneOf(ActionPhase.class);

		for (ActionPhase phase: phases) {
			this.phases.add(phase);
		}
	}

	public Set<ActionPhase> getPhases() {
		return phases;
	}

	public void intercept(ActionContext actionContext) throws InterceptionException {
		String actionName = actionContext.getActionFactory().getInformation().getName();
		String phase = actionContext.getPhase().name().toLowerCase();

		actionContext.getWriter().write(String.format("intercepted action '%s' during %s", actionName, phase));

		actionContext.proceed();
	}
}