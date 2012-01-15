/*
 * Copyright 2009-2012 Levi Hoogenberg
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
package com.googlecode.aluminumproject.libraries.core.actions;

import com.googlecode.aluminumproject.AluminumException;
import com.googlecode.aluminumproject.annotations.Required;
import com.googlecode.aluminumproject.context.Context;
import com.googlecode.aluminumproject.libraries.actions.AbstractAction;
import com.googlecode.aluminumproject.writers.Writer;

import java.util.List;

@SuppressWarnings("javadoc")
public class SetVariable extends AbstractAction {
	private String scope;
	private @Required String name;
	private Object value;

	public SetVariable() {
		value = NO_VALUE;
	}

	public void execute(Context context, Writer writer) throws AluminumException {
		if (value == NO_VALUE) {
			List<?> values = getBodyList(context, writer);

			if (values.isEmpty()) {
				throw new AluminumException("the 'set-variable' action needs a value, " +
					"either as a parameter or in its body");
			} else if (values.size() == 1) {
				value = values.get(0);
			} else {
				value = values;
			}
		}

		if (scope == null) {
			logger.debug("setting variable '", name, "' in innermost scope with value ", value);

			context.setVariable(name, value);
		} else {
			logger.debug("setting variable '", name, "' in scope '", scope, "' with value ", value);

			context.setVariable(scope, name, value);
		}
	}

	private final static Object NO_VALUE = new Object();
}