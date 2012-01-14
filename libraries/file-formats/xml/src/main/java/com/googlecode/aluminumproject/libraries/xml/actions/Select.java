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
package com.googlecode.aluminumproject.libraries.xml.actions;

import com.googlecode.aluminumproject.AluminumException;
import com.googlecode.aluminumproject.annotations.Required;
import com.googlecode.aluminumproject.context.Context;
import com.googlecode.aluminumproject.libraries.actions.AbstractAction;
import com.googlecode.aluminumproject.libraries.xml.model.SelectionContext;
import com.googlecode.aluminumproject.writers.NullWriter;
import com.googlecode.aluminumproject.writers.Writer;

import java.util.List;

/**
 * Executes an <a href="http://www.w3.org/TR/xpath/">XPath</a> query and writes the results. The following node types
 * may be selected:
 * <ul>
 * <li>{@link com.googlecode.aluminumproject.libraries.xml.model.Element Elements};
 * <li>Namespaces;
 * <li>Attributes;
 * <li>Text;
 * <li>Comments.
 * </ul>
 * Except for elements, all results will have type {@link String string}.
 * <p>
 * When a query leads to a single result, only that result will be written. In any other case, the result will be
 * written as a {@link List list}.
 * <p>
 * XPath expressions may be given a context by using the context parameter or by nesting {@link Namespace namespace
 * actions}.
 *
 * @author levi_h
 */
public class Select extends AbstractAction implements SelectionContextContainer {
	private @Required com.googlecode.aluminumproject.libraries.xml.model.Element element;

	private @Required String expression;
	private SelectionContext context;

	/**
	 * Creates a <em>select</em> action.
	 */
	public Select() {
		context = new SelectionContext();
	}

	public SelectionContext getSelectionContext() {
		return context;
	}

	public void execute(Context context, Writer writer) throws AluminumException {
		getBody().invoke(context, new NullWriter());

		List<?> results = element.select(expression, this.context);

		writer.write((results.size() == 1) ? results.get(0) : results);
	}
}