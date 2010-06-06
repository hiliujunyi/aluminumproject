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

import com.googlecode.aluminumproject.converters.test.TestConverter;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("all")
@Test(groups = {"core", "fast"})
public class SimpleConverterTest {
	private Converter<Float> converter;

	@BeforeMethod
	public void createConverter() {
		converter = new TestConverter();
	}

	public void targetTypeShouldBeSupported() {
		assert converter.supportsTargetType(CharSequence.class);
	}

	@Test(dependsOnMethods = "targetTypeShouldBeSupported")
	public void supertypesOfTargetTypeShouldBeSupported() {
		assert converter.supportsTargetType(Object.class);
	}

	@Test(dependsOnMethods = "targetTypeShouldBeSupported")
	public void subtypesOfTargetTypeShouldNotBeSupported() {
		assert !converter.supportsTargetType(String.class);
	}

	@Test(dependsOnMethods = "targetTypeShouldBeSupported")
	public void valueShouldBeConvertibleIntoSupportedType() {
		CharSequence convertedValue = converter.convert(12.5F, CharSequence.class);
		assert convertedValue != null;
		assert convertedValue.equals("12.5");
	}

	@Test(dependsOnMethods = "subtypesOfTargetTypeShouldNotBeSupported", expectedExceptions = ConverterException.class)
	public void conversionToUnsupportedTargetTypeShouldCauseException() {
		converter.convert(12F, String.class);
	}
}