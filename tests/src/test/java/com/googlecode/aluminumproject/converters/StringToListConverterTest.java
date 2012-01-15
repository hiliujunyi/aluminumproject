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
package com.googlecode.aluminumproject.converters;

import com.googlecode.aluminumproject.AluminumException;
import com.googlecode.aluminumproject.configuration.ConfigurationParameters;
import com.googlecode.aluminumproject.configuration.TestConfiguration;
import com.googlecode.aluminumproject.converters.ds.StringToListConverter;
import com.googlecode.aluminumproject.utilities.GenericsUtilities;
import com.googlecode.aluminumproject.utilities.Injector;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("javadoc")
@Test(groups = {"libraries", "libraries-ds", "fast"})
public class StringToListConverterTest {
	private Converter<String> converter;

	@BeforeMethod
	public void createConverter() {
		TestConfiguration configuration = new TestConfiguration(new ConfigurationParameters());
		configuration.setConverterRegistry(new DefaultConverterRegistry());
		configuration.getConverterRegistry().initialise(configuration);

		converter = new StringToListConverter();

		Injector injector = new Injector();
		injector.addValueProvider(new Injector.ClassBasedValueProvider(configuration));
		injector.inject(converter);
	}

	public void untypedListShouldBeSupportedAsTargetType() {
		assert converter.supportsTargetType(List.class);
	}

	public void typedListShouldBeSupportedAsTargetType() {
		assert converter.supportsTargetType(GenericsUtilities.getType("List<String>", "java.lang", "java.util"));
	}

	@Test(expectedExceptions = AluminumException.class)
	public void tryingToConvertToUnsupportedTypeShouldCauseException() {
		converter.convert("[1]", Integer.class);
	}

	@Test(dependsOnMethods = "untypedListShouldBeSupportedAsTargetType")
	public void untypedListShouldBeConvertible() {
		Object convertedValue = converter.convert("[a, b, c]", List.class);
		assert convertedValue instanceof List;
		assert convertedValue.equals(Arrays.asList("a", "b", "c"));
	}

	@Test(dependsOnMethods = "typedListShouldBeSupportedAsTargetType")
	public void typedListShouldBeConvertible() {
		Type targetType = GenericsUtilities.getType("List<Integer>", "java.lang", "java.util");

		Object convertedValue = converter.convert("[1, 2, 3]", targetType);
		assert convertedValue instanceof List;
		assert convertedValue.equals(Arrays.asList(1, 2, 3));
	}

	@Test(dependsOnMethods = "typedListShouldBeConvertible")
	public void nestedListShouldBeConvertible() {
		Type targetType = GenericsUtilities.getType("List<List<Integer>>", "java.lang", "java.util");

		Object convertedValue = converter.convert("[[1, 2], [3, 4]]", targetType);
		assert convertedValue instanceof List;

		List<List<Integer>> expectedValue = new ArrayList<List<Integer>>();
		expectedValue.add(Arrays.asList(1, 2));
		expectedValue.add(Arrays.asList(3, 4));
		assert convertedValue.equals(expectedValue);
	}

	@Test(dependsOnMethods = "untypedListShouldBeSupportedAsTargetType", expectedExceptions = AluminumException.class)
	public void tryingToConvertListWithIllegalFormatShouldCauseException() {
		converter.convert("1, 2", List.class);
	}
}