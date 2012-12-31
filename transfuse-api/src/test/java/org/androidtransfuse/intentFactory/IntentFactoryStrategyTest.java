/**
 * Copyright 2012 John Ericksen
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
package org.androidtransfuse.intentFactory;

import android.content.Context;
import android.content.Intent;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertNotNull;

/**
 * @author John Ericksen
 */
public class IntentFactoryStrategyTest {

    @Test
    public void verifyMethods() throws NoSuchMethodException {
        Method getExtrasMethod = IntentFactoryStrategy.class.getMethod(IntentFactoryStrategy.GET_EXTRAS_METHOD);
        assertNotNull(getExtrasMethod);
        Method getTargetContext = IntentFactoryStrategy.class.getMethod(IntentFactoryStrategy.GET_TARGET_CONTEXT_METHOD);
        assertNotNull(getTargetContext);
        Method startMethod = IntentFactoryStrategy.class.getMethod(IntentFactoryStrategy.START_METHOD, Context.class, Intent.class);
        assertNotNull(startMethod);
    }
}