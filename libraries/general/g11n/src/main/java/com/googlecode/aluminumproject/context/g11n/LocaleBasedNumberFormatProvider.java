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
package com.googlecode.aluminumproject.context.g11n;

import com.googlecode.aluminumproject.AluminumException;
import com.googlecode.aluminumproject.context.Context;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Provides number formats that are suitable for the {@link GlobalisationContext globalisation context}'s current {@link
 * Locale locale}.
 */
public class LocaleBasedNumberFormatProvider implements NumberFormatProvider {
	private String customPattern;

	/**
	 * Creates a locale-based number format provider.
	 *
	 * @param customPattern the pattern to use for the custom number format
	 */
	public LocaleBasedNumberFormatProvider(String customPattern) {
		this.customPattern = customPattern;
	}

	public NumberFormat provide(NumberFormatType type, Context context) throws AluminumException {
		NumberFormat numberFormat;

		Locale locale = GlobalisationContext.from(context).getLocaleProvider().provide(context);

		if (type == NumberFormatType.NUMBER) {
			numberFormat = NumberFormat.getNumberInstance(locale);
		} else if (type == NumberFormatType.CURRENCY) {
			numberFormat = NumberFormat.getCurrencyInstance(locale);
		} else if (type == NumberFormatType.PERCENTAGE) {
			numberFormat = NumberFormat.getPercentInstance(locale);
		} else if (type == NumberFormatType.CUSTOM) {
			try {
				numberFormat = new DecimalFormat(customPattern, new DecimalFormatSymbols(locale));
			} catch (IllegalArgumentException exception) {
				throw new AluminumException(exception,
					"can't create custom number format with pattern '", customPattern, "'");
			}
		} else {
			throw new AluminumException("unsupported number format type: ", type);
		}

		return numberFormat;
	}
}