/*
 * Copyright 2011 Levi Hoogenberg
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
package com.googlecode.aluminumproject.libraries.ds.actions;

import com.googlecode.aluminumproject.annotations.Required;
import com.googlecode.aluminumproject.context.Context;
import com.googlecode.aluminumproject.libraries.actions.AbstractAction;
import com.googlecode.aluminumproject.libraries.actions.ActionException;
import com.googlecode.aluminumproject.libraries.ds.functions.Comparators;
import com.googlecode.aluminumproject.utilities.UtilityException;
import com.googlecode.aluminumproject.writers.Writer;
import com.googlecode.aluminumproject.writers.WriterException;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Writes a {@link List list} that contains the sorted elements of an {@link Iterable iterable}.
 *
 * @author levi_h
 */
public class Sort extends AbstractAction {
	private @Required Iterable<?> elements;

	private Comparator<Object> comparator;

	/**
	 * Creates a <em>sort</em> action.
	 */
	public Sort() {}

	public void execute(Context context, Writer writer) throws ActionException, WriterException {
		List<Object> list = new LinkedList<Object>();

		for (Object element: elements) {
			list.add(element);
		}

		if (comparator == null) {
			comparator = Comparators.naturalOrder();
		}

		try {
			Collections.sort(list, comparator);
		} catch (UtilityException exception) {
			throw new ActionException(exception, "can't sort ", list, " ", comparator);
		}

		writer.write(list);
	}
}