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
import static com.googlecode.aluminumproject.context.g11n.GlobalisationContextProvider.DATE_FORMAT_CUSTOM_PATTERN;
import static com.googlecode.aluminumproject.context.g11n.GlobalisationContextProvider.DATE_FORMAT_PROVIDER_CLASS;
import static com.googlecode.aluminumproject.context.g11n.GlobalisationContextProvider.LOCALE;
import static com.googlecode.aluminumproject.context.g11n.GlobalisationContextProvider.LOCALE_PROVIDER_CLASS;
import static com.googlecode.aluminumproject.context.g11n.GlobalisationContextProvider.NUMBER_FORMAT_CUSTOM_PATTERN;
import static com.googlecode.aluminumproject.context.g11n.GlobalisationContextProvider.NUMBER_FORMAT_PROVIDER_CLASS;
import static com.googlecode.aluminumproject.context.g11n.GlobalisationContextProvider.RESOURCE_BUNDLE_BASE_NAME;
import static com.googlecode.aluminumproject.context.g11n.GlobalisationContextProvider.RESOURCE_BUNDLE_PROVIDER_CLASS;

import com.googlecode.aluminumproject.configuration.ConfigurationParameters;
import com.googlecode.aluminumproject.configuration.TestConfiguration;
import com.googlecode.aluminumproject.context.Context;
import com.googlecode.aluminumproject.context.DefaultContext;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("javadoc")
@Test(groups = {"libraries", "libraries-g11n", "fast"})
public class GlobalisationContextProviderTest {
	private GlobalisationContextProvider globalisationContextProvider;

	private Context context;

	private Locale defaultLocale;

	@BeforeMethod
	public void createGlobalisationContextProviderAndContext() {
		globalisationContextProvider = new GlobalisationContextProvider();

		context = new DefaultContext();
	}

	@BeforeClass
	public void changeDefaultLocale() {
		defaultLocale = Locale.getDefault();

		Locale.setDefault(Locale.ENGLISH);
	}

	@AfterClass
	public void restoreDefaultLocale() {
		Locale.setDefault(defaultLocale);
	}

	private void initialiseGlobalisationContextProvider() {
		initialiseGlobalisationContextProvider(new ConfigurationParameters());
	}

	private void initialiseGlobalisationContextProvider(ConfigurationParameters parameters) {
		globalisationContextProvider.initialise(new TestConfiguration(parameters));
	}

	public void globalisationContextShouldBeAddedToContextBeforeTemplate() {
		initialiseGlobalisationContextProvider();

		globalisationContextProvider.beforeTemplate(context);

		assert context.getImplicitObjectNames().contains(GLOBALISATION_CONTEXT);
	}

	@Test(dependsOnMethods = "globalisationContextShouldBeAddedToContextBeforeTemplate")
	public void globalisationContextShouldBeRemovedFromContextAfterTemplate() {
		initialiseGlobalisationContextProvider();

		globalisationContextProvider.beforeTemplate(context);
		globalisationContextProvider.afterTemplate(context);

		assert !context.getImplicitObjectNames().contains(GLOBALISATION_CONTEXT);
	}

	@Test(dependsOnMethods = "globalisationContextShouldBeAddedToContextBeforeTemplate")
	public void globalisationContextShouldBeInheritedFromParentContext() {
		initialiseGlobalisationContextProvider();

		Context subcontext = context.createSubcontext();

		globalisationContextProvider.beforeTemplate(context);
		globalisationContextProvider.beforeTemplate(subcontext);

		assert subcontext.getImplicitObjectNames().contains(GLOBALISATION_CONTEXT);

		Object globalisationContextOfContext = context.getImplicitObject(GLOBALISATION_CONTEXT);
		Object globalisationContextOfSubcontext = subcontext.getImplicitObject(GLOBALISATION_CONTEXT);
		assert globalisationContextOfSubcontext == globalisationContextOfContext;
	}

	@Test(dependsOnMethods = "globalisationContextShouldBeAddedToContextBeforeTemplate")
	public void defaultLocaleProviderShouldProvideDefaultLocale() {
		initialiseGlobalisationContextProvider();

		globalisationContextProvider.beforeTemplate(context);

		Locale locale = GlobalisationContext.from(context).getLocaleProvider().provide(context);
		assert locale != null;
		assert locale.equals(Locale.getDefault());
	}

	@Test(dependsOnMethods = "defaultLocaleProviderShouldProvideDefaultLocale")
	public void defaultLocaleShouldBeConfigurable() {
		ConfigurationParameters parameters = new ConfigurationParameters();
		parameters.addParameter(LOCALE, "fr_FR");

		initialiseGlobalisationContextProvider(parameters);

		globalisationContextProvider.beforeTemplate(context);

		Locale locale = GlobalisationContext.from(context).getLocaleProvider().provide(context);
		assert locale != null;

		String language = locale.getLanguage();
		assert language != null;
		assert language.equals("fr");

		String country = locale.getCountry();
		assert country != null;
		assert country.equals("FR");
	}

	public static class SpanishLocaleProvider extends ConstantLocaleProvider {
		public SpanishLocaleProvider() {
			super(new Locale("es", "ES"));
		}
	}

	@Test(dependsOnMethods = "globalisationContextShouldBeAddedToContextBeforeTemplate")
	public void localeProviderShouldBeConfigurable() {
		ConfigurationParameters parameters = new ConfigurationParameters();
		parameters.addParameter(LOCALE_PROVIDER_CLASS, SpanishLocaleProvider.class.getName());

		initialiseGlobalisationContextProvider(parameters);

		globalisationContextProvider.beforeTemplate(context);

		Locale locale = GlobalisationContext.from(context).getLocaleProvider().provide(context);
		assert locale != null;

		String language = locale.getLanguage();
		assert language != null;
		assert language.equals("es");

		String country = locale.getCountry();
		assert country != null;
		assert country.equals("ES");
	}

	@Test(dependsOnMethods = "defaultLocaleProviderShouldProvideDefaultLocale")
	public void defaultResourceBundleProviderShouldProvideResourcesFromResourceBundleWithDefaultBaseName() {
		initialiseGlobalisationContextProvider();

		globalisationContextProvider.beforeTemplate(context);

		ResourceBundle resourceBundle = GlobalisationContext.from(context).getResourceBundleProvider().provide(context);
		assert resourceBundle != null;
		assert Collections.list(resourceBundle.getKeys()).contains("aluminum");
		assert resourceBundle.getString("aluminum").equals("a powerful and flexible template engine");
	}

	@Test(dependsOnMethods = "defaultResourceBundleProviderShouldProvideResourcesFromResourceBundleWithDefaultBaseName")
	public void baseNameOfDefaultResourceBundleProviderShouldBeConfigurable() {
		ConfigurationParameters parameters = new ConfigurationParameters();
		parameters.addParameter(RESOURCE_BUNDLE_BASE_NAME, "resources/projects");

		initialiseGlobalisationContextProvider(parameters);

		globalisationContextProvider.beforeTemplate(context);

		ResourceBundle resourceBundle = GlobalisationContext.from(context).getResourceBundleProvider().provide(context);
		assert resourceBundle != null;
		assert Collections.list(resourceBundle.getKeys()).contains("rivoli");
		assert resourceBundle.getString("rivoli").equals("the framework that makes generating reports fun");
	}

	public static class ProjectsResourceBundleProvider extends NameBasedResourceBundleProvider {
		public ProjectsResourceBundleProvider() {
			super("resources/projects");
		}
	}

	@Test(dependsOnMethods = "globalisationContextShouldBeAddedToContextBeforeTemplate")
	public void resourceBundleProviderShouldBeConfigurable() {
		ConfigurationParameters parameters = new ConfigurationParameters();
		parameters.addParameter(RESOURCE_BUNDLE_PROVIDER_CLASS, ProjectsResourceBundleProvider.class.getName());

		initialiseGlobalisationContextProvider(parameters);

		globalisationContextProvider.beforeTemplate(context);

		ResourceBundle resourceBundle = GlobalisationContext.from(context).getResourceBundleProvider().provide(context);
		assert resourceBundle != null;
		assert Collections.list(resourceBundle.getKeys()).contains("aluminum");
		assert resourceBundle.getString("aluminum").equals("a powerful and flexible template engine");
	}

	@Test(dependsOnMethods = "defaultLocaleShouldBeConfigurable")
	public void defaultDateFormatProviderShouldBeLocaleBased() {
		ConfigurationParameters parameters = new ConfigurationParameters();
		parameters.addParameter(LOCALE, "fr_FR");

		initialiseGlobalisationContextProvider(parameters);

		globalisationContextProvider.beforeTemplate(context);

		DateFormat dateFormat =
			GlobalisationContext.from(context).getDateFormatProvider().provide(DateFormatType.MEDIUM_DATE, context);
		assert dateFormat != null;
		assert dateFormat.format(new Date(0L)).equals("1 janv. 1970");
	}

	@Test(dependsOnMethods = "globalisationContextShouldBeAddedToContextBeforeTemplate")
	public void defaultDateFormatProviderShouldUseDefaultCustomPattern() {
		initialiseGlobalisationContextProvider();

		globalisationContextProvider.beforeTemplate(context);

		DateFormat dateFormat =
			GlobalisationContext.from(context).getDateFormatProvider().provide(DateFormatType.CUSTOM, context);
		assert dateFormat != null;
		assert dateFormat.format(new Date(0L)).equals("1970-01-01, 00:00");
	}

	@Test(dependsOnMethods = "defaultDateFormatProviderShouldUseDefaultCustomPattern")
	public void customPatternOfDefaultDateFormatShouldBeConfigurable() {
		ConfigurationParameters parameters = new ConfigurationParameters();
		parameters.addParameter(DATE_FORMAT_CUSTOM_PATTERN, "yyyyMMdd");

		initialiseGlobalisationContextProvider(parameters);

		globalisationContextProvider.beforeTemplate(context);

		DateFormat dateFormat =
			GlobalisationContext.from(context).getDateFormatProvider().provide(DateFormatType.CUSTOM, context);
		assert dateFormat != null;
		assert dateFormat.format(new Date(0L)).equals("19700101");
	}

	public static class YearFormatProvider implements DateFormatProvider {
		public DateFormat provide(DateFormatType type, Context context) {
			return new SimpleDateFormat("yy");
		}
	}

	@Test(dependsOnMethods = "globalisationContextShouldBeAddedToContextBeforeTemplate")
	public void dateFormatProviderShouldBeConfigurable() {
		ConfigurationParameters parameters = new ConfigurationParameters();
		parameters.addParameter(DATE_FORMAT_PROVIDER_CLASS, YearFormatProvider.class.getName());

		initialiseGlobalisationContextProvider(parameters);

		globalisationContextProvider.beforeTemplate(context);

		DateFormat dateFormat =
			GlobalisationContext.from(context).getDateFormatProvider().provide(DateFormatType.LONG_DATE, context);
		assert dateFormat != null;
		assert dateFormat.format(new Date(0L)).equals("70");
	}

	@Test(dependsOnMethods = "defaultLocaleShouldBeConfigurable")
	public void defaultNumberFormatProviderShouldBeLocaleBased() {
		ConfigurationParameters parameters = new ConfigurationParameters();
		parameters.addParameter(LOCALE, "nl_NL");

		initialiseGlobalisationContextProvider(parameters);

		globalisationContextProvider.beforeTemplate(context);

		NumberFormat numberFormat =
			GlobalisationContext.from(context).getNumberFormatProvider().provide(NumberFormatType.PERCENTAGE, context);
		assert numberFormat != null;
		assert numberFormat.format(0.8).equals("80%");
	}

	@Test(dependsOnMethods = "defaultNumberFormatProviderShouldBeLocaleBased")
	public void defaultNumberFormatProviderShouldUseDefaultCustomPattern() {
		ConfigurationParameters parameters = new ConfigurationParameters();
		parameters.addParameter(LOCALE, "nl_NL");

		initialiseGlobalisationContextProvider(parameters);

		globalisationContextProvider.beforeTemplate(context);

		NumberFormat numberFormat =
			GlobalisationContext.from(context).getNumberFormatProvider().provide(NumberFormatType.CUSTOM, context);
		assert numberFormat != null;
		assert numberFormat.format(1000L).equals("1.000");
		assert numberFormat.format(-1.5).equals("-1,5");
	}

	@Test(dependsOnMethods = "defaultNumberFormatProviderShouldUseDefaultCustomPattern")
	public void customPatternOfDefaultNumberFormatProviderShouldBeConfigurable() {
		ConfigurationParameters parameters = new ConfigurationParameters();
		parameters.addParameter(LOCALE, "nl_NL");
		parameters.addParameter(NUMBER_FORMAT_CUSTOM_PATTERN, "#,#00.0");

		initialiseGlobalisationContextProvider(parameters);

		globalisationContextProvider.beforeTemplate(context);

		NumberFormat numberFormat =
			GlobalisationContext.from(context).getNumberFormatProvider().provide(NumberFormatType.CUSTOM, context);
		assert numberFormat != null;
		assert numberFormat.format(1000L).equals("1.000,0");
		assert numberFormat.format(-1.5).equals("-01,5");
	}

	public static class IntegerFormatProvider implements NumberFormatProvider {
		public NumberFormat provide(NumberFormatType type, Context context) {
			return new DecimalFormat("0");
		}
	}

	@Test(dependsOnMethods = "globalisationContextShouldBeAddedToContextBeforeTemplate")
	public void numberFormatProviderShouldBeConfigurable() {
		ConfigurationParameters parameters = new ConfigurationParameters();
		parameters.addParameter(NUMBER_FORMAT_PROVIDER_CLASS, IntegerFormatProvider.class.getName());

		initialiseGlobalisationContextProvider(parameters);

		globalisationContextProvider.beforeTemplate(context);

		NumberFormat numberFormat =
			GlobalisationContext.from(context).getNumberFormatProvider().provide(NumberFormatType.NUMBER, context);
		assert numberFormat != null;
		assert numberFormat.format(12.5).equals("12");
		assert numberFormat.format(-12.5).equals("-12");
	}
}