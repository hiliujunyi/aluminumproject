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
package com.googlecode.aluminumproject.annotations;

import com.googlecode.aluminumproject.libraries.actions.ActionContribution;
import com.googlecode.aluminumproject.libraries.actions.DefaultActionContributionFactory;
import com.googlecode.aluminumproject.utilities.GenericsUtilities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Type;

/**
 * Contains information about an {@link ActionContribution action contribution}. It will be used by the {@link
 * DefaultActionContributionFactory default action contribution factory} to complete {@link
 * com.googlecode.aluminumproject.libraries.actions.ActionContributionInformation action contribution information} when
 * it's applied to an action contribution class.
 *
 * @author levi_h
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ActionContributionInformation {
	/**
	 * The name of the action contribution.
	 */
	String name() default "";

	/**
	 * The type of the action contribution's parameter.
	 * <p>
	 * Note that the type of this attribute is {@code String}, since {@link Type types} are not permitted. The attribute
	 * value will be converted to a type by {@link GenericsUtilities#getType(String, String...) the getType utility
	 * method} (with {@code java.lang} and {@code java.util} as default packages).
	 */
	String parameterType() default "Object";
}