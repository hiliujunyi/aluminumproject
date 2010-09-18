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
package com.googlecode.aluminumproject.libraries;

import com.googlecode.aluminumproject.configuration.Configuration;
import com.googlecode.aluminumproject.configuration.ConfigurationException;
import com.googlecode.aluminumproject.configuration.ConfigurationParameters;
import com.googlecode.aluminumproject.libraries.actions.TestActionContributionFactory;
import com.googlecode.aluminumproject.libraries.functions.Add;
import com.googlecode.aluminumproject.libraries.functions.FunctionFactory;
import com.googlecode.aluminumproject.utilities.ReflectionUtilities;

/**
 * A library that can be used in tests.
 *
 * @author levi_h
 */
public class TestLibrary extends AbstractLibrary {
	private Configuration configuration;

	private LibraryInformation information;

	/**
	 * Creates a test library.
	 */
	public TestLibrary() {
		super(ReflectionUtilities.getPackageName(TestLibrary.class));

		String url = "http://aluminumproject.googlecode.com/test";
		String version = "test";
		String displayName = "Test library";

		information = new LibraryInformation(url, version, displayName, false, false, true);
	}

	@Override
	public void initialise(
			Configuration configuration, ConfigurationParameters parameters) throws ConfigurationException {
		super.initialise(configuration, parameters);

		addActionContributionFactory(new TestActionContributionFactory());

		this.configuration = configuration;
	}

	/**
	 * Returns the injected configuration.
	 *
	 * @return the configuration used
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	public LibraryInformation getInformation() {
		return information;
	}

	@Override
	public FunctionFactory getDynamicFunctionFactory(String name) {
		FunctionFactory dynamicFunctionFactory;

		if (name.matches("add\\d+(and\\d+)+")) {
			dynamicFunctionFactory = new Add.Factory(name);

			initialiseLibraryElement(dynamicFunctionFactory);
		} else {
			dynamicFunctionFactory = super.getDynamicFunctionFactory(name);
		}

		return dynamicFunctionFactory;
	}
}