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
package org.androidtransfuse.analysis;

import org.androidtransfuse.TransfuseTestInjector;
import org.androidtransfuse.analysis.adapter.ASTClassFactory;
import org.androidtransfuse.analysis.adapter.ASTType;
import org.androidtransfuse.annotations.Application;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

/**
 * @author John Ericksen
 */
public class ApplicationAnalysisTest {

    @Inject
    private ApplicationAnalysis applicationAnalysis;
    @Inject
    private ASTClassFactory astClassFactory;
    private ASTType applicationASTType;

    @Application
    public class ApplicationAnalysisTarget {
    }

    @Before
    public void setup() {
        TransfuseTestInjector.inject(this);

        applicationASTType = astClassFactory.getType(ApplicationAnalysisTarget.class);
    }

    @Test
    public void testAnalysis() {
        applicationAnalysis.analyze(applicationASTType);
    }

}