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
package com.googlecode.aluminumproject.libraries.core.actions;

import com.googlecode.aluminumproject.annotations.ActionParameterInformation;
import com.googlecode.aluminumproject.annotations.Injected;
import com.googlecode.aluminumproject.configuration.Configuration;
import com.googlecode.aluminumproject.context.Context;
import com.googlecode.aluminumproject.context.ContextException;
import com.googlecode.aluminumproject.libraries.actions.AbstractAction;
import com.googlecode.aluminumproject.libraries.actions.ActionException;
import com.googlecode.aluminumproject.libraries.actions.ActionParameter;
import com.googlecode.aluminumproject.libraries.actions.DynamicallyParameterisable;
import com.googlecode.aluminumproject.templates.TemplateException;
import com.googlecode.aluminumproject.templates.TemplateProcessor;
import com.googlecode.aluminumproject.utilities.Utilities;
import com.googlecode.aluminumproject.writers.NullWriter;
import com.googlecode.aluminumproject.writers.Writer;
import com.googlecode.aluminumproject.writers.WriterException;

import java.util.HashMap;
import java.util.Map;

/**
 * Includes a template.
 * <p>
 * The action has two parameters: the name of the included template and the name of the parser that should be used for
 * it. The parser parameter may be omitted; in that case, the parser that is used for the template that contains the
 * include action will be used to parse the included template as well.
 * <p>
 * The include action supports dynamic parameters, all of which will be made available as variables in the included
 * template's context. It's also possible to pass blocks to included templates by using the {@link Block block} action;
 * these blocks can be used through the {@link BlockContents block contents} action.
 *
 * @author levi_h
 */
public class Include extends AbstractAction implements DynamicallyParameterisable {
	private String name;
	private String parser;

	private Map<String, ActionParameter> variables;

	private @Injected Configuration configuration;

	/**
	 * Creates an <em>include</em> action.
	 */
	public Include() {
		variables = new HashMap<String, ActionParameter>();
	}

	/**
	 * Sets the name of the template to include.
	 *
	 * @param name the template name to use
	 */
	@ActionParameterInformation(required = true)
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the name of the parser that will be used to parse the included template.
	 *
	 * @param parser the parser to use
	 */
	public void setParser(String parser) {
		this.parser = parser;
	}

	public void setParameter(String name, ActionParameter parameter) {
		variables.put(name, parameter);
	}

	public void execute(Context context, Writer writer) throws ActionException, ContextException, WriterException {
		String parser = this.parser;

		if (parser == null) {
			Map<String, Object> templateInformation =
				Utilities.typed(context.getImplicitObject(Context.ALUMINUM_IMPLICIT_OBJECT));

			parser = (String) templateInformation.get(TemplateProcessor.TEMPLATE_PARSER);
		}

		Context subcontext = context.createSubcontext();

		getBody().invoke(subcontext, new NullWriter());

		for (Map.Entry<String, ActionParameter> variable: variables.entrySet()) {
			String variableName = variable.getKey();
			Object variableValue = variable.getValue().getValue(Object.class, context);

			logger.debug("setting variable '", variableName, "' to ", variableValue);

			subcontext.setVariable(variableName, variableValue);
		}

		logger.debug("including template '", name, "' using parser '", parser, "'");

		try {
			new TemplateProcessor(configuration).processTemplate(name, parser, subcontext, writer);
		} catch (TemplateException exception) {
			throw new ActionException(exception, "can't include template '", name, "'");
		}
	}
}