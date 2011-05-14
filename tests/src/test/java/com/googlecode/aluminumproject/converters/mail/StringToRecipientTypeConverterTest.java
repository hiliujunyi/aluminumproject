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
package com.googlecode.aluminumproject.converters.mail;

import com.googlecode.aluminumproject.context.Context;
import com.googlecode.aluminumproject.context.DefaultContext;
import com.googlecode.aluminumproject.converters.Converter;
import com.googlecode.aluminumproject.converters.ConverterException;

import javax.mail.Message.RecipientType;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("all")
@Test(groups = {"libraries", "libraries-mail", "fast"})
public class StringToRecipientTypeConverterTest {
	private Converter<String> converter;

	private Context context;

	@BeforeMethod
	public void createConverterAndContext() {
		converter = new StringToRecipientTypeConverter();

		context = new DefaultContext();
	}

	public void toTypeShouldBeConvertible() {
		assert converter.convert("to", RecipientType.class, context) == RecipientType.TO;
	}

	public void carbonCopyTypeShouldBeConvertible() {
		assert converter.convert("cc", RecipientType.class, context) == RecipientType.CC;
	}

	public void blindCarbonCopyTypeShouldBeConvertible() {
		assert converter.convert("bcc", RecipientType.class, context) == RecipientType.BCC;
	}

	public void newsgroupsTypeShouldBeConvertible() {
		Object recipientType = converter.convert("newsgroups", RecipientType.class, context);
		assert recipientType == javax.mail.internet.MimeMessage.RecipientType.NEWSGROUPS;
	}

	@Test(dependsOnMethods = "toTypeShouldBeConvertible")
	public void converterShouldBeCaseInsensitive() {
		assert converter.convert("TO", RecipientType.class, context) == RecipientType.TO;
	}

	@Test(expectedExceptions = ConverterException.class)
	public void tryingToConvertUnknownTypeShouldCauseException() {
		converter.convert("copy", RecipientType.class, context);
	}
}