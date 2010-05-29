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
package com.googlecode.aluminumproject.converters.common;

import com.googlecode.aluminumproject.converters.Converter;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("all")
@Test(groups = {"core", "fast"})
public class StringToBooleanConverterTest {
	private Converter<String> converter;

	@BeforeMethod
	public void createConverter() {
		converter = new StringToBooleanConverter();
	}

	public void literalTrueShouldResultInTrue() {
		assert converter.convert("true", Boolean.TYPE);
	}

	public void literalFalseShouldResultInFalse() {
		assert !converter.convert("false", Boolean.TYPE);
	}

	public void nonLiteralShouldResultInFalse() {
		assert !converter.convert("yes", Boolean.TYPE);
	}

	@Test(dependsOnMethods = {"literalTrueShouldResultInTrue", "literalFalseShouldResultInFalse"})
	public void conversionShouldBeCaseInsensitive() {
		assert converter.convert("TRUE", Boolean.TYPE);
		assert !converter.convert("False", Boolean.TYPE);
	}
}