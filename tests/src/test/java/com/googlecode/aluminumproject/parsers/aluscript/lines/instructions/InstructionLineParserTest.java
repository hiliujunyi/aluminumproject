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
package com.googlecode.aluminumproject.parsers.aluscript.lines.instructions;

import com.googlecode.aluminumproject.libraries.actions.ActionContributionFactory;
import com.googlecode.aluminumproject.libraries.actions.ActionParameter;
import com.googlecode.aluminumproject.libraries.actions.ConstantActionParameter;
import com.googlecode.aluminumproject.libraries.actions.ExpressionActionParameter;
import com.googlecode.aluminumproject.libraries.test.actions.TestActionContributionFactory;
import com.googlecode.aluminumproject.parsers.aluscript.AluScriptException;
import com.googlecode.aluminumproject.parsers.aluscript.instructions.Instruction;
import com.googlecode.aluminumproject.parsers.aluscript.instructions.test.TestInstruction;
import com.googlecode.aluminumproject.parsers.aluscript.lines.LineParser;
import com.googlecode.aluminumproject.parsers.aluscript.test.TestAluScriptContext;
import com.googlecode.aluminumproject.templates.ActionElement;
import com.googlecode.aluminumproject.templates.TemplateElement;

import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("all")
@Test(groups = {"parsers", "parsers-aluscript", "fast"})
public class InstructionLineParserTest {
	private LineParser lineParser;

	private TestAluScriptContext context;

	@BeforeMethod
	public void createLineParser() {
		lineParser = new InstructionLineParser();

		context = new TestAluScriptContext();
		context.addLibraryUrlAbbreviation("test", "http://aluminumproject.googlecode.com/test");
	}

	public void linesThatStartWithAtSymbolShouldBeHandled() {
		assert lineParser.handles("@c.template");
	}

	public void indentedLinesThatStartWithAtSymbolShouldBeHandled() {
		assert lineParser.handles("\t@c.template");
	}

	public void linesThatDoNotStartWithAtSymbolShouldNotBeHandled() {
		assert !lineParser.handles("c.template");
	}

	public void parsingLineWithInstructionShouldExecuteIt() {
		lineParser.parseLine("@test", context);

		Instruction instruction = context.findInstruction("test");
		assert instruction instanceof TestInstruction;
		assert ((TestInstruction) instruction).isExecuted();
	}

	public void parsingLineWithInstructionThatContainsDotShouldAddActionElementToContext() {
		lineParser.parseLine("@test.test", context);

		List<TemplateElement> templateElements = context.getTemplateElements();
		assert templateElements.size() == 1;
		assert templateElements.get(0) instanceof ActionElement;
	}

	@Test(dependsOnMethods = "parsingLineWithInstructionThatContainsDotShouldAddActionElementToContext")
	public void parsingActionLineWithParameterShouldAddParameterToActionElement() {
		lineParser.parseLine("@test.test(description: 'test')", context);

		Map<String, ActionParameter> parameters =
			((ActionElement) context.getTemplateElements().get(0)).getParameters();
		assert parameters != null;
		assert parameters.size() == 1;
		assert parameters.containsKey("description");
		assert parameters.get("description") instanceof ConstantActionParameter;
	}

	@Test(dependsOnMethods = "parsingLineWithInstructionThatContainsDotShouldAddActionElementToContext")
	public void parsingActionLineWithParameterThatContainsDotShouldAddContributionToActionElement() {
		lineParser.parseLine("@test.test(test.test: [test])", context);

		Map<ActionContributionFactory, ActionParameter> contributionFactories =
			((ActionElement) context.getTemplateElements().get(0)).getContributionFactories();
		assert contributionFactories != null;
		assert contributionFactories.size() == 1;
		assert contributionFactories.keySet().iterator().next() instanceof TestActionContributionFactory;
		assert contributionFactories.values().iterator().next() instanceof ExpressionActionParameter;
	}

	@Test(expectedExceptions = AluScriptException.class)
	public void parsingMalformedLineShouldCauseException() {
		lineParser.parseLine("@test(", context);
	}

	@Test(expectedExceptions = AluScriptException.class)
	public void supplyingDuplicateParameterShouldCauseException() {
		lineParser.parseLine("@test.test(description: 'test', description: test)", context);
	}
}