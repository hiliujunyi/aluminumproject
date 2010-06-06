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
package com.googlecode.aluminumproject.resources;

import com.googlecode.aluminumproject.configuration.ConfigurationParameters;
import com.googlecode.aluminumproject.configuration.test.TestConfiguration;
import com.googlecode.aluminumproject.utilities.resources.MemoryResourceStoreFinder;
import com.googlecode.aluminumproject.utilities.resources.ResourceStoreFinder;

import org.testng.annotations.Test;

@SuppressWarnings("all")
@Test(groups = {"core", "fast"})
public class MemoryTemplateStoreFinderFactoryTest {
	public void factoryShouldCreateMemoryTemplateStoreFinder() {
		ConfigurationParameters parameters = new ConfigurationParameters();

		TemplateStoreFinderFactory templateStoreFinderFactory = new MemoryTemplateStoreFinderFactory();
		templateStoreFinderFactory.initialise(new TestConfiguration(parameters), parameters);
		assert templateStoreFinderFactory.createTemplateStoreFinder() instanceof MemoryResourceStoreFinder;
	}

	@Test(dependsOnMethods = "factoryShouldCreateMemoryTemplateStoreFinder")
	public void repeatedCreationShouldResultInSingleTemplateStoreFinder() {
		ConfigurationParameters parameters = new ConfigurationParameters();

		TemplateStoreFinderFactory templateStoreFinderFactory = new MemoryTemplateStoreFinderFactory();
		templateStoreFinderFactory.initialise(new TestConfiguration(parameters), parameters);

		ResourceStoreFinder firstTemplateStoreFinder = templateStoreFinderFactory.createTemplateStoreFinder();
		ResourceStoreFinder secondTemplateStoreFinder = templateStoreFinderFactory.createTemplateStoreFinder();
		assert firstTemplateStoreFinder == secondTemplateStoreFinder;
	}
}