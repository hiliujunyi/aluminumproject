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
package com.googlecode.aluminumproject.converters.ds;

import com.googlecode.aluminumproject.AluminumException;
import com.googlecode.aluminumproject.annotations.Injected;
import com.googlecode.aluminumproject.configuration.Configuration;
import com.googlecode.aluminumproject.converters.Converter;
import com.googlecode.aluminumproject.converters.ConverterRegistry;
import com.googlecode.aluminumproject.utilities.text.Splitter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Converts strings to {@link List lists}. Lists are expected to have the following format: {@code [1, 2, 3]}.
 */
public class StringToListConverter implements Converter<String> {
	private @Injected Configuration configuration;

	/**
	 * Creates a string to list converter.
	 */
	public StringToListConverter() {}

	public boolean supportsSourceType(Class<? extends String> sourceType) {
		return true;
	}

	public boolean supportsTargetType(Type targetType) {
		boolean targetTypeIsInterface = (targetType == List.class);
		boolean targetTypeIsParameterisedInterface =
			(targetType instanceof ParameterizedType) && (((ParameterizedType) targetType).getRawType() == List.class);

		return targetTypeIsInterface || targetTypeIsParameterisedInterface;
	}

	public Object convert(String value, Type targetType) throws AluminumException {
		if (!supportsTargetType(targetType)) {
			throw new AluminumException("expected list as target type, not ", targetType);
		}

		ConverterRegistry converterRegistry = configuration.getConverterRegistry();

		List<Object> list = new LinkedList<Object>();

		Type elementType = (targetType == List.class)
			? Object.class : ((ParameterizedType) targetType).getActualTypeArguments()[0];

		for (String element: getElements(value)) {
			list.add(converterRegistry.convert(element, elementType));
		}

		return list;
	}

	private List<String> getElements(String list) throws AluminumException {
		ElementProcessor elementProcessor = new ElementProcessor();

		new Splitter(Arrays.asList("\\[", "\\s*,\\s*", "\\]"), '\\').split(list, elementProcessor);

		return elementProcessor.getElements();
	}

	private static class ElementProcessor implements Splitter.TokenProcessor {
		private List<String> elements;

		private StringBuilder elementBuilder;
		private int level;

		public ElementProcessor() {
			elements = new LinkedList<String>();

			elementBuilder = new StringBuilder();
		}

		public List<String> getElements() {
			return elements;
		}

		public void process(String token, String separator, String separatorPattern) throws AluminumException {
			if (separatorPattern == null) {
				if (level > 0) {
					throw new AluminumException("unclosed nested list");
				} else if (!token.equals("")) {
					throw new AluminumException("unexpected token after list: '", token, "'");
				}
			} else if (separatorPattern.equals("\\s*,\\s*") || separator.equals("]")) {
				elementBuilder.append(token);

				if (level == 0) {
					throw new AluminumException("unexpected separator: '", separator, "'");
				} else if (level == 1) {
					elements.add(elementBuilder.toString());

					elementBuilder.delete(0, elementBuilder.length());
				} else {
					elementBuilder.append(separator);
				}

				if (separator.equals("]")) {
					level--;
				}
			} else if (token.equals("")) {
				if (level > 0) {
					elementBuilder.append(separator);
				}

				level++;
			} else {
				throw new AluminumException("unexpected token before list: '", token, "'");
			}
		}
	}
}