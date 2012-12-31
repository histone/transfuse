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
package org.androidtransfuse.annotations;

import org.junit.Test;

import java.lang.reflect.Method;

import static junit.framework.Assert.assertNotNull;

/**
 * @author John Ericksen
 */
public class ParcelConverterTest {

    @Test
    public void verifyMethodNames() throws NoSuchMethodException {
        Method translateToMethod = ParcelConverter.class.getMethod(ParcelConverter.CONVERT_TO_PARCEL, new Class[]{Object.class, android.os.Parcel.class});
        assertNotNull(translateToMethod);
        Method translateFromMethod = ParcelConverter.class.getMethod(ParcelConverter.CONVERT_FROM_PARCEL, new Class[]{android.os.Parcel.class});
        assertNotNull(translateFromMethod);
    }
}