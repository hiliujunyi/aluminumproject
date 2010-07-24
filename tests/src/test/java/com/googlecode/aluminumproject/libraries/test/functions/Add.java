/*
 * Copyright 2010 Levi Hoogenberg
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
package com.googlecode.aluminumproject.libraries.test.functions;

import com.googlecode.aluminumproject.configuration.Configuration;
import com.googlecode.aluminumproject.configuration.ConfigurationParameters;
import com.googlecode.aluminumproject.context.Context;
import com.googlecode.aluminumproject.libraries.AbstractLibraryElement;
import com.googlecode.aluminumproject.libraries.functions.Function;
import com.googlecode.aluminumproject.libraries.functions.FunctionArgument;
import com.googlecode.aluminumproject.libraries.functions.FunctionArgumentInformation;
import com.googlecode.aluminumproject.libraries.functions.FunctionException;
import com.googlecode.aluminumproject.libraries.functions.FunctionFactory;
import com.googlecode.aluminumproject.libraries.functions.FunctionInformation;

import java.util.Collections;
import java.util.List;

/**
 * A dynamic function that parses its name (which should have the form {@code addNandN[andN...]}, where <em>N</em> is a
 * number) and adds the terms that were found.
 *
 * @author levi_h
 */
public class Add implements Function {
	private int[] terms;

	private Add(int[] terms) {
		this.terms = terms;
	}

	public Integer call(Context context) {
		int sum = 0;

		for (int term: terms) {
			sum += term;
		}

		return sum;
	}

	/**
	 * Creates {@link Add add} functions.
	 *
	 * @author levi_h
	 */
	public static class Factory extends AbstractLibraryElement implements FunctionFactory {
		private String name;

		private FunctionInformation information;

		/**
		 * Creates a factory for the <em>add</em> function.
		 *
		 * @param name the name of the addition function
		 */
		public Factory(String name) {
			this.name = name;

			information = new FunctionInformation(
				name, Integer.TYPE, Collections.<FunctionArgumentInformation>emptyList());
		}

		public void initialise(Configuration configuration, ConfigurationParameters parameters) {}

		public FunctionInformation getInformation() {
			return information;
		}

		public Function create(List<FunctionArgument> arguments, Context context) throws FunctionException {
			String[] termsAsText = name.substring("add".length()).split("and");
			int[] terms = new int[termsAsText.length];

			for (int i = 0; i < termsAsText.length; i++) {
				terms[i] = Integer.parseInt(termsAsText[i]);
			}

			return new Add(terms);
		}
	}
}