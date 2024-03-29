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
package com.googlecode.aluminumproject.libraries.actions;

import java.lang.reflect.Type;

/**
 * Provides information about the parameter of an {@link Action action}.
 */
public class ActionParameterInformation {
	private String name;
	private Type type;
	private boolean required;

	private Integer indexWhenFunctionArgument;

	/**
	 * Creates action parameter information.
	 *
	 * @param name the name of the parameter
	 * @param type the type of the parameter
	 * @param required whether the parameter is required or not
	 * @param indexWhenFunctionArgument the argument index when the parameter's action is used as if it were a function
	 *                                  (may be {@code null})
	 */
	public ActionParameterInformation(String name, Type type, boolean required, Integer indexWhenFunctionArgument) {
		this.name = name;
		this.type = type;
		this.required = required;

		this.indexWhenFunctionArgument = indexWhenFunctionArgument;
	}

	/**
	 * Returns the name of the parameter. It should be unique for an {@link Action action}.
	 *
	 * @return the parameter's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the type of the parameter.
	 *
	 * @return the parameter's type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Returns whether the parameter is required or not.
	 *
	 * @return {@code true} if the parameter is required or {@code false} if it's an optional parameter
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * Determines, when the action of the parameter is used as if it were a function, which of the function arguments
	 * maps to the parameter. When the action is not usable as function or when the parameter is not used if it is, this
	 * method will return {@code null}.
	 *
	 * @return the index of the parameter when its action is used as if it were a function, possibly {@code null}
	 */
	public Integer getIndexWhenFunctionArgument() {
		return indexWhenFunctionArgument;
	}
}