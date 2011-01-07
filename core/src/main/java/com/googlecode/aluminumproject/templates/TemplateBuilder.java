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
package com.googlecode.aluminumproject.templates;

import com.googlecode.aluminumproject.context.Context;
import com.googlecode.aluminumproject.utilities.Utilities;
import com.googlecode.aluminumproject.writers.Writer;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builds a {@link Template template}.
 * <p>
 * A template builder works from a {@link TemplateElement template element}. Initially, this template element is {@code
 * null}, but {@link #addTemplateElement(TemplateElement) adding} a template element causes it to become the current
 * one. After adding children to that template element, the previous template element can be {@link
 * #restoreCurrentTemplateElement() made current again}.
 * <p>
 * When all template elements have been added to the template builder, a template can be {@link #build() built}. After
 * that, the template builder should no longer be used.
 *
 * @author levi_h
 */
public class TemplateBuilder {
	private TemplateElements templateElements;
	private TemplateElement currentTemplateElement;

	private boolean built;

	/**
	 * Creates a template builder.
	 */
	public TemplateBuilder() {
		templateElements = new TemplateElements();
	}

	/**
	 * Adds a template element to the current template element and makes the new template element the current one.
	 *
	 * @param templateElement the template element to add
	 * @throws TemplateException when this template builder should no longer be used
	 */
	public void addTemplateElement(TemplateElement templateElement) throws TemplateException {
		ensureNotBuilt();

		templateElements.addTemplateElement(currentTemplateElement, templateElement);

		currentTemplateElement = templateElement;
	}

	/**
	 * Makes the template element that was the current one before the current template element current again.
	 *
	 * @throws TemplateException when there is no previous template element to restore or when this template builder
	 *                           should no longer be used
	 */
	public void restoreCurrentTemplateElement() throws TemplateException {
		ensureNotBuilt();

		currentTemplateElement = templateElements.getParent(currentTemplateElement);
	}

	/**
	 * Builds a template from the added template elements.
	 *
	 * @return a template that contains the template elements that were added to this builder
	 */
	public Template build() {
		ensureNotBuilt();

		built = true;

		return new BuiltTemplate(templateElements);
	}

	private void ensureNotBuilt() throws TemplateException {
		if (built) {
			throw new TemplateException("can't operate on builder - a template has already been built");
		}
	}

	/**
	 * Contains the template elements that were added to the {@link TemplateBuilder template builder}.
	 *
	 * @author levi_h
	 */
	private static class TemplateElements {
		private Map<TemplateElement, List<TemplateElement>> templateElements;

		/**
		 * Creates template elements.
		 */
		public TemplateElements() {
			templateElements = new LinkedHashMap<TemplateElement, List<TemplateElement>>();

			addTemplateElements(null);
		}

		/**
		 * Adds a template element to another template element.
		 *
		 * @param parentTemplateElement the template element that's the parent of the added template element
		 * @param templateElement the template element to add
		 */
		public void addTemplateElement(TemplateElement parentTemplateElement, TemplateElement templateElement) {
			templateElements.get(parentTemplateElement).add(templateElement);

			addTemplateElements(templateElement);
		}

		private void addTemplateElements(TemplateElement parentTemplateElement) {
			templateElements.put(parentTemplateElement, new LinkedList<TemplateElement>());
		}

		/**
		 * Finds the parent of a template element.
		 *
		 * @param templateElement the template element to find the parent of
		 * @return the template element that's the parent of the given template element
		 * @throws TemplateException when the template element has not been added
		 */
		public TemplateElement getParent(TemplateElement templateElement) throws TemplateException {
			Iterator<Map.Entry<TemplateElement, List<TemplateElement>>> it = templateElements.entrySet().iterator();

			TemplateElement parentTemplateElement = null;
			boolean parentTemplateElementFound = false;

			while (it.hasNext() && !parentTemplateElementFound) {
				Map.Entry<TemplateElement, List<TemplateElement>> templateElementWithChildren = it.next();

				if (parentTemplateElementFound = templateElementWithChildren.getValue().contains(templateElement)) {
					parentTemplateElement = templateElementWithChildren.getKey();
				}
			}

			if (parentTemplateElementFound) {
				return parentTemplateElement;
			} else {
				throw new TemplateException("element ", templateElement, " could not be found");
			}
		}

		/**
		 * Finds the children of a template element.
		 *
		 * @param templateElement the template element to find the children of
		 * @return the template elements that have the given template element as their parent
		 * @throws TemplateException when the template element has not been added
		 */
		public List<TemplateElement> getChildren(TemplateElement templateElement) throws TemplateException {
			if (templateElements.containsKey(templateElement)) {
				return templateElements.get(templateElement);
			} else {
				throw new TemplateException("element ", templateElement, " could not be found");
			}
		}
	}

	/**
	 * The template that is built by the {@link TemplateBuilder template builder}.
	 *
	 * @author levi_h
	 */
	private static class BuiltTemplate implements Template {
		private TemplateElements templateElements;

		/**
		 * Creates a built template.
		 *
		 * @param templateElements the template elements that the template consists of
		 */
		public BuiltTemplate(TemplateElements templateElements) {
			this.templateElements = templateElements;
		}

		public void processChildren(
				TemplateContext templateContext, Context context, Writer writer) throws TemplateException {
			for (TemplateElement templateElement: getChildren(templateContext.getCurrentTemplateElement())) {
				templateContext.addTemplateElement(templateElement);

				Map<String, Object> internalInformation =
					Utilities.typed(context.getImplicitObject(Context.ALUMINUM_IMPLICIT_OBJECT));
				Object previousTemplate = internalInformation.put(TEMPLATE_KEY, this);
				Object previousTemplateContext = internalInformation.put(TEMPLATE_CONTEXT_KEY, templateContext);

				templateElement.process(this, templateContext, context, writer);

				restoreValue(internalInformation, TEMPLATE_KEY, previousTemplate);
				restoreValue(internalInformation, TEMPLATE_CONTEXT_KEY, previousTemplateContext);

				templateContext.removeCurrentTemplateElement();
			}
		}

		private void restoreValue(Map<String, Object> internalInformation, String key, Object previousValue) {
			if (previousValue == null) {
				internalInformation.remove(key);
			} else {
				internalInformation.put(key, previousValue);
			}
		}

		public TemplateElement getParent(TemplateElement templateElement) throws TemplateException {
			return templateElements.getParent(templateElement);
		}

		public List<TemplateElement> getChildren(TemplateElement templateElement) throws TemplateException {
			return templateElements.getChildren(templateElement);
		}
	}
}