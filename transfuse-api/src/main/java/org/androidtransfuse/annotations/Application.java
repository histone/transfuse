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
 * Defines a class to be a Transfuse Application component.  For classes that do not extend the {@code android.app.Application}
 * class this annotation activates the event systems, dependency injection features and manifest management.</p>
 * <p>
 * For classes that do extend the android.app.Application class, defining a class as a Transfuse Application will simply
 * activate manifest management of the Application.</p>
 * <p>
 * Under both cases you may define additional manifest metadata which will be associated with the Application manifest
 * entry.</p>
 *
 * @author John Ericksen
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Application {

    String name() default "";

    String label() default "";

    boolean allowTaskReparenting() default false;

    String backupAgent() default "";

    boolean debuggable() default false;

    String description() default "";

    boolean enabled() default true;

    boolean hasCode() default true;

    boolean hardwareAccelerated() default false;

    String icon() default "";

    boolean killAfterRestore() default true;

    String logo() default "";

    String manageSpaceActivity() default "";

    String permission() default "";

    boolean persistent() default false;

    String process() default "";

    boolean restoreAnyVersion() default false;

    String taskAffinity() default "";

    String theme() default "";

    UIOptions uiOptions() default UIOptions.NONE;
}
