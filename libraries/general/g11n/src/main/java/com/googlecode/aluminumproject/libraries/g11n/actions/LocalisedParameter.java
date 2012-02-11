/*
 * Copyright 2010-2012 Aluminum project
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
package com.googlecode.aluminumproject.libraries.g11n.actions;

import com.googlecode.aluminumproject.AluminumException;
import com.googlecode.aluminumproject.annotations.Injected;
import com.googlecode.aluminumproject.configuration.Configuration;
import com.googlecode.aluminumproject.context.Context;
import com.googlecode.aluminumproject.context.g11n.GlobalisationContext;
import com.googlecode.aluminumproject.converters.ConverterRegistry;
import com.googlecode.aluminumproject.libraries.actions.AbstractActionContribution;
import com.googlecode.aluminumproject.libraries.actions.ActionContributionOptions;
import com.googlecode.aluminumproject.libraries.actions.ActionParameter;
import com.googlecode.aluminumproject.templates.ActionContributionDescriptor;
import com.googlecode.aluminumproject.utilities.Logger;
import com.googlecode.aluminumproject.writers.Writer;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.ResourceBundle;

@SuppressWarnings("javadoc")
public class LocalisedParameter extends AbstractActionContribution {
	private @Injected ActionContributionDescriptor descriptor;

	private @Injected Configuration configuration;

	private final Logger logger;

	public LocalisedParameter() {
		logger = Logger.get(getClass());
	}

	public void make(Context context, Writer writer,
			ActionParameter parameter, ActionContributionOptions options) throws AluminumException {
		String name = descriptor.getName();

		String key = (String) parameter.getValue(String.class, context);

		logger.debug("adding localised action parameter '", name, "' with key '", key, "'");

		options.setParameter(name, new LocalisedActionParameter(key, configuration.getConverterRegistry()));
	}

	private static class LocalisedActionParameter implements ActionParameter {
		private String key;

		private ConverterRegistry converterRegistry;

		public LocalisedActionParameter(String key, ConverterRegistry converterRegistry) {
			this.key = key;

			this.converterRegistry = converterRegistry;
		}

		public String getText() {
			return key;
		}

		public Object getValue(Type type, Context context) throws AluminumException {
			ResourceBundle resourceBundle =
				GlobalisationContext.from(context).getResourceBundleProvider().provide(context);

			if (Collections.list(resourceBundle.getKeys()).contains(key)) {
				return converterRegistry.convert(resourceBundle.getObject(key), type);
			} else {
				throw new AluminumException("can't find localised resource with key '", key, "'");
			}
		}
	}
}