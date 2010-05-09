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

import com.googlecode.aluminumproject.annotations.Ignored;
import com.googlecode.aluminumproject.converters.Converter;
import com.googlecode.aluminumproject.converters.ConverterException;
import com.googlecode.aluminumproject.converters.SimpleConverter;
import com.googlecode.aluminumproject.utilities.ReflectionUtilities;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Converts {@link String strings} to {@link Byte bytes}, {@link Short shorts}, {@link Integer integers}, {@link Long
 * longs}, {@link Float floats}, {@link Double doubles}, {@link BigInteger big integers}, and {@link BigDecimal big
 * decimals} using the various {@code valueOf}-methods and, for big integers and big decimals, constructors.
 *
 * @author levi_h
 */
public class StringToNumberConverter implements Converter<String> {
	private Map<Class<?>, Converter<String>> converters;

	/**
	 * Creates a string to number converter.
	 */
	public StringToNumberConverter() {
		converters = new HashMap<Class<?>, Converter<String>>();
		converters.put(Byte.TYPE, new StringToByteConverter());
		converters.put(Short.TYPE, new StringToShortConverter());
		converters.put(Integer.TYPE, new StringToIntegerConverter());
		converters.put(Long.TYPE, new StringToLongConverter());
		converters.put(Float.TYPE, new StringToFloatConverter());
		converters.put(Double.TYPE, new StringToDoubleConverter());

		for (Class<?> type: new HashSet<Class<?>>(converters.keySet())) {
			converters.put(ReflectionUtilities.wrapPrimitiveType(type), converters.get(type));
		}

		converters.put(BigInteger.class, new StringToBigIntegerConverter());
		converters.put(BigDecimal.class, new StringToBigDecimalConverter());
	}

	public boolean supportsSourceType(Class<? extends String> sourceType) {
		return true;
	}

	public <T> boolean supportsTargetType(Class<T> targetType) {
		return converters.containsKey(targetType);
	}

	public <T> T convert(String value, Class<T> targetType) throws ConverterException {
		return converters.get(targetType).convert(value, targetType);
	}

	@Ignored
	private static class StringToByteConverter extends SimpleConverter<String, Byte> {
		@Override
		protected Byte convert(String value) throws ConverterException {
			try {
				return Byte.valueOf(value);
			} catch (NumberFormatException exception) {
				throw new ConverterException(exception, "can't convert to byte");
			}
		}
	}

	@Ignored
	private static class StringToShortConverter extends SimpleConverter<String, Short> {
		@Override
		protected Short convert(String value) throws ConverterException {
			try {
				return Short.valueOf(value);
			} catch (NumberFormatException exception) {
				throw new ConverterException(exception, "can't convert to short");
			}
		}
	}

	@Ignored
	private static class StringToIntegerConverter extends SimpleConverter<String, Integer> {
		@Override
		protected Integer convert(String value) throws ConverterException {
			try {
				return Integer.valueOf(value);
			} catch (NumberFormatException exception) {
				throw new ConverterException(exception, "can't convert to integer");
			}
		}
	}

	@Ignored
	private static class StringToLongConverter extends SimpleConverter<String, Long> {
		@Override
		protected Long convert(String value) throws ConverterException {
			try {
				return Long.valueOf(value);
			} catch (NumberFormatException exception) {
				throw new ConverterException(exception, "can't convert to long");
			}
		}
	}

	@Ignored
	private static class StringToFloatConverter extends SimpleConverter<String, Float> {
		@Override
		protected Float convert(String value) throws ConverterException {
			try {
				return Float.valueOf(value);
			} catch (NumberFormatException exception) {
				throw new ConverterException(exception, "can't convert to float");
			}
		}
	}

	@Ignored
	private static class StringToDoubleConverter extends SimpleConverter<String, Double> {
		@Override
		protected Double convert(String value) throws ConverterException {
			try {
				return Double.valueOf(value);
			} catch (NumberFormatException exception) {
				throw new ConverterException(exception, "can't convert to double");
			}
		}
	}

	@Ignored
	private static class StringToBigIntegerConverter extends SimpleConverter<String, BigInteger> {
		@Override
		protected BigInteger convert(String value) throws ConverterException {
			try {
				return new BigInteger(value);
			} catch (NumberFormatException exception) {
				throw new ConverterException(exception, "can't convert to big integer");
			}
		}
	}

	@Ignored
	private static class StringToBigDecimalConverter extends SimpleConverter<String, BigDecimal> {
		@Override
		protected BigDecimal convert(String value) throws ConverterException {
			try {
				return new BigDecimal(value);
			} catch (NumberFormatException exception) {
				throw new ConverterException(exception, "can't convert to big decimal");
			}
		}
	}
}