/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2020 wcm.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.junit.jupiter.params;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.util.ExceptionUtils;
import org.junit.platform.commons.util.Preconditions;

/**
 * <p>CombinatorialParameterizedTestExtension class.</p>
 *
 * @since 5.0
 */
public abstract class CombinatorialParameterizedTestExtension implements TestTemplateInvocationContextProvider {

  private static final String METHOD_CONTEXT_KEY = "context";

  /** {@inheritDoc} */
  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(
      ExtensionContext extensionContext) {

    Method templateMethod = extensionContext.getRequiredTestMethod();
    String displayName = extensionContext.getDisplayName();
    ParameterizedTestMethodContext methodContext = getStore(extensionContext)//
        .get(METHOD_CONTEXT_KEY, ParameterizedTestMethodContext.class);
    ParameterizedTestNameFormatter formatter = createNameFormatter(templateMethod, displayName);
    AtomicLong invocationCount = new AtomicLong(0);

    return provideArguments(extensionContext)
          .map(Arguments::get)
          .map(arguments -> consumedArguments(arguments, methodContext))
          .map(arguments -> createInvocationContext(formatter, methodContext, arguments))
          .peek(invocationContext -> invocationCount.incrementAndGet())
          .onClose(() ->
              Preconditions.condition(invocationCount.get() > 0,
                  "Configuration error: You must configure at least one set of arguments for this @ParameterizedTest"));
    }

  /** {@inheritDoc} */
  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    if (!context.getTestMethod().isPresent()) {
      return false;
    }

    Method testMethod = context.getTestMethod().get();
    if (!supportsTestMethod(testMethod)) {
      return false;
    }

    ParameterizedTestMethodContext methodContext = new ParameterizedTestMethodContext(testMethod);

    Preconditions.condition(methodContext.hasPotentiallyValidSignature(),
        () -> String.format(
            "@ParameterizedTest method [%s] declares formal parameters in an invalid order: "
                + "argument aggregators must be declared after any indexed arguments "
                + "and before any arguments resolved by another ParameterResolver.",
            testMethod.toGenericString()));

    getStore(context).put(METHOD_CONTEXT_KEY, methodContext);

    return true;
  }

  private Object[] consumedArguments(Object[] arguments, ParameterizedTestMethodContext methodContext) {
    int parameterCount = methodContext.getParameterCount();
    return methodContext.hasAggregator() ? arguments
        : (arguments.length > parameterCount ? Arrays.copyOf(arguments, parameterCount) : arguments);
  }

  private TestTemplateInvocationContext createInvocationContext(ParameterizedTestNameFormatter formatter,
      ParameterizedTestMethodContext methodContext, Object[] arguments) {
    return new ParameterizedTestInvocationContext(formatter, methodContext, arguments);
  }

  private ExtensionContext.Store getStore(ExtensionContext context) {
    return context.getStore(Namespace.create(this.getClass(), context.getRequiredTestMethod()));
  }

  protected List<List<? extends Arguments>> collectArguments(Collection<ArgumentsProvider> providers, ExtensionContext context) {
    List<List<? extends Arguments>> result = new ArrayList<List<? extends Arguments>>();
    for (ArgumentsProvider provider : providers) {
      result.add(arguments(provider, context));
    }
    return result;
  }

  protected abstract Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext);

  protected boolean supportsTestMethod(Method testMethod) {
    return AnnotationSupport.isAnnotated(testMethod, getAnnotationClass());
  }

  protected abstract Class<? extends Annotation> getAnnotationClass();

  private ParameterizedTestNameFormatter createNameFormatter(Method templateMethod, String displayName) {
    String pattern = getNamePattern(templateMethod);
    return new ParameterizedTestNameFormatter(pattern, displayName);
  }

  protected abstract String getNamePattern(Method templateMethod);

  protected static List<? extends Arguments> arguments(ArgumentsProvider provider, ExtensionContext context) {
    try {
      return provider
          .provideArguments(context)
          .collect(Collectors.toList());
    }
    catch (Exception e) {
      throw ExceptionUtils.throwAsUncheckedException(e);
    }
  }

  protected static Arguments flattenArgumentsList(List<Arguments> args) {
    return Arguments.of(listToArray(args));
  }

  protected static Object[] listToArray(List<Arguments> list) {
    Object[] listAsSingleArray = new Object[] {};
    for (Arguments args : list) {
      listAsSingleArray = ArrayUtils.addAll(listAsSingleArray, args.get());
    }
    return listAsSingleArray;
  }

}
