/*
 * Copyright 2010-2011 Levi Hoogenberg
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
package com.googlecode.aluminumproject.parsers.aluscript.instructions;

import com.googlecode.aluminumproject.AluminumException;
import com.googlecode.aluminumproject.parsers.aluscript.AluScriptContext;
import com.googlecode.aluminumproject.templates.TextElement;

import java.util.Map;

/**
 * Creates a {@link TextElement text element} and adds it to the context.
 *
 * @author levi_h
 */
public class TextInstruction extends AbstractInstruction {
	private String text;

	/**
	 * Creates a text instruction.
	 *
	 * @param name the name of the instruction
	 * @param text the text to create the text element with
	 */
	public TextInstruction(String name, String text) {
		super(name);

		this.text = text;
	}

	public void execute(Map<String, String> parameters, AluScriptContext context) throws AluminumException {
		if (!parameters.isEmpty()) {
			throw new AluminumException("text instructions do not allow parameters");
		}

		context.addTemplateElement(context.getConfiguration().getTemplateElementFactory().createTextElement(
			text, context.getLibraryUrlAbbreviations()));
	}
}