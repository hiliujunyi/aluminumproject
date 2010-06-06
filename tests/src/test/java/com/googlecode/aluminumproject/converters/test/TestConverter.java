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
package com.googlecode.aluminumproject.converters.test;

import com.googlecode.aluminumproject.converters.SimpleConverter;

/**
 * A converter that can be used in tests.
 *
 * @author levi_h
 */
public class TestConverter extends SimpleConverter<Float, CharSequence> {
	/**
	 * Creates a test converter.
	 */
	public TestConverter() {}

	@Override
	protected CharSequence convert(Float value) {
		return value.toString();
	}
}