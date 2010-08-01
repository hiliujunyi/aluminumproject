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
package com.googlecode.aluminumproject.libraries.io;

import com.googlecode.aluminumproject.libraries.AbstractLibrary;
import com.googlecode.aluminumproject.libraries.LibraryInformation;
import com.googlecode.aluminumproject.utilities.EnvironmentUtilities;
import com.googlecode.aluminumproject.utilities.ReflectionUtilities;

/**
 * Provides actions and functions related to input and output.
 *
 * @author levi_h
 */
public class IoLibrary extends AbstractLibrary {
	private LibraryInformation information;

	/**
	 * Creates an I/O library.
	 */
	public IoLibrary() {
		super(ReflectionUtilities.getPackageName(IoLibrary.class));

		String url = "http://aluminumproject.googlecode.com/io";
		String version = EnvironmentUtilities.getVersion();

		String displayName = "I/O library";

		information = new LibraryInformation(url, version, displayName);
	}

	public LibraryInformation getInformation() {
		return information;
	}
}