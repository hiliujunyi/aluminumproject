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
package com.googlecode.aluminumproject.serialisers;

import com.googlecode.aluminumproject.configuration.ConfigurationElement;
import com.googlecode.aluminumproject.parsers.Parser;
import com.googlecode.aluminumproject.templates.Template;

/**
 * Serialises a {@link Template template}. A serialiser is the inverse of a {@link Parser parser}.
 *
 * @author levi_h
 */
public interface Serialiser extends ConfigurationElement {
	/**
	 * Serialises a template.
	 *
	 * @param template the template to serialise
	 * @param name the name to use for the template
	 * @throws SerialisationException when the template can't be serialised
	 */
	void serialiseTemplate(Template template, String name) throws SerialisationException;
}