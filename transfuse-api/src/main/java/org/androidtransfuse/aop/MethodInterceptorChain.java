/**
 * Copyright 2013 John Ericksen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.androidtransfuse.aop;


import org.androidtransfuse.util.TransfuseInjectionException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

/**
 * Defines a chain of interceptors to wrap the given method.
 *
 * @author John Ericksen
 */
public class MethodInterceptorChain {

    private final MethodInterceptor[] methodInterceptors;
    private final MethodExecution methodExecution;
    private final Object proxy;

    public MethodInterceptorChain(MethodExecution methodExecution, Object proxy, MethodInterceptor... methodInterceptorChains) {
        this.methodExecution = methodExecution;
        this.methodInterceptors = methodInterceptorChains;
        this.proxy = proxy;
    }

    /**
     * Invoke the method interception chain.
     *
     * @param arguments provided to the wrapped method.
     * @return value returned by interceptor chain.
     */
    public Object invoke(Object[] arguments) {
        try {
            return new MethodInterceptorIterator(arguments).proceed();
        } catch (Throwable e) {
            throw new TransfuseInjectionException("Error while invoking Method Interceptor", e);
        }
    }

    /**
     * Class which encapsulates the iteration of the MethodInterceptors and final call to the MethodExecution instance.
     */
    private final class MethodInterceptorIterator implements MethodInvocation {

        private int i = -1;
        private final Object[] arguments;

        private MethodInterceptorIterator(Object[] arguments) {
            this.arguments = arguments;
        }

        @Override
        public Method getMethod() {
            try {
                return methodExecution.getMethod();
            } catch (Exception e) {
                throw new TransfuseInjectionException("Error while calling getMethod", e);
            }
        }

        @Override
        public Object[] getArguments() {
            return arguments;
        }

        @Override
        public Object proceed() throws Throwable {
            //recursively iterate through the method interceptors
            i++;
            if (i == methodInterceptors.length) {
                return methodExecution.invoke();
            } else {
                return methodInterceptors[i].invoke(this);
            }
        }

        @Override
        public Object getThis() {
            return proxy;
        }

        @Override
        public AccessibleObject getStaticPart() {
            return getMethod();
        }
    }

    /**
     * Interface defining the {@code Method} to be invoked.
     */
    public interface MethodExecution {

        String GET_METHOD = "getMethod";
        String INVOKE = "invoke";

        /**
         * Looks up the represented class {@code Method}.
         * @return Method
         * @throws Exception if an error occurs
         */
        Method getMethod() throws Exception;

        /**
         * Invokes the represented {@code Method}.
         * @return value returned by the method
         * @throws Throwable if an error occurs
         */
        Object invoke() throws Throwable;
    }
} 