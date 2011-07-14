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
package com.googlecode.aluminumproject.libraries.scripting.actions;

import com.googlecode.aluminumproject.annotations.Required;
import com.googlecode.aluminumproject.context.Context;
import com.googlecode.aluminumproject.context.ContextException;
import com.googlecode.aluminumproject.libraries.actions.AbstractAction;
import com.googlecode.aluminumproject.libraries.actions.ActionException;
import com.googlecode.aluminumproject.writers.Writer;
import com.googlecode.aluminumproject.writers.WriterException;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

/**
 * Evaluates a script.
 *
 * @author levi_h
 */
public class Script extends AbstractAction {
	private @Required String type;

	/**
	 * Creates a script.
	 */
	public Script() {}

	public void execute(Context context, Writer writer) throws ActionException, WriterException {
		ScriptEngineManager engineManager = new ScriptEngineManager();
		ScriptEngine engine = engineManager.getEngineByMimeType(type);

		if (engine == null) {
			List<String> availableTypes = new LinkedList<String>();

			for (ScriptEngineFactory engineFactory: engineManager.getEngineFactories()) {
				availableTypes.addAll(engineFactory.getMimeTypes());
			}

			throw new ActionException("unsupported script type: '", type, "', available types are ", availableTypes);
		} else {
			ScriptContext scriptContext = createScriptContext(engine, context, writer);

			try {
				engine.eval(getBodyText(context, writer), scriptContext);
			} catch (ScriptException exception) {
				throw new ActionException(exception, "can't evaluate script");
			}

			synchroniseVariables(scriptContext.getBindings(ScriptContext.ENGINE_SCOPE), context);
		}
	}

	private ScriptContext createScriptContext(
			ScriptEngine engine, Context context, Writer writer) throws ContextException {
		Bindings bindings = engine.createBindings();
		synchroniseVariables(context, bindings);

		ScriptContext scriptContext = new SimpleScriptContext();
		scriptContext.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
		scriptContext.setWriter(new PrintWriter(new ScriptWriter(writer)));

		return scriptContext;
	}

	private void synchroniseVariables(Context context, Bindings bindings) throws ContextException {
		for (String name: context.getVariableNames(Context.TEMPLATE_SCOPE)) {
			bindings.put(name, context.getVariable(Context.TEMPLATE_SCOPE, name));
		}
	}

	private void synchroniseVariables(Bindings bindings, Context context) {
		for (String name: bindings.keySet()) {
			context.setVariable(name, bindings.get(name));
		}
	}
}