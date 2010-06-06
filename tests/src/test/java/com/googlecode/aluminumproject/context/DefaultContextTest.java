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
package com.googlecode.aluminumproject.context;

import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("all")
@Test(groups = {"core", "fast"})
public class DefaultContextTest {
	private Context context;

	@BeforeMethod
	public void createContext() {
		context = new DefaultContext();
	}

	public void contextShouldContainSingleScope() {
		Set<String> scopeNames = context.getScopeNames();
		assert scopeNames != null;
		assert scopeNames.size() == 1;
		assert scopeNames.contains(Context.TEMPLATE_SCOPE);
	}

	public void subcontextShouldBeCreatable() {
		Context subcontext = context.createSubcontext();
		assert subcontext != context;
		assert subcontext instanceof DefaultContext;
	}
}