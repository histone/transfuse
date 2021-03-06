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

import javax.inject.Qualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Injection qualifier annotation specifying which view is injected by resource id or tag.</p>
 *
 * <p>
 * Injecting with this qualifier will lookup in the relevant Activity or Fragment class the view element using the
 * {@code getViewById()} or {@code getViewByTag()} methods.  Additionally, as long as the
 * type is a dependent of the Android {@code View} class, the injected instance will be cast to the given type.
 * </p>
 *
 * @author John Ericksen
 */
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface View {

    /**
     * Resource id used to lookup the View via the {@code Activity.getViewById()} method.
     */
    int value() default -1;

    /**
     * Resource tag used to lookup the View via the {@code Activity.getViewByTag()} method.
     */
    String tag() default "";
}