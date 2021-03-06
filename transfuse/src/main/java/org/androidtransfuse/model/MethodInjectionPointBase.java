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
package org.androidtransfuse.model;

import org.androidtransfuse.adapter.ASTAccessModifier;
import org.androidtransfuse.adapter.ASTType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author John Ericksen
 */
public abstract class MethodInjectionPointBase {

    private final ASTType containingType;
    private final List<InjectionNode> injectionNodes = new ArrayList<InjectionNode>();
    private final List<ASTType> throwsTypes = new ArrayList<ASTType>();
    private final ASTAccessModifier accessModifier;

    protected MethodInjectionPointBase(ASTType containingType, ASTAccessModifier astAccessModifier) {
        this.containingType = containingType;
        this.accessModifier = astAccessModifier;
    }

    public void addInjectionNode(InjectionNode injectionNode) {
        this.injectionNodes.add(injectionNode);
    }

    public List<InjectionNode> getInjectionNodes() {
        return injectionNodes;
    }

    public void addThrows(Collection<ASTType> types) {
        throwsTypes.addAll(types);
    }

    public List<ASTType> getThrowsTypes() {
        return throwsTypes;
    }

    public ASTType getContainingType() {
        return containingType;
    }

    public ASTAccessModifier getAccessModifier() {
        return accessModifier;
    }
}
