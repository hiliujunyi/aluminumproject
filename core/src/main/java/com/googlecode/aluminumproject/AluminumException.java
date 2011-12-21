/*
 * Copyright 2009-2011 Levi Hoogenberg
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
package com.googlecode.aluminumproject;

import com.googlecode.aluminumproject.utilities.StringUtilities;

/**
 * Thrown by the template engine when an unexpected condition arises.
 *
 * @author levi_h
 */
public class AluminumException extends RuntimeException {
	/**
	 * Creates an Aluminum exception.
	 *
	 * @param messageParts the parts of the exception message
	 */
	public AluminumException(Object... messageParts) {
		super(StringUtilities.join(messageParts));
	}

	/**
	 * Creates an Aluminum exception.
	 *
	 * @param cause the cause of the exception
	 * @param messageParts the parts of the exception message.
	 */
	public AluminumException(Throwable cause, Object... messageParts) {
		super(StringUtilities.join(messageParts), cause);
	}

	private final static long serialVersionUID = 20111218L;
}