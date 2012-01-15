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
package com.googlecode.aluminumproject.libraries.g11n.actions;

import com.googlecode.aluminumproject.AluminumException;
import com.googlecode.aluminumproject.annotations.Typed;
import com.googlecode.aluminumproject.context.Context;
import com.googlecode.aluminumproject.context.g11n.GlobalisationContext;
import com.googlecode.aluminumproject.context.g11n.NameBasedResourceBundleProvider;
import com.googlecode.aluminumproject.context.g11n.ResourceBundleProvider;
import com.googlecode.aluminumproject.interceptors.AbstractActionInterceptor;
import com.googlecode.aluminumproject.libraries.actions.ActionContribution;
import com.googlecode.aluminumproject.libraries.actions.ActionContributionOptions;
import com.googlecode.aluminumproject.libraries.actions.ActionFactory;
import com.googlecode.aluminumproject.libraries.actions.ActionParameter;
import com.googlecode.aluminumproject.templates.ActionContext;
import com.googlecode.aluminumproject.templates.ActionPhase;
import com.googlecode.aluminumproject.writers.Writer;

@SuppressWarnings("javadoc")
@Typed("String")
public class WithResourceBundle implements ActionContribution {
	public boolean canBeMadeTo(ActionFactory actionFactory) {
		return true;
	}

	public void make(Context context, Writer writer,
			final ActionParameter parameter, ActionContributionOptions options) throws AluminumException {
		options.addInterceptor(new AbstractActionInterceptor(ActionPhase.CREATION, ActionPhase.EXECUTION) {
			private ResourceBundleProvider resourceBundleProvider;

			public void intercept(ActionContext actionContext) throws AluminumException {
				Context context = actionContext.getContext();
				GlobalisationContext globalisationContext = GlobalisationContext.from(context);

				if (actionContext.getPhase() == ActionPhase.CREATION) {
					resourceBundleProvider = globalisationContext.getResourceBundleProvider();

					globalisationContext.setResourceBundleProvider(
						new NameBasedResourceBundleProvider(((String) parameter.getValue(String.class, context))));

					actionContext.proceed();
				} else {
					try {
						actionContext.proceed();
					} finally {
						globalisationContext.setResourceBundleProvider(resourceBundleProvider);
					}
				}
			}
		});
	}
}