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
package com.googlecode.aluminumproject.libraries.xml.actions;

import com.googlecode.aluminumproject.annotations.ActionParameterInformation;

/**
 * Produces an XML element, the name of which is given as a parameter. Attributes can be added by nesting {@link
 * Attribute attribute actions}.
 *
 * @author levi_h
 */
public class Element extends AbstractElement {
	private String name;

	/**
	 * Creates an <em>element</em> action.
	 */
	public Element() {}

	@Override
	protected String getElementName() {
		return name;
	}

	/**
	 * Sets the name of the element.
	 *
	 * @param name the element name to use
	 */
	@ActionParameterInformation(required = true)
	public void setName(String name) {
		this.name = name;
	}
}