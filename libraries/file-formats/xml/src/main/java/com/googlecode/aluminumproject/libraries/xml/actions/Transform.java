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
import com.googlecode.aluminumproject.libraries.actions.AbstractAction;
import com.googlecode.aluminumproject.libraries.actions.ActionException;
import com.googlecode.aluminumproject.libraries.xml.model.Element;
import com.googlecode.aluminumproject.writers.Writer;
import com.googlecode.aluminumproject.writers.WriterException;

/**
 * Transforms a document and writes the results to the writer.
 *
 * @author levi_h
 */
public class Transform extends AbstractAction {
	@ActionParameterInformation(required = true)
	private Element document;

	@ActionParameterInformation(required = true)
	private Element styleSheet;

	/**
	 * Creates a <em>transform</em> action.
	 */
	public Transform() {}

	public void execute(Context context, Writer writer) throws ActionException, WriterException {
		logger.debug("transforming document ", document, " using style sheet ", styleSheet);

		for (Object result: document.transform(styleSheet)) {
			logger.debug("writing transformation result ", result);

			writer.write(result);
		}
	}
}