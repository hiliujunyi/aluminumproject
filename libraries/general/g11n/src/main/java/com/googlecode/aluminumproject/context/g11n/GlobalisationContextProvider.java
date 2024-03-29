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

import static com.googlecode.aluminumproject.context.g11n.GlobalisationContext.GLOBALISATION_CONTEXT;

import com.googlecode.aluminumproject.AluminumException;
import com.googlecode.aluminumproject.configuration.Configuration;
import com.googlecode.aluminumproject.configuration.ConfigurationElementFactory;
import com.googlecode.aluminumproject.configuration.ConfigurationParameters;
import com.googlecode.aluminumproject.context.Context;
import com.googlecode.aluminumproject.context.ContextEnricher;
import com.googlecode.aluminumproject.utilities.GlobalisationUtilities;
import com.googlecode.aluminumproject.utilities.Logger;

import java.util.Locale;
import java.util.TimeZone;

/**
 * Enriches the context with a {@link GlobalisationContext globalisation context}. Before each template, a globalisation
 * context is made available as an implicit object. It can be obtained by using a {@link
 * GlobalisationContext#from(Context) utility method}. After the template has been processed, the implicit object is
 * removed again.
 * <p>
 * When a context has a parent context that contains a globalisation context, it is inherited by the subcontext. In any
 * other case, a new globalisation context is created. The following paragraphs describe the elements of a new
 * globalisation context.
 * <p>
 * The type of the locale provider that the globalisation context is created with can be configured with the {@value
 * #LOCALE_PROVIDER_CLASS} parameter. By default, a {@link ConstantLocaleProvider constant locale provider} will be
 * used with either the value of the configuration parameter {@value #LOCALE} or the {@link Locale#getDefault() default
 * locale}.
 * <p>
 * The time zone provider contained by the globalisation context defaults to a {@link ConstantTimeZoneProvider constant
 * one} that uses the {@link TimeZone#getDefault() default time zone}. Both the constant time zone and the time zone
 * provider can be configured (using the {@value #TIME_ZONE} and {@value #TIME_ZONE_PROVIDER_CLASS} parameters,
 * respectively).
 * <p>
 * The globalisation context's resource bundle provider will be {@link NameBasedResourceBundleProvider name-based},
 * unless a different one is configured via a configuration parameter named {@value #RESOURCE_BUNDLE_PROVIDER_CLASS}.
 * The base name of the default resource bundle provider can be configured by using the {@value
 * #RESOURCE_BUNDLE_BASE_NAME} parameter; by default, {@value #DEFAULT_RESOURCE_BUNDLE_BASE_NAME} will be used.
 * <p>
 * The date format provider that will be used for the globalisation context can be configured by providing a parameter
 * named {@value #DATE_FORMAT_PROVIDER_CLASS}. If none is configured, the date format provider will be {@link
 * EnvironmentBasedDateFormatProvider based on the current locale and time zone}. Its custom pattern will be {@value
 * #DEFAULT_DATE_FORMAT_CUSTOM_PATTERN}, unless the configuration contains a {@value #DATE_FORMAT_CUSTOM_PATTERN}
 * parameter.
 * <p>
 * The default number format provider for the globalisation context is {@link LocaleBasedNumberFormatProvider
 * locale-based}; a custom one can be configured through the {@value #NUMBER_FORMAT_PROVIDER_CLASS} parameter. The
 * {@value #NUMBER_FORMAT_CUSTOM_PATTERN} parameter controls custom pattern of the locale-based number format provider;
 * without any configuration, {@value #DEFAULT_NUMBER_FORMAT_CUSTOM_PATTERN} will be used.
 */
public class GlobalisationContextProvider implements ContextEnricher {
	private LocaleProvider localeProvider;
	private TimeZoneProvider timeZoneProvider;
	private ResourceBundleProvider resourceBundleProvider;
	private DateFormatProvider dateFormatProvider;
	private NumberFormatProvider numberFormatProvider;

	private final Logger logger;

	/**
	 * Creates a globalisation context provider.
	 */
	public GlobalisationContextProvider() {
		logger = Logger.get(getClass());
	}

	public void initialise(Configuration configuration) throws AluminumException {
		ConfigurationParameters parameters = configuration.getParameters();
		ConfigurationElementFactory configurationElementFactory = configuration.getConfigurationElementFactory();

		createLocaleProvider(parameters, configurationElementFactory);
		createTimeZoneProvider(parameters, configurationElementFactory);
		createResourceBundleProvider(parameters, configurationElementFactory);
		createDateFormatProvider(parameters, configurationElementFactory);
		createNumberFormatProvider(parameters, configurationElementFactory);
	}

	private void createLocaleProvider(ConfigurationParameters parameters,
			ConfigurationElementFactory configurationElementFactory) throws AluminumException {
		String localeProviderClassName = parameters.getValue(LOCALE_PROVIDER_CLASS, null);

		if (localeProviderClassName == null) {
			logger.debug("no locale provider configured, using constant locale provider");

			Locale locale;

			String configuredLocale = parameters.getValue(LOCALE, null);

			if (configuredLocale == null) {
				logger.debug("no locale configured, using default locale");

				locale = Locale.getDefault();
			} else {
				logger.debug("using configured locale '", configuredLocale, "'");

				locale = GlobalisationUtilities.convertLocale(configuredLocale);
			}

			localeProvider = new ConstantLocaleProvider(locale);
		} else {
			logger.debug("using configured locale provider of type ", localeProviderClassName);

			localeProvider = configurationElementFactory.instantiate(localeProviderClassName, LocaleProvider.class);
		}
	}

	private void createTimeZoneProvider(ConfigurationParameters parameters,
			ConfigurationElementFactory configurationElementFactory) throws AluminumException {
		String timeZoneProviderClassName = parameters.getValue(TIME_ZONE_PROVIDER_CLASS, null);

		if (timeZoneProviderClassName == null) {
			logger.debug("no time zone provider configured, using constant time zone provider");

			TimeZone timeZone;

			String configuredTimeZone = parameters.getValue(TIME_ZONE, null);

			if (configuredTimeZone == null) {
				logger.debug("no time zone configured, using default time zone");

				timeZone = TimeZone.getDefault();
			} else {
				logger.debug("using configured time zone '", configuredTimeZone, "'");

				timeZone = GlobalisationUtilities.convertTimeZone(configuredTimeZone);
			}

			timeZoneProvider = new ConstantTimeZoneProvider(timeZone);
		} else {
			logger.debug("using configured time zone provider of type ", timeZoneProviderClassName);

			timeZoneProvider =
				configurationElementFactory.instantiate(timeZoneProviderClassName, TimeZoneProvider.class);
		}
	}

	private void createResourceBundleProvider(ConfigurationParameters parameters,
			ConfigurationElementFactory configurationElementFactory) throws AluminumException {
		String resourceBundleProviderClassName = parameters.getValue(RESOURCE_BUNDLE_PROVIDER_CLASS, null);

		if (resourceBundleProviderClassName == null) {
			String baseName = parameters.getValue(RESOURCE_BUNDLE_BASE_NAME, DEFAULT_RESOURCE_BUNDLE_BASE_NAME);

			logger.debug("no resource bundle provider configured, ",
				"using name-based resource bundle provider with base name '", baseName, "'");

			resourceBundleProvider = new NameBasedResourceBundleProvider(baseName);
		} else {
			logger.debug("using configured resource bundle provider of type ", resourceBundleProviderClassName);

			resourceBundleProvider =
				configurationElementFactory.instantiate(resourceBundleProviderClassName, ResourceBundleProvider.class);
		}
	}

	private void createDateFormatProvider(ConfigurationParameters parameters,
			ConfigurationElementFactory configurationElementFactory) throws AluminumException {
		String dateFormatProviderClassName = parameters.getValue(DATE_FORMAT_PROVIDER_CLASS, null);

		if (dateFormatProviderClassName == null) {
			String customPattern = parameters.getValue(DATE_FORMAT_CUSTOM_PATTERN, DEFAULT_DATE_FORMAT_CUSTOM_PATTERN);

			logger.debug("no date format provider configured, ",
				"using locale-based date format provider with custom pattern '", customPattern, "'");

			dateFormatProvider = new EnvironmentBasedDateFormatProvider(customPattern);
		} else {
			logger.debug("using configured date format provider of type ", dateFormatProviderClassName);

			dateFormatProvider =
				configurationElementFactory.instantiate(dateFormatProviderClassName, DateFormatProvider.class);
		}
	}

	private void createNumberFormatProvider(ConfigurationParameters parameters,
			ConfigurationElementFactory configurationElementFactory) throws AluminumException {
		String numberFormatProviderClassName = parameters.getValue(NUMBER_FORMAT_PROVIDER_CLASS, null);

		if (numberFormatProviderClassName == null) {
			String customPattern =
				parameters.getValue(NUMBER_FORMAT_CUSTOM_PATTERN, DEFAULT_NUMBER_FORMAT_CUSTOM_PATTERN);

			logger.debug("no number format provider configured, ",
				"using locale-based number format provider with custom pattern '", customPattern, "'");

			numberFormatProvider = new LocaleBasedNumberFormatProvider(customPattern);
		} else {
			logger.debug("using configured number format provider of type ", numberFormatProviderClassName);

			numberFormatProvider =
				configurationElementFactory.instantiate(numberFormatProviderClassName, NumberFormatProvider.class);
		}
	}

	public void disable() {}

	public void beforeTemplate(Context context) throws AluminumException {
		Context parentContext = context.getParent();

		Object globalisationContext;

		if ((parentContext != null) && parentContext.getImplicitObjectNames().contains(GLOBALISATION_CONTEXT)) {
			globalisationContext = parentContext.getImplicitObject(GLOBALISATION_CONTEXT);
		} else {
			globalisationContext = new GlobalisationContext(
				localeProvider, timeZoneProvider, resourceBundleProvider, dateFormatProvider, numberFormatProvider);
		}

		context.addImplicitObject(GLOBALISATION_CONTEXT, globalisationContext);
	}

	public void afterTemplate(Context context) throws AluminumException {
		context.removeImplicitObject(GLOBALISATION_CONTEXT);
	}

	/**
	 * The name of the configuration parameter that contains the class name of the locale provider that should be used.
	 */
	public final static String LOCALE_PROVIDER_CLASS = "library.g11n.locale_provider.class";

	/**
	 * The name of the configuration parameter that holds the locale that will be given to the constant locale provider
	 * when no locale provider is configured.
	 */
	public final static String LOCALE = "library.g11n.locale";

	/** The name of the configuration parameter that controls which time zone provider will be used. */
	public final static String TIME_ZONE_PROVIDER_CLASS = "library.g11n.time_zone_provider.class";

	/**
	 * The name of the configuration parameter that contains the time zone that should be provided when no time zone
	 * provider is configured.
	 */
	public final static String TIME_ZONE = "library.g11n.time_zone";

	/**
	 * The name of the configuration parameter with the class name of the resource bundle provider that should be used.
	 */
	public final static String RESOURCE_BUNDLE_PROVIDER_CLASS = "library.g11n.resource_bundle_provider.class";

	/**
	 * The name of the configuration parameter that contains the base name that will be passed to the name-based
	 * resource bundle provider when no resource bundle provider is configured.
	 */
	public final static String RESOURCE_BUNDLE_BASE_NAME = "library.g11n.resource_bundle_provider.name_based.base_name";

	/**
	 * The base name that will be supplied to the name-based resource bundle provider if none is configured: {@value}.
	 */
	public final static String DEFAULT_RESOURCE_BUNDLE_BASE_NAME = "resources/aluminum";

	/**
	 * The name of the configuration parameter that holds the class name of the date format provider that should be
	 * used.
	 */
	public final static String DATE_FORMAT_PROVIDER_CLASS = "library.g11n.date_format_provider.class";

	/**
	 * The name of the configuration parameter that contains the custom pattern that, when no date format provider is
	 * configured, the locale-based date format provider will be created with.
	 */
	public final static String DATE_FORMAT_CUSTOM_PATTERN =
		"library.g11n.date_format_provider.locale_based.custom_pattern";

	/**
	 * The custom pattern that will be supplied to the locale-based date format provider when neither a date provider
	 * nor a custom pattern is configured: {@value}.
	 */
	public final static String DEFAULT_DATE_FORMAT_CUSTOM_PATTERN = "yyyy-MM-dd, HH:mm";

	/** The name of the configuration parameter that contains the class name of the number format provider to use. */
	public final static String NUMBER_FORMAT_PROVIDER_CLASS = "library.g11n.number_format_provider.class";

	/**
	 * The name of the configuration prarameter that holds the custom pattern to create the default number format
	 * provider with.
	 */
	public final static String NUMBER_FORMAT_CUSTOM_PATTERN =
		"library.g11n.date_format_provider.locale_based.custom_pattern";

	/**
	 * The custom pattern that will be used for the locale-based number format provider when none is configured:
	 * {@value}.
	 */
	public final static String DEFAULT_NUMBER_FORMAT_CUSTOM_PATTERN = "#,##0.##";
}