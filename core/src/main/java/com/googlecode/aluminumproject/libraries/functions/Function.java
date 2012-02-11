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
package com.googlecode.aluminumproject.libraries.functions;

import com.googlecode.aluminumproject.AluminumException;
import com.googlecode.aluminumproject.context.Context;
import com.googlecode.aluminumproject.libraries.LibraryElementCreation;

/**
 * Determines a result based on a number of {@link FunctionArgument arguments}.
 */
public interface Function extends LibraryElementCreation<FunctionFactory> {
	/**
	 * Calls this function.
	 *
	 * @param context the context to run in
	 * @return the result of this function
	 * @throws AluminumException when the function can't be run
	 */
	Object call(Context context) throws AluminumException;
}