/*
 * Copyright 2010-2013 Aluminum project
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
import com.googlecode.aluminumproject.context.g11n.DateFormatProvider;
import com.googlecode.aluminumproject.context.g11n.DateFormatType;
import com.googlecode.aluminumproject.context.g11n.GlobalisationContext;
import com.googlecode.aluminumproject.context.g11n.NumberFormatProvider;
import com.googlecode.aluminumproject.context.g11n.NumberFormatType;
import com.googlecode.aluminumproject.interceptors.AbstractActionInterceptor;
import com.googlecode.aluminumproject.libraries.actions.AbstractActionContribution;
import com.googlecode.aluminumproject.libraries.actions.Action;
import com.googlecode.aluminumproject.libraries.actions.ActionContributionOptions;
import com.googlecode.aluminumproject.libraries.actions.ActionFactory;
import com.googlecode.aluminumproject.libraries.actions.ActionParameter;
import com.googlecode.aluminumproject.libraries.actions.ClassBasedActionFactory;
import com.googlecode.aluminumproject.templates.ActionContext;
import com.googlecode.aluminumproject.templates.ActionPhase;
import com.googlecode.aluminumproject.writers.Writer;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

@SuppressWarnings("javadoc")
@Typed("String")
public class WithCustomPattern extends AbstractActionContribution {
	public boolean canBeMadeTo(ActionFactory actionFactory) {
		boolean canBeMade;

		if (canBeMade = (actionFactory instanceof ClassBasedActionFactory)) {
			Class<? extends Action> actionClass = ((ClassBasedActionFactory) actionFactory).getActionClass();

			canBeMade &= FormatDate.class.isAssignableFrom(actionClass) ||
				FormatNumber.class.isAssignableFrom(actionClass);
		}

		return canBeMade;
	}

	public void make(Context context, Writer writer,
			final ActionParameter parameter, ActionContributionOptions options) throws AluminumException {
		options.addInterceptor(new AbstractActionInterceptor(ActionPhase.EXECUTION) {
			public void intercept(ActionContext actionContext) throws AluminumException {
				Context context = actionContext.getContext();
				GlobalisationContext globalisationContext = GlobalisationContext.from(context);

				DateFormatProvider dateFormatProvider = globalisationContext.getDateFormatProvider();
				NumberFormatProvider numberFormatProvider = globalisationContext.getNumberFormatProvider();

				try {
					String customPattern = (String) parameter.getValue(String.class, context);

					if (actionContext.getAction() instanceof FormatDate) {
						globalisationContext.setDateFormatProvider(
							new CustomDateFormatProvider(customPattern, dateFormatProvider));
					} else if (actionContext.getAction() instanceof FormatNumber) {
						globalisationContext.setNumberFormatProvider(
							new CustomNumberFormatProvider(customPattern, numberFormatProvider));
					}

					actionContext.proceed();
				} finally {
					globalisationContext.setDateFormatProvider(dateFormatProvider);
					globalisationContext.setNumberFormatProvider(numberFormatProvider);
				}
			}
		});
	}

	private static class CustomDateFormatProvider implements DateFormatProvider {
		private String customPattern;

		private DateFormatProvider delegate;

		public CustomDateFormatProvider(String customPattern, DateFormatProvider delegate) {
			this.customPattern = customPattern;

			this.delegate = delegate;
		}

		public DateFormat provide(DateFormatType type, Context context) throws AluminumException {
			DateFormat dateFormat;

			if (type == DateFormatType.CUSTOM) {
				GlobalisationContext globalisationContext = GlobalisationContext.from(context);

				dateFormat = new SimpleDateFormat(customPattern, globalisationContext.getLocaleProvider().provide(context));
				dateFormat.setTimeZone(globalisationContext.getTimeZoneProvider().provide(context));
			} else {
				dateFormat = delegate.provide(type, context);
			}

			return dateFormat;
		}
	}

	private static class CustomNumberFormatProvider implements NumberFormatProvider {
		private String customPattern;

		private NumberFormatProvider delegate;

		public CustomNumberFormatProvider(String customPattern, NumberFormatProvider delegate) {
			this.customPattern = customPattern;

			this.delegate = delegate;
		}

		public NumberFormat provide(NumberFormatType type, Context context) throws AluminumException {
			NumberFormat numberFormat;

			if (type == NumberFormatType.CUSTOM) {
				try {
					Locale locale = GlobalisationContext.from(context).getLocaleProvider().provide(context);

					numberFormat = new DecimalFormat(customPattern, new DecimalFormatSymbols(locale));
				} catch (IllegalArgumentException exception) {
					throw new AluminumException(exception, "can't create custom number format");
				}
			} else {
				numberFormat = delegate.provide(type, context);
			}

			return numberFormat;
		}
	}
}