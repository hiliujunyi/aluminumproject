/*
 * Copyright 2010-2012 Levi Hoogenberg
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
package com.googlecode.aluminumproject.libraries.io.functions;

import com.googlecode.aluminumproject.context.Context;
import com.googlecode.aluminumproject.context.DefaultContext;
import com.googlecode.aluminumproject.libraries.io.IoLibraryTest;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.testng.annotations.Test;

@SuppressWarnings("javadoc")
@Test(groups = {"libraries", "libraries-io", "slow"})
public class WritersTest extends IoLibraryTest {
	public void linesShouldBeWrittenToFile() throws IOException {
		File file = createTemporaryFile();

		Context context = new DefaultContext();
		context.setVariable("file", file);

		processTemplate("write-file", context);

		List<String> lines = readLines(file);
		assert lines != null;
		assert lines.size() == 1;

		String firstLine = lines.get(0);
		assert firstLine != null;
		assert firstLine.equals("blue");
	}

	@Test(dependsOnMethods = "linesShouldBeWrittenToFile")
	public void linesShouldBeAppendedToFileInAppendMode() throws IOException {
		File file = createTemporaryFile();

		Context context = new DefaultContext();
		context.setVariable("file", file);

		processTemplate("write-file", context);
		processTemplate("write-file-in-append-mode", context);

		List<String> lines = readLines(file);
		assert lines != null;
		assert lines.size() == 1;

		String firstLine = lines.get(0);
		assert firstLine != null;
		assert firstLine.equals("blue sky");
	}

	public void fileContentsShouldBeReplacedWhenNotInAppendMode() throws IOException {
		File file = createTemporaryFile();

		Context context = new DefaultContext();
		context.setVariable("file", file);

		processTemplate("write-file", context);
		processTemplate("write-file-in-append-mode", context);
		processTemplate("write-file", context);

		List<String> lines = readLines(file);
		assert lines != null;
		assert lines.size() == 1;

		String firstLine = lines.get(0);
		assert firstLine != null;
		assert firstLine.equals("blue");
	}
}