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
import com.googlecode.aluminumproject.context.Context;
import com.googlecode.aluminumproject.context.ContextException;
import com.googlecode.aluminumproject.libraries.actions.AbstractAction;
import com.googlecode.aluminumproject.libraries.actions.ActionException;
import com.googlecode.aluminumproject.writers.Writer;
import com.googlecode.aluminumproject.writers.WriterException;

/**
 * Adds an attribute to an {@link AbstractElement element}.
 *
 * @author levi_h
 */
public class Attribute extends AbstractAction {
	private String prefix;
	private String name;
	private String value;

	/**
	 * Creates an <em>attribute</em> action.
	 */
	public Attribute() {}

	/**
	 * Sets the namespace prefix of the attribute.
	 *
	 * @param prefix the attribute prefix to use
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * Sets the name of the attribute.
	 *
	 * @param name the attribute name to use
	 */
	@ActionParameterInformation(required = true)
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the value of the attribute.
	 *
	 * @param value the attribute value to use
	 */
	@ActionParameterInformation(required = true)
	public void setValue(String value) {
		this.value = value;
	}

	public void execute(Context context, Writer writer) throws ActionException, ContextException, WriterException {
		AbstractElement element = findAncestorOfType(AbstractElement.class);

		if (element == null) {
			throw new ActionException("attributes should be nested inside an element");
		} else {
			element.addAttribute(prefix, name, value);
		}
	}
}