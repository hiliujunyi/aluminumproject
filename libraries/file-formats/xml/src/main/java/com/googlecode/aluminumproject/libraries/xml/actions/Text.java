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
package com.googlecode.aluminumproject.libraries.xml.actions;

import com.googlecode.aluminumproject.AluminumException;
import com.googlecode.aluminumproject.annotations.Typed;
import com.googlecode.aluminumproject.context.Context;
import com.googlecode.aluminumproject.interceptors.AbstractActionInterceptor;
import com.googlecode.aluminumproject.libraries.AbstractLibraryElementCreation;
import com.googlecode.aluminumproject.libraries.actions.ActionContribution;
import com.googlecode.aluminumproject.libraries.actions.ActionContributionFactory;
import com.googlecode.aluminumproject.libraries.actions.ActionContributionOptions;
import com.googlecode.aluminumproject.libraries.actions.ActionFactory;
import com.googlecode.aluminumproject.libraries.actions.ActionParameter;
import com.googlecode.aluminumproject.libraries.actions.DefaultActionFactory;
import com.googlecode.aluminumproject.templates.ActionContext;
import com.googlecode.aluminumproject.templates.ActionPhase;
import com.googlecode.aluminumproject.writers.Writer;

@SuppressWarnings("javadoc")
@Typed("String")
public class Text extends AbstractLibraryElementCreation<ActionContributionFactory> implements ActionContribution {
	public boolean canBeMadeTo(ActionFactory actionFactory) {
		return (actionFactory instanceof DefaultActionFactory) &&
			AbstractElement.class.isAssignableFrom(((DefaultActionFactory) actionFactory).getActionClass());
	}

	public void make(Context context, Writer writer,
			ActionParameter parameter, ActionContributionOptions options) throws AluminumException {
		String text = (String) parameter.getValue(String.class, context);

		options.addInterceptor(new TextSetter(text));
	}

	private static class TextSetter extends AbstractActionInterceptor {
		private String text;

		public TextSetter(String text) {
			super(ActionPhase.CREATION);

			this.text = text;
		}

		public void intercept(ActionContext actionContext) throws AluminumException {
			actionContext.proceed();

			((AbstractElement) actionContext.getAction()).setText(text);
		}
	}
}