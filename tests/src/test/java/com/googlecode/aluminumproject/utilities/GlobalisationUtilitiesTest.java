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
package com.googlecode.aluminumproject.utilities;

import com.googlecode.aluminumproject.AluminumException;

import java.util.Locale;
import java.util.TimeZone;

import org.testng.annotations.Test;

@SuppressWarnings("javadoc")
@Test(groups = {"libraries", "libraries-g11n", "fast"})
public class GlobalisationUtilitiesTest {
	public void languageShouldBeConvertibleIntoLocale() {
		Locale locale = GlobalisationUtilities.convertLocale("nl");
		assert locale != null;

		String language = locale.getLanguage();
		assert language != null;
		assert language.equals("nl");
	}

	public void languageAndCountryShouldBeConvertibleIntoLocale() {
		Locale locale = GlobalisationUtilities.convertLocale("nl_NL");
		assert locale != null;

		String language = locale.getLanguage();
		assert language != null;
		assert language.equals("nl");

		String country = locale.getCountry();
		assert country != null;
		assert country.equals("NL");
	}

	public void languageCountryAndVariantShouldBeConvertibleIntoLocale() {
		Locale locale = GlobalisationUtilities.convertLocale("nl_NL_Amsterdam");
		assert locale != null;

		String language = locale.getLanguage();
		assert language != null;
		assert language.equals("nl");

		String country = locale.getCountry();
		assert country != null;
		assert country.equals("NL");

		String variant = locale.getVariant();
		assert variant != null;
		assert variant.equals("Amsterdam");
	}

	@Test(expectedExceptions = AluminumException.class)
	public void convertingMoreThanThreeLocalePartsShouldCauseException() {
		GlobalisationUtilities.convertLocale("nl_NL_Amsterdam_West");
	}

	public void idShouldBeConvertibleIntoTimeZone() {
		TimeZone timeZone = GlobalisationUtilities.convertTimeZone("UTC");
		assert timeZone != null;
		assert timeZone.getID().equals("UTC");
	}

	public void timeZoneIdShouldBeCaseInsensitive() {
		TimeZone timeZone = GlobalisationUtilities.convertTimeZone("utc");
		assert timeZone != null;
		assert timeZone.getID().equals("UTC");
	}

	@Test(expectedExceptions = AluminumException.class)
	public void convertingNonexistentTimeZoneIdShouldCauseException() {
		GlobalisationUtilities.convertTimeZone("unknown");
	}
}