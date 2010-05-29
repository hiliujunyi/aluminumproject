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
package com.googlecode.aluminumproject.libraries.text.actions;

import com.googlecode.aluminumproject.context.Context;
import com.googlecode.aluminumproject.context.DefaultContext;
import com.googlecode.aluminumproject.libraries.text.TextLibraryTest;

import org.testng.annotations.Test;

@SuppressWarnings("all")
@Test(groups = {"libraries", "libraries-text", "slow"})
public class TrimTest extends TextLibraryTest {
	public void textShouldBeTrimmable() {
		String output = processTemplate("trim", new DefaultContext());
		assert output != null;
		assert output.equals("*");
	}

	public void variableShouldBeTrimmable() {
		Context context = new DefaultContext();

		processTemplate("trim-variable", context);

		assert context.getVariableNames(Context.TEMPLATE_SCOPE).contains("text");

		Object text = context.getVariable(Context.TEMPLATE_SCOPE, "text");
		assert text != null;
		assert text.equals("*");
	}

	public void linesShouldBeTrimmable() {
		String output = processTemplate("trim-lines", new DefaultContext());
		assert output != null;
		assert output.equals("\n*\n*\n");
	}

	public void trimModesShouldBeCombinable() {
		String output = processTemplate("trim-combined", new DefaultContext());
		assert output != null;
		assert output.equals("*\n*");
	}
}