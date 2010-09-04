/*
 * Copyright 2010 Levi Hoogenberg
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
package com.googlecode.aluminumproject.libraries.xml.actions;

import com.googlecode.aluminumproject.libraries.xml.model.Element;
import com.googlecode.aluminumproject.writers.Writer;
import com.googlecode.aluminumproject.writers.WriterException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import nu.xom.Document;
import nu.xom.Serializer;

/**
 * An element that wraps a XOM element.
 *
 * @author levi_h
 */
class XomElement implements Element {
	private nu.xom.Element element;

	/**
	 * Creates a XOM element.
	 *
	 * @param element the wrapped element
	 */
	public XomElement(nu.xom.Element element) {
		this.element = element;
	}

	public void writeDocument(Writer writer, int indentation) throws WriterException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		Serializer serializer = new Serializer(out);
		serializer.setLineSeparator("\n");
		serializer.setIndent(indentation);

		try {
			serializer.write(new Document(element));
		} catch (IOException exception) {
			throw new WriterException(exception, "can't write document");
		}

		writer.write(new String(out.toByteArray()).trim());
	}
}