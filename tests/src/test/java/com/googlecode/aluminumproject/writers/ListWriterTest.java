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
package com.googlecode.aluminumproject.writers;

import java.util.List;

import org.testng.annotations.Test;

@SuppressWarnings("all")
@Test(groups = {"core", "fast"})
public class ListWriterTest {
	public void initialListShouldBeEmpty() {
		ListWriter writer = new ListWriter();

		List<?> list = writer.getList();
		assert list != null;
		assert list.isEmpty();
	}

	@Test(dependsOnMethods = "initialListShouldBeEmpty")
	public void listShouldNotContainWrittenObjectWithoutFlushing() {
		ListWriter writer = new ListWriter();

		writer.write(10);

		assert writer.getList().isEmpty();
	}

	@Test(dependsOnMethods = "listShouldNotContainWrittenObjectWithoutFlushing")
	public void listShouldContainWrittenObjectAfterFlushing() {
		ListWriter writer = new ListWriter();

		writer.write(10);
		writer.flush();

		List<?> list = writer.getList();
		assert list.size() == 1;
		assert list.contains(10);
	}

	public void listShouldContainWrittenObjectsWhenAutoFlushing() {
		ListWriter writer = new ListWriter(true);

		writer.write("a");
		writer.write("b");

		List<?> list = writer.getList();
		assert list.size() == 2;

		Object firstObject = list.get(0);
		assert firstObject != null;
		assert firstObject.equals("a");

		Object secondObject = list.get(1);
		assert secondObject != null;
		assert secondObject.equals("b");
	}

	@Test(dependsOnMethods = "listShouldContainWrittenObjectAfterFlushing")
	public void clearingListShouldRemoveWrittenObjects() {
		ListWriter writer = new ListWriter();

		writer.write("removed");
		writer.clear();
		writer.flush();

		assert writer.getList().isEmpty();
	}

	@Test(dependsOnMethods = "clearingListShouldRemoveWrittenObjects")
	public void clearingListShouldNotRemoveAlreadyFlushedObjects() {
		ListWriter writer = new ListWriter();

		writer.write("flushed");
		writer.flush();

		writer.write("removed");
		writer.clear();
		writer.flush();

		List<?> list = writer.getList();
		assert list.size() == 1;
		assert list.contains("flushed");
	}
}