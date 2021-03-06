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
package org.androidtransfuse.analysis;

import org.androidtransfuse.adapter.ASTField;
import org.androidtransfuse.adapter.ASTMethod;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.analysis.astAnalyzer.ASTAnalysis;
import org.androidtransfuse.analysis.astAnalyzer.VirtualProxyAspect;
import org.androidtransfuse.gen.variableBuilder.VariableBuilder;
import org.androidtransfuse.gen.variableBuilder.VariableInjectionBuilder;
import org.androidtransfuse.model.InjectionNode;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Stack;

/**
 * Analysis class for ASTType Injection Analysis
 *
 * @author John Ericksen
 */
public class Analyzer {

    @Inject
    private Provider<VariableInjectionBuilder> variableInjectionBuilderProvider;

    /**
     * Analyze the given ASTType and produces a corresponding InjectionNode with the contained
     * AST injection related elements (constructor, method, field) recursively analyzed for InjectionNodes
     *
     * @param instanceType astType instance
     * @param concreteType required type
     * @param context      required
     * @return InjectionNode root
     */
    public InjectionNode analyze(final ASTType instanceType, final ASTType concreteType, final AnalysisContext context) {

        InjectionNode injectionNode;

        if (context.isDependent(concreteType)) {
            //if this type is a dependency of itself, we've found a back link.
            //This dependency loop must be broken using a virtual proxy
            injectionNode = context.getInjectionNode(concreteType);

            Stack<InjectionNode> loopedDependencies = context.getDependencyHistory();

            InjectionNode proxyDependency = findProxyableDependency(injectionNode, loopedDependencies);

            if (proxyDependency == null) {
                throw new TransfuseAnalysisException("Unable to find a dependency to proxy");
            }

            VirtualProxyAspect proxyAspect = getProxyAspect(proxyDependency);
            proxyAspect.getProxyInterfaces().add(proxyDependency.getUsageType());

        } else {
            injectionNode = new InjectionNode(instanceType, concreteType);
            //default variable builder
            injectionNode.addAspect(VariableBuilder.class, variableInjectionBuilderProvider.get());

            AnalysisContext nextContext = context.addDependent(injectionNode);

            //loop over super classes (extension and implements)
            scanClassHierarchy(concreteType, injectionNode, nextContext);
        }

        return injectionNode;
    }

    private InjectionNode findProxyableDependency(InjectionNode duplicateDependency, Stack<InjectionNode> loopedDependencies) {
        //there may be better ways to identify the proxyable injection node.
        if (!duplicateDependency.getUsageType().isConcreteClass()) {
            return duplicateDependency;
        }
        for (InjectionNode loopInjectionNode = loopedDependencies.pop(); !loopedDependencies.empty() && !loopInjectionNode.equals(duplicateDependency); loopInjectionNode = loopedDependencies.pop()) {
            if (!loopInjectionNode.getUsageType().isConcreteClass()) {
                //found interface
                return loopInjectionNode;
            }
        }

        return null;
    }

    private void scanClassHierarchy(ASTType concreteType, InjectionNode injectionNode, AnalysisContext context) {
        if (concreteType.getSuperClass() != null) {
            scanClassHierarchy(concreteType.getSuperClass(), injectionNode, context);
        }

        for (ASTAnalysis analysis : context.getAnalysisRepository().getAnalysisSet()) {

            analysis.analyzeType(injectionNode, concreteType, context);

            for (ASTMethod astMethod : concreteType.getMethods()) {
                analysis.analyzeMethod(injectionNode, concreteType, astMethod, context);
            }

            for (ASTField astField : concreteType.getFields()) {
                analysis.analyzeField(injectionNode, concreteType, astField, context);
            }
        }
    }

    private VirtualProxyAspect getProxyAspect(InjectionNode injectionNode) {
        if (!injectionNode.containsAspect(VirtualProxyAspect.class)) {
            injectionNode.addAspect(new VirtualProxyAspect());
        }
        return injectionNode.getAspect(VirtualProxyAspect.class);
    }
}
