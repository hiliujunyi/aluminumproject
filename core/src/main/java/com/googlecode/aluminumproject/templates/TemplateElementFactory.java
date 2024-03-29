/*
 * Copyright 2009-2012 Aluminum project
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

import com.googlecode.aluminumproject.AluminumException;
import com.googlecode.aluminumproject.configuration.ConfigurationElement;
import com.googlecode.aluminumproject.expressions.ExpressionFactory;
import com.googlecode.aluminumproject.libraries.actions.ActionParameter;
import com.googlecode.aluminumproject.parsers.Parser;

import java.util.List;
import java.util.Map;

/**
 * Used by {@link Parser parsers} to create various kinds of {@link TemplateElement template elements}.
 */
public interface TemplateElementFactory extends ConfigurationElement {
	/**
	 * Creates an action element.
	 *
	 * @param actionDescriptor the action descriptor
	 * @param parameters the action parameters
	 * @param contributionDescriptors the action contribution descriptors
	 * @param libraryUrlAbbreviations the action element's library URL abbreviations
	 * @param lineNumber the location of the action element
	 * @return the new action element
	 * @throws AluminumException when the action element can't be created
	 */
	ActionElement createActionElement(ActionDescriptor actionDescriptor,
		Map<String, ActionParameter> parameters, List<ActionContributionDescriptor> contributionDescriptors,
		Map<String, String> libraryUrlAbbreviations, int lineNumber) throws AluminumException;

	/**
	 * Creates a text element.
	 *
	 * @param text the text that the text element will write
	 * @param libraryUrlAbbreviations the text element's library URL abbreviations
	 * @param lineNumber the location of the text element
	 * @return the new text element
	 * @throws AluminumException when the text element can't be created
	 */
	TextElement createTextElement(String text, Map<String, String> libraryUrlAbbreviations, int lineNumber)
		throws AluminumException;

	/**
	 * Creates an expression element.
	 *
	 * @param expressionFactory the expression factory that will create the evaluated expression
	 * @param text the expression text
	 * @param libraryUrlAbbreviations the expression element's library URL abbreviations
	 * @param lineNumber the location of the expression element
	 * @return the new expression element
	 * @throws AluminumException when the expression element can't be created
	 */
	ExpressionElement createExpressionElement(ExpressionFactory expressionFactory, String text,
		Map<String, String> libraryUrlAbbreviations, int lineNumber) throws AluminumException;
}