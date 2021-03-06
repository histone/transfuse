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
package org.androidtransfuse.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Allows for multiple {@code @BindInterceptor} declarations to be provided on a {@code @TransfuseModule} class.</p>
 *
 * <p>Example:
 * <pre>
 *     {@code @TransfuseModule}
 *     {@literal @}BindInterceptors({
 *         {@code @BindInterceptor(Asynchronous.class, AsynchronousMethodInterceptor.class),}
 *         {@code @BindInterceptor(UIThread.class, UIThreadMethodInterceptor.class)}
 *     }
 *     public class Module{}
 * </pre>
 * </p>
 *
 * @author John Ericksen
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BindInterceptors {

    BindInterceptor[] value();
}
