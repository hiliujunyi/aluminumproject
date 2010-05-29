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
package com.googlecode.aluminumproject.libraries.functions;

import com.googlecode.aluminumproject.configuration.ConfigurationParameters;
import com.googlecode.aluminumproject.configuration.test.TestConfiguration;
import com.googlecode.aluminumproject.context.Context;
import com.googlecode.aluminumproject.context.DefaultContext;
import com.googlecode.aluminumproject.converters.ConverterRegistry;
import com.googlecode.aluminumproject.converters.DefaultConverterRegistry;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("all")
@Test(groups = {"core", "fast"})
public class ConstantFunctionArgumentTest {
	private FunctionArgument argument;

	private Context context;

	@BeforeMethod
	public void createArgument() {
		ConfigurationParameters parameters = new ConfigurationParameters();

		ConverterRegistry converterRegistry = new DefaultConverterRegistry();
		converterRegistry.initialise(new TestConfiguration(parameters), parameters);

		argument = new ConstantFunctionArgument("32768", converterRegistry);

		context = new DefaultContext();
	}

	public void valueShouldBeRetrievableAsObject() {
		Object valueAsObject = argument.getValue(Object.class, context);
		assert valueAsObject != null;
		assert valueAsObject.equals("32768");
	}

	public void valueShouldBeRetrievableWithOriginalType() {
		String valueAsString = argument.getValue(String.class, context);
		assert valueAsString != null;
		assert valueAsString.equals("32768");
	}

	public void valueShouldBeRetrievableWithCompatibleType() {
		Integer valueAsInteger = argument.getValue(Integer.class, context);
		assert valueAsInteger != null;
		assert valueAsInteger.intValue() == 32768;
	}

	@Test(expectedExceptions = FunctionException.class)
	public void retrievingValueWithIncompatibleTypeShouldCauseException() {
		argument.getValue(Short.TYPE, context);
	}
}