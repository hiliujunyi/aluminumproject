/*
 * Copyright 2010-2012 Aluminum project
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
package com.googlecode.aluminumproject.libraries.xml;

import com.googlecode.aluminumproject.AluminumException;
import com.googlecode.aluminumproject.libraries.AbstractLibrary;
import com.googlecode.aluminumproject.libraries.LibraryInformation;
import com.googlecode.aluminumproject.libraries.actions.ActionContributionFactory;
import com.googlecode.aluminumproject.libraries.actions.ActionFactory;
import com.googlecode.aluminumproject.libraries.actions.DefaultActionContributionFactory;
import com.googlecode.aluminumproject.libraries.actions.DefaultActionFactory;
import com.googlecode.aluminumproject.libraries.xml.actions.DynamicAttribute;
import com.googlecode.aluminumproject.libraries.xml.actions.DynamicElement;
import com.googlecode.aluminumproject.utilities.ReflectionUtilities;
import com.googlecode.aluminumproject.utilities.environment.EnvironmentUtilities;

@SuppressWarnings("javadoc")
public class XmlLibrary extends AbstractLibrary {
	private LibraryInformation information;

	public XmlLibrary() {
		super(ReflectionUtilities.getPackageName(XmlLibrary.class));

		information = new LibraryInformation(URL, "x", EnvironmentUtilities.getVersion(), true, true, false);
	}

	public LibraryInformation getInformation() {
		return information;
	}

	@Override
	public ActionFactory getDynamicActionFactory(String name) throws AluminumException {
		ActionFactory actionFactory = new DefaultActionFactory(DynamicElement.class);
		initialiseLibraryElement(actionFactory);
		return actionFactory;
	}

	@Override
	public ActionContributionFactory getDynamicActionContributionFactory(String name) throws AluminumException {
		ActionContributionFactory actionContributionFactory =
			new DefaultActionContributionFactory(DynamicAttribute.class);
		initialiseLibraryElement(actionContributionFactory);
		return actionContributionFactory;
	}

	public final static String URL = "http://aluminumproject.googlecode.com/xml";
}