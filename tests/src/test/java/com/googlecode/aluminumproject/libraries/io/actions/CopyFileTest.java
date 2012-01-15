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
package com.googlecode.aluminumproject.libraries.io.actions;

import static com.googlecode.aluminumproject.libraries.io.functions.Directories.temporaryDirectory;

import com.googlecode.aluminumproject.AluminumException;
import com.googlecode.aluminumproject.context.Context;
import com.googlecode.aluminumproject.context.DefaultContext;
import com.googlecode.aluminumproject.libraries.io.IoLibraryTest;

import java.io.File;
import java.io.IOException;

import org.testng.annotations.Test;

@SuppressWarnings("javadoc")
@Test(groups = {"libraries", "libraries-io", "slow"})
public class CopyFileTest extends IoLibraryTest {
	public void fileShouldBeCopyable() throws IOException {
		File sourceFile = createTemporaryFile(3);
		File targetDirectory = createTemporaryDirectory();
		File targetFile = new File(targetDirectory, sourceFile.getName());

		assert !targetFile.exists();

		Context context = new DefaultContext();
		context.setVariable("source", sourceFile);
		context.setVariable("targetDirectory", targetDirectory);

		processTemplate("copy-file", context);

		assert targetFile.exists();
		assert readLines(sourceFile).equals(readLines(targetFile));
	}

	@Test(expectedExceptions = AluminumException.class)
	public void copyingDirectoryShouldCauseException() throws IOException {
		File sourceDirectory = createTemporaryDirectory();
		File targetDirectory = createTemporaryDirectory();

		Context context = new DefaultContext();
		context.setVariable("source", sourceDirectory);
		context.setVariable("targetDirectory", targetDirectory);

		processTemplate("copy-file", context);
	}

	@Test(expectedExceptions = AluminumException.class)
	public void copyingNonexistentFileShouldCauseException() throws IOException {
		File sourceFile = new File(temporaryDirectory(), generateUniqueName(temporaryDirectory()));

		assert !sourceFile.exists();

		Context context = new DefaultContext();
		context.setVariable("source", sourceFile);
		context.setVariable("targetDirectory", createTemporaryDirectory());

		processTemplate("copy-file", context);
	}

	@Test(dependsOnMethods = "fileShouldBeCopyable", expectedExceptions = AluminumException.class)
	public void copyingFileToDirectoryThatContainsFileWithSameNameShouldCauseException() throws IOException {
		File sourceFile = createTemporaryFile(5);
		File targetDirectory = createTemporaryDirectory();

		Context context = new DefaultContext();
		context.setVariable("source", sourceFile);
		context.setVariable("targetDirectory", targetDirectory);

		processTemplate("copy-file", context);
		processTemplate("copy-file", context);
	}

	@Test(expectedExceptions = AluminumException.class)
	public void copyingFileToNonexistentDirectoryShouldCauseException() throws IOException {
		File sourceFile = createTemporaryFile(10);
		File targetDirectory = new File(temporaryDirectory(), generateUniqueName(temporaryDirectory()));

		assert !targetDirectory.exists();

		Context context = new DefaultContext();
		context.setVariable("source", sourceFile);
		context.setVariable("targetDirectory", targetDirectory);

		processTemplate("copy-file", context);
	}
}