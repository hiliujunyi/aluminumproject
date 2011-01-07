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
package com.googlecode.aluminumproject.templates;

import com.googlecode.aluminumproject.context.Context;
import com.googlecode.aluminumproject.libraries.actions.ActionParameter;
import com.googlecode.aluminumproject.writers.Writer;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Am action element that can be used in tests.
 *
 * @author levi_h
 */
public class TestActionElement implements ActionElement {
	private ActionDescriptor descriptor;

	/**
	 * Creates a test action element with a default descriptor.
	 */
	public TestActionElement() {
		this(new ActionDescriptor("test", "test"));
	}

	/**
	 * Creates a test action element.
	 *
	 * @param descriptor a descriptor of the action
	 */
	public TestActionElement(ActionDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	public Map<String, String> getLibraryUrlAbbreviations() {
		return Collections.emptyMap();
	}

	public ActionDescriptor getDescriptor() {
		return descriptor;
	}

	public Map<String, ActionParameter> getParameters() {
		return Collections.emptyMap();
	}

	public List<ActionContributionDescriptor> getContributionDescriptors() {
		return Collections.emptyList();
	}

	public void process(Template template, TemplateContext templateContext, Context context, Writer writer) {}
}