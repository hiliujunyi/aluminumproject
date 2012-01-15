/*
 * Copyright 2010-2012 Levi Hoogenberg
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
package com.googlecode.aluminumproject.interceptors;

import com.googlecode.aluminumproject.AluminumException;
import com.googlecode.aluminumproject.templates.ActionPhase;
import com.googlecode.aluminumproject.utilities.Logger;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Simplifies implementing {@link ActionInterceptor the ActionInterceptor interface} by accepting the intercepted
 * phases that subclasses pass to the constructor.
 */
public abstract class AbstractActionInterceptor implements ActionInterceptor {
	private Set<ActionPhase> phases;

	/** The logger to use. */
	protected final Logger logger;

	/**
	 * Creates an abstract action interceptor.
	 *
	 * @param phases the action phases to intercept
	 * @throws AluminumException when no action phases are specified
	 */
	protected AbstractActionInterceptor(ActionPhase... phases) throws AluminumException {
		if (phases.length == 0) {
			throw new AluminumException("action interceptors should intercept at least one action phase");
		} else {
			this.phases = EnumSet.copyOf(Arrays.asList(phases));
		}

		logger = Logger.get(getClass());
	}

	public Set<ActionPhase> getPhases() {
		return Collections.unmodifiableSet(phases);
	}
}