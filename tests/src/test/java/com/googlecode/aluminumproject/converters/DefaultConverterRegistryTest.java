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
package com.googlecode.aluminumproject.converters;

import static com.googlecode.aluminumproject.configuration.DefaultConfiguration.CONFIGURATION_ELEMENT_PACKAGES;
import static com.googlecode.aluminumproject.converters.DefaultConverterRegistry.CONVERTER_PACKAGES;
import static com.googlecode.aluminumproject.utilities.ReflectionUtilities.getPackageName;

import com.googlecode.aluminumproject.configuration.ConfigurationParameters;
import com.googlecode.aluminumproject.configuration.DefaultConfiguration;
import com.googlecode.aluminumproject.configuration.test.TestConfiguration;
import com.googlecode.aluminumproject.converters.common.ObjectToStringConverter;
import com.googlecode.aluminumproject.converters.test.IgnoredConverter;
import com.googlecode.aluminumproject.converters.test.TestConverter;
import com.googlecode.aluminumproject.utilities.ReflectionUtilities;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("all")
@Test(groups = {"core", "fast"})
public class DefaultConverterRegistryTest {
	private DefaultConverterRegistry converterRegistry;

	@BeforeMethod
	public void createConverterRegistry() {
		ConfigurationParameters parameters = new ConfigurationParameters();

		converterRegistry = new DefaultConverterRegistry();
		converterRegistry.initialise(new TestConfiguration(parameters), parameters);
	}

	@Test(expectedExceptions = ConverterException.class)
	public void tryingToGetUnregisteredConverterShouldCauseException() {
		converterRegistry.getConverter(Class.class, ClassLoader.class);
	}

	public void defaultConverterRegistryShouldContainCommonConverters() {
		Converter<? super Object> converter = converterRegistry.getConverter(Object.class, String.class);
		assert converter instanceof ObjectToStringConverter;
	}

	public void converterPackagesShouldBeConfigurable() {
		ConfigurationParameters parameters = new ConfigurationParameters();
		parameters.addParameter(CONVERTER_PACKAGES, getPackageName(TestConverter.class));

		converterRegistry = new DefaultConverterRegistry();
		converterRegistry.initialise(new TestConfiguration(parameters), parameters);

		Converter<? super Float> converter = converterRegistry.getConverter(Float.class, CharSequence.class);
		assert converter instanceof TestConverter;
	}

	public void converterPackagesShouldBeExtendedByConfigurationElementPackages() {
		ConfigurationParameters parameters = new ConfigurationParameters();
		parameters.addParameter(CONFIGURATION_ELEMENT_PACKAGES, getPackageName(TestConverter.class));

		converterRegistry = new DefaultConverterRegistry();
		converterRegistry.initialise(new TestConfiguration(parameters), parameters);

		Converter<? super Float> converter = converterRegistry.getConverter(Float.class, CharSequence.class);
		assert converter instanceof TestConverter;
	}

	@Test(dependsOnMethods = "defaultConverterRegistryShouldContainCommonConverters")
	public void converterShouldBeFoundForSubtypesOfSourceType() {
		Converter<? super String> converter = converterRegistry.getConverter(String.class, String.class);
		assert converter instanceof ObjectToStringConverter;
	}

	public void registeredConverterShouldBeFindable() {
		converterRegistry.registerConverter(new TestConverter());

		Converter<? super Float> converter = converterRegistry.getConverter(Float.class, CharSequence.class);
		assert converter instanceof TestConverter;
	}

	@Test(dependsOnMethods = {
		"converterShouldBeFoundForSubtypesOfSourceType",
		"registeredConverterShouldBeFindable"
	})
	public void gettingConverterShouldResultInMostSpecificOne() {
		converterRegistry.registerConverter(new IgnoredConverter());

		assert converterRegistry.getConverter(String.class, String.class) instanceof IgnoredConverter;
	}

	public void converterShouldBeRegisteredWhenItsPackageIs() {
		converterRegistry.registerConverters(getPackageName(TestConverter.class));

		Converter<? super Float> converter = converterRegistry.getConverter(Float.class, CharSequence.class);
		assert converter instanceof TestConverter;
	}

	@Test(dependsOnMethods = {
		"tryingToGetUnregisteredConverterShouldCauseException",
		"converterShouldBeRegisteredWhenItsPackageIs"
	})
	public void ignoredConvertersShouldBeIgnoredWhenRegisteringPackages() {
		converterRegistry.registerConverters(getPackageName(IgnoredConverter.class));

		assert converterRegistry.getConverter(String.class, String.class) instanceof ObjectToStringConverter;
	}

	public void convertingNullShouldResultInNull() {
		assert converterRegistry.convert(null, Object.class) == null;
	}

	public void convertingToActualTypeShouldResultInSourceValue() {
		assert converterRegistry.convert(converterRegistry, ConverterRegistry.class) == converterRegistry;
	}

	@Test(dependsOnMethods = "converterShouldBeFoundForSubtypesOfSourceType")
	public void conversionShouldUseRegisteredConverters() {
		String convertedValue = converterRegistry.convert(2.5D, String.class);
		assert convertedValue != null;
		assert convertedValue.equals("2.5");
	}

	@Test(expectedExceptions = ConverterException.class)
	public void tryingConversionWithUnregisteredTypeShouldCauseException() {
		converterRegistry.convert(1, Boolean.TYPE);
	}

	@Test(dependsOnMethods = "conversionShouldUseRegisteredConverters", expectedExceptions = ConverterException.class)
	public void illegalConversionShouldCauseException() {
		converterRegistry.convert("running", Thread.State.class);
	}
}