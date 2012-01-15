/*
 * Copyright 2011-2012 Levi Hoogenberg
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
package com.googlecode.aluminumproject.finders;

import com.googlecode.aluminumproject.AluminumException;
import com.googlecode.aluminumproject.configuration.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Uses the file system to find templates.
 * <p>
 * The directories that are expected to contain templates can be configured using the {@value #TEMPLATE_DIRECTORIES}
 * parameter. At least one directory should be provided.
 */
public class FileSystemTemplateFinder extends AbstractTemplateFinder {
	private String[] templateDirectories;

	/**
	 * Creates a file system template finder.
	 */
	public FileSystemTemplateFinder() {}

	@Override
	public void initialise(Configuration configuration) throws AluminumException {
		templateDirectories = configuration.getParameters().getValues(TEMPLATE_DIRECTORIES);

		if (templateDirectories.length == 0) {
			throw new AluminumException("please provide one or more template directories");
		} else {
			logger.debug("using template directories ", templateDirectories);
		}
	}

	public InputStream find(String name) throws AluminumException {
		InputStream stream = null;

		for (String directory: templateDirectories) {
			logger.debug("trying to find template '", name, "' in directory '", directory, "'");

			File file = new File(directory, name);

			if (file.exists()) {
				logger.debug("found template '", name, "': ", file.getAbsolutePath());

				try {
					stream = new FileInputStream(file);
				} catch (FileNotFoundException exception) {
					throw new AluminumException("can't create file input stream for template '", name, "'");
				}
			}
		}

		if (stream == null) {
			throw new AluminumException("can't find template '", name, "' in ", templateDirectories);
		} else {
			return stream;
		}
	}

	/**
	 * The name of the configuration parameter that holds a comma-separated list of directories that can contain
	 * templates.
	 */
	public final static String TEMPLATE_DIRECTORIES = "template_finder.file_system.template_directories";
}