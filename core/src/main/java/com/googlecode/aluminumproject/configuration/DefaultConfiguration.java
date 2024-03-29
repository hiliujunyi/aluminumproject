/*
 * Copyright 2009-2012 Aluminum project
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
package com.googlecode.aluminumproject.configuration;

import static com.googlecode.aluminumproject.utilities.ReflectionUtilities.getPackageName;
import static com.googlecode.aluminumproject.utilities.ReflectionUtilities.instantiate;
import static com.googlecode.aluminumproject.utilities.ReflectionUtilities.isAbstract;

import com.googlecode.aluminumproject.AluminumException;
import com.googlecode.aluminumproject.annotations.Ignored;
import com.googlecode.aluminumproject.annotations.Named;
import com.googlecode.aluminumproject.cache.Cache;
import com.googlecode.aluminumproject.context.ContextEnricher;
import com.googlecode.aluminumproject.converters.ConverterRegistry;
import com.googlecode.aluminumproject.converters.DefaultConverterRegistry;
import com.googlecode.aluminumproject.expressions.ExpressionFactory;
import com.googlecode.aluminumproject.finders.ClassPathTemplateFinder;
import com.googlecode.aluminumproject.finders.DefaultTypeFinder;
import com.googlecode.aluminumproject.finders.InMemoryTemplateStoreFinder;
import com.googlecode.aluminumproject.finders.TemplateFinder;
import com.googlecode.aluminumproject.finders.TemplateStoreFinder;
import com.googlecode.aluminumproject.finders.TypeFinder;
import com.googlecode.aluminumproject.finders.TypeFinder.TypeFilter;
import com.googlecode.aluminumproject.libraries.Library;
import com.googlecode.aluminumproject.parsers.Parser;
import com.googlecode.aluminumproject.serialisers.Serialiser;
import com.googlecode.aluminumproject.templates.DefaultTemplateElementFactory;
import com.googlecode.aluminumproject.templates.TemplateElementFactory;
import com.googlecode.aluminumproject.utilities.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A default configuration. It is created with a number of initialisation parameters, which determines which {@link
 * ConfigurationElement configuration elements} will be used.
 * <p>
 * The type finder can be configured through the configuration parameter named {@value #TYPE_FINDER_CLASS}, which is
 * expected to hold the fully qualified name of the type finder class. If the parameter is not supplied, a {@link
 * DefaultTypeFinder default type finder} will be used instead.
 * <p>
 * The other configuration elements will be created using the {@link DefaultConfigurationElementFactory default
 * configuration element factory}, although a different implementation can be used by either providing a value for the
 * {@value #CONFIGURATION_ELEMENT_FACTORY_CLASS} parameter or overriding {@link #createConfigurationElementFactory() the
 * createConfigurationElementFactory method}.
 * <p>
 * For the converter registry, the configuration will look for a parameter with the name {@value
 * #CONVERTER_REGISTRY_CLASS}. If the parameter exists, its value will be interpreted as the class name of the converter
 * registry. If the parameter is absent, the converter registry will default to a {@link DefaultConverterRegistry
 * default converter registry}.
 * <p>
 * The template element factory is configured in the same way - if a configuration parameter with {@value
 * #TEMPLATE_ELEMENT_FACTORY_CLASS} as name is present, its value will be used as template element factory class name.
 * If the parameter is not given, a {@link DefaultTemplateElementFactory default template element factory} will be used.
 * <p>
 * The default template finder and template store finder are a {@link ClassPathTemplateFinder class path template
 * finder} and an {@link InMemoryTemplateStoreFinder in-memory template store finder}, respectively; other
 * implementations can be configured through the {@value #TEMPLATE_FINDER_CLASS} and {@value
 * #TEMPLATE_STORE_FINDER_CLASS} parameters.
 * <p>
 * For the cache, the configuration will look for a parameter with the name {@value #CACHE_CLASS}. If the parameter
 * exists, its value will be interpreted as the class name of the cache. If the parameter does not exist, no cache will
 * be used.
 * <p>
 * All other configuration elements are found by scanning one ore more packages. For each configuration element type,
 * there is a default package in which configuration elements will be looked for. These locations can be changed by
 * supplying configuration parameters that contain comma-separated lists of package names, both individually (the exact
 * configuration parameters can be found in the table below) and globally (using a configuration parameter named {@value
 * #CONFIGURATION_ELEMENT_PACKAGES}). Configuration elements that are found through a package scan are excluded from the
 * configuration when they are annotated with {@link Ignored &#64;Ignored}.
 * <table border="0">
 * <tr>
 * <th align="left">Configuration elements</th>
 * <th align="left">Default package</th>
 * <th align="left">Configuration parameter</th>
 * </tr>
 * <tr>
 * <td>Libraries</td>
 * <td>{@code com.googlecode.aluminumproject.libraries}</td>
 * <td>{@value #LIBRARY_PACKAGES}</td>
 * </tr>
 * <tr>
 * <td>Parsers</td>
 * <td>{@code com.googlecode.aluminumproject.parsers}</td>
 * <td>{@value #PARSER_PACKAGES}</td>
 * </tr>
 * <tr>
 * <td>Serialisers</td>
 * <td>{@code com.googlecode.aluminumproject.serialisers}</td>
 * <td>{@value #SERIALISER_PACKAGES}</td>
 * </tr>
 * <tr>
 * <td>Context enrichers</td>
 * <td>{@code com.googlecode.aluminumproject.context}</td>
 * <td>{@value #CONTEXT_ENRICHER_PACKAGES}</td>
 * </tr>
 * <tr>
 * <td>Expression factories</td>
 * <td>{@code com.googlecode.aluminumproject.expressions}</td>
 * <td>{@value #EXPRESSION_FACTORY_PACKAGES}</td>
 * </tr>
 * </table>
 * When a parser or serialiser is found, it is named after the last part of the package it's in: this means that a
 * parser named {@code com.googlecode.aluminumproject.parsers.simple.SimpleParser} or a serialiser named {@code
 * com.googlecode.aluminumproject.serialisers.simple.SimpleSerialiser} will be registered under the name {@code simple}.
 * This behaviour can be changed by annotating a parser or serialiser with {@link Named &#64;Named}.
 */
public class DefaultConfiguration implements Configuration {
	private ConfigurationParameters parameters;

	private TypeFinder typeFinder;
	private ConfigurationElementFactory configurationElementFactory;
	private ConverterRegistry converterRegistry;
	private TemplateElementFactory templateElementFactory;
	private TemplateFinder templateFinder;
	private TemplateStoreFinder templateStoreFinder;
	private Cache cache;
	private List<Library> libraries;
	private Map<String, Parser> parsers;
	private Map<String, Serialiser> serialisers;
	private List<ContextEnricher> contextEnrichers;
	private List<ExpressionFactory> expressionFactories;

	private boolean open;

	private final Logger logger;

	/**
	 * Creates a default configuration without any parameters.
	 *
	 * @throws AluminumException when the configuration can't be created
	 */
	public DefaultConfiguration() throws AluminumException {
		this(new ConfigurationParameters());
	}

	/**
	 * Creates a default configuration.
	 *
	 * @param parameters the configuration parameters to use
	 * @throws AluminumException when the configuration can't be created
	 */
	public DefaultConfiguration(ConfigurationParameters parameters) throws AluminumException {
		this.parameters = parameters;

		open = true;

		logger = Logger.get(getClass());

		createTypeFinder();
		typeFinder.initialise(this);

		configurationElementFactory = createConfigurationElementFactory();
		configurationElementFactory.initialise(this);

		createConverterRegistry();
		createTemplateElementFactory();
		createTemplateFinder();
		createTemplateStoreFinder();
		createCache();
		createLibraries();
		createParsers();
		createSerialisers();
		createContextEnrichers();
		createExpressionFactories();

		initialise();
	}

	private void createTypeFinder() throws AluminumException {
		String typeFinderClassName = parameters.getValue(TYPE_FINDER_CLASS, DefaultTypeFinder.class.getName());

		logger.debug("creating type finder of type ", typeFinderClassName);

		typeFinder = instantiate(typeFinderClassName, TypeFinder.class, Thread.currentThread().getContextClassLoader());
	}

	/**
	 * Creates the configuration element factory. This implementation instantiates the class that is configured in the
	 * configuration parameter named {@value #CONFIGURATION_ELEMENT_FACTORY_CLASS}, but subclasses may override this
	 * method if they want to use a configuration element factory that should not be constructed through reflection.
	 *
	 * @return the factory that should be used to create the elements in this configuration
	 * @throws AluminumException when the configuration element factory can't be created
	 */
	protected ConfigurationElementFactory createConfigurationElementFactory() throws AluminumException {
		String configurationElementFactoryClassName = parameters.getValue(
			CONFIGURATION_ELEMENT_FACTORY_CLASS, DefaultConfigurationElementFactory.class.getName());

		logger.debug("creating configuration element factory of type ", configurationElementFactoryClassName);

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		return instantiate(configurationElementFactoryClassName, ConfigurationElementFactory.class, classLoader);
	}

	private void createConverterRegistry() throws AluminumException {
		String converterRegistryClassName =
			parameters.getValue(CONVERTER_REGISTRY_CLASS, DefaultConverterRegistry.class.getName());

		logger.debug("creating converter registry of type ", converterRegistryClassName);

		converterRegistry =
			configurationElementFactory.instantiate(converterRegistryClassName, ConverterRegistry.class);
	}

	private void createTemplateElementFactory() throws AluminumException {
		String templateElementFactoryClassName =
			parameters.getValue(TEMPLATE_ELEMENT_FACTORY_CLASS, DefaultTemplateElementFactory.class.getName());

		logger.debug("creating template element factory of type ", templateElementFactoryClassName);

		templateElementFactory =
			configurationElementFactory.instantiate(templateElementFactoryClassName, TemplateElementFactory.class);
	}

	private void createTemplateFinder() throws AluminumException {
		String templateFinderClassName =
			parameters.getValue(TEMPLATE_FINDER_CLASS, ClassPathTemplateFinder.class.getName());

		logger.debug("creating template finder of type ", templateFinderClassName);

		templateFinder = configurationElementFactory.instantiate(templateFinderClassName, TemplateFinder.class);
	}

	private void createTemplateStoreFinder() throws AluminumException {
		String templateStoreFinderClassName =
			parameters.getValue(TEMPLATE_STORE_FINDER_CLASS, InMemoryTemplateStoreFinder.class.getName());

		logger.debug("creating template store finder of type ", templateStoreFinderClassName);

		templateStoreFinder =
			configurationElementFactory.instantiate(templateStoreFinderClassName, TemplateStoreFinder.class);
	}

	private void createCache() throws AluminumException {
		String cacheClassName = parameters.getValue(CACHE_CLASS, null);

		if (cacheClassName == null) {
			logger.debug("not using a cache");
		} else {
			logger.debug("creating cache of type ", cacheClassName);

			cache = configurationElementFactory.instantiate(cacheClassName, Cache.class);
		}
	}

	private void createLibraries() throws AluminumException {
		String[] libraryPackages = getConfiguredPackages(LIBRARY_PACKAGES, getPackageName(Library.class));

		logger.debug("libraries will be looked for in ", libraryPackages);

		List<Class<?>> libraryClasses = typeFinder.find(new TypeFilter() {
			public boolean accepts(Class<?> type) {
				return Library.class.isAssignableFrom(type) && !isAbstract(type)
					&& !type.isAnnotationPresent(Ignored.class);
			}
		}, libraryPackages);

		libraries = new ArrayList<Library>();

		for (Class<?> libraryClass: libraryClasses) {
			logger.debug("adding library of type ", libraryClass.getName());

			libraries.add(configurationElementFactory.instantiate(libraryClass.getName(), Library.class));
		}
	}

	private void createParsers() throws AluminumException {
		String[] parserPackages = getConfiguredPackages(PARSER_PACKAGES, getPackageName(Parser.class));

		logger.debug("parsers will be looked for in ", parserPackages);

		List<Class<?>> parserClasses = typeFinder.find(new TypeFilter() {
			public boolean accepts(Class<?> type) {
				return Parser.class.isAssignableFrom(type) && !isAbstract(type)
					&& !type.isAnnotationPresent(Ignored.class);
			}
		}, parserPackages);

		parsers = new HashMap<String, Parser>();

		for (Class<?> parserClass: parserClasses) {
			String name = getNameBasedOnAnnotationOrPackageName(parserClass);

			if (parsers.containsKey(name)) {
				throw new AluminumException("duplicate parser name: '", name, "' ",
					"(types: ", parsers.get(name).getClass().getName(), " and ", parserClass.getName(), ")");
			} else {
				logger.debug("registering parser of type ", parserClass.getName(), " under name '", name, "'");

				parsers.put(name, configurationElementFactory.instantiate(parserClass.getName(), Parser.class));
			}
		}
	}

	private void createSerialisers() throws AluminumException {
		String[] serialiserPackages = getConfiguredPackages(SERIALISER_PACKAGES, getPackageName(Serialiser.class));

		logger.debug("serialisers will be looked for in ", serialiserPackages);

		List<Class<?>> serialiserClasses = typeFinder.find(new TypeFilter() {
			public boolean accepts(Class<?> type) {
				return Serialiser.class.isAssignableFrom(type) && !isAbstract(type)
					&& !type.isAnnotationPresent(Ignored.class);
			}
		}, serialiserPackages);

		serialisers = new HashMap<String, Serialiser>();

		for (Class<?> serialiserClass: serialiserClasses) {
			String name = getNameBasedOnAnnotationOrPackageName(serialiserClass);

			if (serialisers.containsKey(name)) {
				throw new AluminumException("duplicate serialiser name: '", name, "' ",
					"(types: ", serialisers.get(name).getClass().getName(), " and ", serialiserClass.getName(), ")");
			} else {
				logger.debug("registering serialiser of type ", serialiserClass.getName(), " under name '", name, "'");

				serialisers.put(name,
					configurationElementFactory.instantiate(serialiserClass.getName(), Serialiser.class));
			}
		}
	}

	private String getNameBasedOnAnnotationOrPackageName(Class<?> type) {
		String name;

		if (type.isAnnotationPresent(Named.class)) {
			name = type.getAnnotation(Named.class).value();
		} else {
			String packageName = getPackageName(type);

			name = packageName.substring(packageName.lastIndexOf('.') + 1);
		}

		return name;
	}

	private void createContextEnrichers() throws AluminumException {
		String[] contextEnricherPackages =
			getConfiguredPackages(CONTEXT_ENRICHER_PACKAGES, getPackageName(ContextEnricher.class));

		List<Class<?>> contextEnricherClasses = typeFinder.find(new TypeFilter() {
			public boolean accepts(Class<?> type) {
				return ContextEnricher.class.isAssignableFrom(type) && !isAbstract(type)
					&& !type.isAnnotationPresent(Ignored.class);
			}
		}, contextEnricherPackages);

		contextEnrichers = new ArrayList<ContextEnricher>();

		for (Class<?> contextEnricherClass: contextEnricherClasses) {
			logger.debug("adding context enricher of type ", contextEnricherClass.getName());

			contextEnrichers.add(
				configurationElementFactory.instantiate(contextEnricherClass.getName(), ContextEnricher.class));
		}
	}

	private void createExpressionFactories() throws AluminumException {
		String[] expressionFactoryPackages =
			getConfiguredPackages(EXPRESSION_FACTORY_PACKAGES, getPackageName(ExpressionFactory.class));

		List<Class<?>> expressionFactoryClasses = typeFinder.find(new TypeFilter() {
			public boolean accepts(Class<?> type) {
				return ExpressionFactory.class.isAssignableFrom(type) && !isAbstract(type)
					&& !type.isAnnotationPresent(Ignored.class);
			}
		}, expressionFactoryPackages);

		expressionFactories = new ArrayList<ExpressionFactory>();

		for (Class<?> expressionFactoryClass: expressionFactoryClasses) {
			logger.debug("adding expression factory of type ", expressionFactoryClass.getName());

			expressionFactories.add(
				configurationElementFactory.instantiate(expressionFactoryClass.getName(), ExpressionFactory.class));
		}
	}

	private String[] getConfiguredPackages(String parameterName, String... defaultPackages) {
		Set<String> packages = new HashSet<String>();

		Collections.addAll(packages, parameters.getValues(parameterName, defaultPackages));
		Collections.addAll(packages, parameters.getValues(CONFIGURATION_ELEMENT_PACKAGES));

		return packages.toArray(new String[packages.size()]);
	}

	private void initialise() throws AluminumException {
		converterRegistry.initialise(this);
		templateElementFactory.initialise(this);
		templateFinder.initialise(this);
		templateStoreFinder.initialise(this);

		if (cache != null) {
			cache.initialise(this);
		}

		for (Library library: libraries) {
			library.initialise(this);
		}

		for (Parser parser: parsers.values()) {
			parser.initialise(this);
		}

		for (Serialiser serialiser: serialisers.values()) {
			serialiser.initialise(this);
		}

		for (ContextEnricher contextEnricher: contextEnrichers) {
			contextEnricher.initialise(this);
		}

		for (ExpressionFactory expressionFactory: expressionFactories) {
			expressionFactory.initialise(this);
		}
	}

	public ConfigurationParameters getParameters() throws AluminumException {
		checkOpen();

		return parameters;
	}

	public TypeFinder getTypeFinder() throws AluminumException {
		checkOpen();

		return typeFinder;
	}

	public ConfigurationElementFactory getConfigurationElementFactory() throws AluminumException {
		checkOpen();

		return configurationElementFactory;
	}

	public ConverterRegistry getConverterRegistry() throws AluminumException {
		checkOpen();

		return converterRegistry;
	}

	public TemplateElementFactory getTemplateElementFactory() throws AluminumException {
		checkOpen();

		return templateElementFactory;
	}

	public TemplateFinder getTemplateFinder() throws AluminumException {
		checkOpen();

		return templateFinder;
	}

	public TemplateStoreFinder getTemplateStoreFinder() throws AluminumException {
		checkOpen();

		return templateStoreFinder;
	}

	public Cache getCache() throws AluminumException {
		checkOpen();

		return cache;
	}

	public List<Library> getLibraries() throws AluminumException {
		checkOpen();

		return Collections.unmodifiableList(libraries);
	}

	public Map<String, Parser> getParsers() throws AluminumException {
		checkOpen();

		return Collections.unmodifiableMap(parsers);
	}

	public Map<String, Serialiser> getSerialisers() throws AluminumException {
		checkOpen();

		return Collections.unmodifiableMap(serialisers);
	}

	public List<ContextEnricher> getContextEnrichers() throws AluminumException {
		checkOpen();

		return Collections.unmodifiableList(contextEnrichers);
	}

	public List<ExpressionFactory> getExpressionFactories() throws AluminumException {
		checkOpen();

		return Collections.unmodifiableList(expressionFactories);
	}

	public void close() throws AluminumException {
		checkOpen();

		typeFinder.disable();
		configurationElementFactory.disable();
		converterRegistry.disable();
		templateElementFactory.disable();
		templateFinder.disable();
		templateStoreFinder.disable();

		if (cache != null) {
			cache.disable();
		}

		for (Library library: libraries) {
			library.disable();
		}

		for (Parser parser: parsers.values()) {
			parser.disable();
		}

		for (Serialiser serialiser: serialisers.values()) {
			serialiser.disable();
		}

		for (ContextEnricher contextEnricher: contextEnrichers) {
			contextEnricher.disable();
		}

		for (ExpressionFactory expressionFactory: expressionFactories) {
			expressionFactory.disable();
		}

		open = false;
	}

	private void checkOpen() throws AluminumException {
		if (!open) {
			throw new AluminumException("the configuration can't be used - it has been closed");
		}
	}

	/**
	 * The name of the configuration parameter that contains the class name of the type finder to use.
	 */
	public final static String TYPE_FINDER_CLASS = "configuration.default.type_finder.class";

	/**
	 * The name of the configuration parameter that contains the class name of the configuration element factory to use.
	 */
	public final static String CONFIGURATION_ELEMENT_FACTORY_CLASS =
		"configuration.default.configuration_element_factory.class";

	/** The name of the configuration parameter that contains the class name of the converter registry to use. */
	public final static String CONVERTER_REGISTRY_CLASS = "configuration.default.converter_registry.class";

	/** The name of the configuration parameter that contains the class name of the template element factory to use. */
	public final static String TEMPLATE_ELEMENT_FACTORY_CLASS = "configuration.default.template_element_factory.class";

	/** The name of the configuration parameter that contains the class name of the template finder to use. */
	public final static String TEMPLATE_FINDER_CLASS = "configuration.default.template_finder.class";

	/** The name of the configuration parameter that contains the class name of the template store finder to use. */
	public final static String TEMPLATE_STORE_FINDER_CLASS = "configuration.default.template_store_finder.class";

	/** The name of the configuration parameter that contains the class name of the cache to use. */
	public final static String CACHE_CLASS = "configuration.default.cache.class";

	/**
	 * The name of the configuration parameter that holds the comma-separated list of packages that the configuration
	 * will scan for libraries.
	 */
	public final static String LIBRARY_PACKAGES = "configuration.default.library.packages";

	/**
	 * The name of the configuration parameter that contains a comma-separated list of packages in which should be
	 * looked for parsers.
	 */
	public final static String PARSER_PACKAGES = "configuration.default.parser.packages";

	/**
	 * The name of the configuration parameter that holds a comma-separated list of packages that may contain
	 * serialisers.
	 */
	public final static String SERIALISER_PACKAGES = "configuration.default.serialiser.packages";

 	/**
	 * The name of the configuration parameter that contains a comma-separated list of packages in which context
	 * enrichers may be expected.
	 */
	public final static String CONTEXT_ENRICHER_PACKAGES = "configuration.default.context_enricher.packages";

	/**
	 * The name of the configuration parameter that has a comma-separated list of expression factory packages as its
	 * value.
	 */
	public final static String EXPRESSION_FACTORY_PACKAGES = "configuration.default.expression_factory.packages";

	/**
	 * The name of the configuration parameter that contains a comma-separated list of packages that might contain
	 * configuration elements. Classes other than the default configuration that perform package scans are encourage to
	 * support this configuration parameter as well.
	 */
	public final static String CONFIGURATION_ELEMENT_PACKAGES = "configuration.default.configuration_element.packages";
}