/*
 * Copyright 2010-2014 Aluminum project
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
package com.googlecode.aluminumproject.libraries.xml.actions;

import com.googlecode.aluminumproject.AluminumException;
import com.googlecode.aluminumproject.annotations.Required;
import com.googlecode.aluminumproject.annotations.UsableAsFunction;
import com.googlecode.aluminumproject.context.Context;
import com.googlecode.aluminumproject.libraries.actions.AbstractAction;
import com.googlecode.aluminumproject.libraries.xml.model.SelectionContext;
import com.googlecode.aluminumproject.writers.NullWriter;
import com.googlecode.aluminumproject.writers.Writer;

import java.util.List;

@SuppressWarnings("javadoc")
@UsableAsFunction(argumentParameters = {"element", "expression"})
public class Select extends AbstractAction implements SelectionContextContainer {
	private @Required com.googlecode.aluminumproject.libraries.xml.model.Element element;

	private @Required String expression;
	private SelectionContext context;

	private Type type;

	public Select() {
		context = new SelectionContext();

		type = Type.AUTOMATIC;
	}

	public SelectionContext getSelectionContext() {
		return context;
	}

	public void execute(Context context, Writer writer) throws AluminumException {
		getBody().invoke(context, new NullWriter());

		List<?> results = element.select(expression, this.context);

		Object result;

		switch (type) {
			case SINGLE:
				int resultCount = results.size();

				if (resultCount == 1) {
					result = results.get(0);
				} else {
					throw new AluminumException("expected single result, got ", resultCount);
				}

				break;

			case MULTIPLE:
				result = results;

				break;

			case AUTOMATIC:
			default:
				result = (results.size() == 1) ? results.get(0) : results.isEmpty() ? null : results;

				break;
		}

		writer.write(result);
	}

	public static enum Type {
		AUTOMATIC,
		SINGLE,
		MULTIPLE
	}
}