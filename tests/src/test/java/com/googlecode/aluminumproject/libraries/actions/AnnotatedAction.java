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
package com.googlecode.aluminumproject.libraries.actions;

import com.googlecode.aluminumproject.annotations.ActionInformation;
import com.googlecode.aluminumproject.annotations.ActionParameterInformation;
import com.googlecode.aluminumproject.context.Context;
import com.googlecode.aluminumproject.writers.Writer;

/**
 * An annotated action.
 *
 * @author levi_h
 */
@ActionInformation(name = "test")
public class AnnotatedAction extends AbstractAction {
	@ActionParameterInformation(name = "desc", required = true)
	@SuppressWarnings("unused")
	private String description;

	/**
	 * Creates an annotated action.
	 */
	public AnnotatedAction() {}

	public void execute(Context context, Writer writer) {}
}