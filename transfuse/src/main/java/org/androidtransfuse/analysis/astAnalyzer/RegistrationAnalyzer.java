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
package org.androidtransfuse.analysis.astAnalyzer;

import android.view.View;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.androidtransfuse.adapter.*;
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.androidtransfuse.analysis.AnalysisContext;
import org.androidtransfuse.analysis.InjectionPointFactory;
import org.androidtransfuse.analysis.TransfuseAnalysisException;
import org.androidtransfuse.annotations.RegisterListener;
import org.androidtransfuse.gen.componentBuilder.*;
import org.androidtransfuse.listeners.*;
import org.androidtransfuse.model.InjectionNode;

import javax.inject.Inject;
import java.util.*;

/**
 * @author John Ericksen
 */
public class RegistrationAnalyzer implements ASTAnalysis {

    private final ImmutableMap<ASTType, RegistrationGeneratorFactory> generatorFactories;
    private final ASTClassFactory astClassFactory;
    private final InjectionPointFactory injectionPointFactory;
    private final ComponentBuilderFactory componentBuilderFactory;

    @Inject
    public RegistrationAnalyzer(ASTClassFactory astClassFactory,
                                InjectionPointFactory injectionPointFactory,
                                ComponentBuilderFactory componentBuilderFactory) throws NoSuchMethodException {
        this.astClassFactory = astClassFactory;
        this.injectionPointFactory = injectionPointFactory;
        this.componentBuilderFactory = componentBuilderFactory;

        Map<ASTType, String> listenerMethodMapping = new HashMap<ASTType, String>();

        listenerMethodMapping.put(astType(View.OnClickListener.class), "setOnClickListener");
        listenerMethodMapping.put(astType(View.OnLongClickListener.class), "setOnLongClickListener");
        listenerMethodMapping.put(astType(View.OnCreateContextMenuListener.class), "setOnCreateContextMenuListener");
        listenerMethodMapping.put(astType(View.OnKeyListener.class), "setOnKeyListener");
        listenerMethodMapping.put(astType(View.OnTouchListener.class), "setOnTouchListener");
        listenerMethodMapping.put(astType(View.OnFocusChangeListener.class), "setOnFocusChangeListener");

        Set<ASTType> callThroughMapping = new HashSet<ASTType>();

        callThroughMapping.add(astType(ActivityOnKeyDownListener.class));
        callThroughMapping.add(astType(ActivityOnKeyLongPressListener.class));
        callThroughMapping.add(astType(ActivityOnKeyUpListener.class));
        callThroughMapping.add(astType(ActivityOnKeyMultipleListener.class));
        callThroughMapping.add(astType(ActivityOnTouchEventListener.class));
        callThroughMapping.add(astType(ActivityOnTrackballEventListener.class));
        callThroughMapping.add(astType(ActivityMenuComponent.class));
        callThroughMapping.add(astType(ServiceOnStartCommand.class));
        callThroughMapping.add(astType(ServiceOnUnbind.class));

        ImmutableMap.Builder<ASTType, RegistrationGeneratorFactory> generatorBuilder = ImmutableMap.builder();

        generatorBuilder.putAll(Maps.transformValues(listenerMethodMapping, new ListenerMethodMappingTransformer()));
        generatorBuilder.putAll(Maps.transformValues(Maps.uniqueIndex(callThroughMapping, Functions.<ASTType>identity()),
                new CallThroughMethodMappingFunction()));

        generatorFactories = generatorBuilder.build();
    }

    private ASTType astType(Class<?> clazz) {
        return astClassFactory.getType(clazz);
    }

    private final class ListenerMethodMappingTransformer implements Function<String, RegistrationGeneratorFactory> {
        @Override
        public RegistrationGeneratorFactory apply(String value) {
            return new ViewRegistrationGeneratorFactory(value);
        }
    }

    private final class CallThroughMethodMappingFunction implements Function<ASTType, RegistrationGeneratorFactory> {
        @Override
        public RegistrationGeneratorFactory apply(ASTType astType) {
            return buildActivityDelegateRegistrationGeneratorFactory(astType);
        }
    }

    private interface RegistrationGeneratorFactory {

        RegistrationGenerator buildRegistrationGenerator(InjectionNode injectionNode, ASTBase astBase, ASTAnnotation registerAnnotation, AnalysisContext context);
    }

    private final class ViewRegistrationGeneratorFactory implements RegistrationGeneratorFactory {

        private String listenerMethod;

        private ViewRegistrationGeneratorFactory(String listenerMethod) {
            this.listenerMethod = listenerMethod;
        }

        @Override
        public RegistrationGenerator buildRegistrationGenerator(InjectionNode injectionNode, ASTBase astBase, ASTAnnotation registerAnnotation, AnalysisContext context) {

            InjectionNode viewInjectionNode = buildViewInjectionNode(registerAnnotation, context);

            ViewRegistrationInvocationBuilder invocationBuilder;
            if (astBase instanceof ASTType) {
                invocationBuilder = new ViewTypeRegistrationInvocationBuilderImpl();
            } else if (astBase instanceof ASTMethod) {
                invocationBuilder = componentBuilderFactory.buildViewMethodRegistrationInvocationBuilder((ASTMethod) astBase);
            } else if (astBase instanceof ASTField) {
                invocationBuilder = componentBuilderFactory.buildViewFieldRegistrationInvocationBuilder((ASTField) astBase);
            } else {
                throw new TransfuseAnalysisException("ASTBase type not mapped");
            }

            return componentBuilderFactory.buildViewRegistrationGenerator(viewInjectionNode, listenerMethod, injectionNode, invocationBuilder);
        }
    }

    private final class ActivityDelegateRegistrationGeneratorFactory implements RegistrationGeneratorFactory {

        private ImmutableList<ASTMethod> methods;

        private ActivityDelegateRegistrationGeneratorFactory(ImmutableList<ASTMethod> methods) {
            this.methods = methods;
        }

        @Override
        public RegistrationGenerator buildRegistrationGenerator(InjectionNode injectionNode, ASTBase astBase, ASTAnnotation registerAnnotation, AnalysisContext context) {

            ActivityDelegateASTReference delegateASTReference;

            if (astBase instanceof ASTType) {
                delegateASTReference = componentBuilderFactory.buildActivityTypeDelegateASTReference();
            } else if (astBase instanceof ASTMethod) {
                delegateASTReference = componentBuilderFactory.buildActivityMethodDelegateASTReference((ASTMethod) astBase);
            } else if (astBase instanceof ASTField) {
                delegateASTReference = componentBuilderFactory.buildActivityFieldDelegateASTReference((ASTField) astBase);
            } else {
                throw new TransfuseAnalysisException("ASTBase type not mapped");
            }

            //set injection node to field
            if (!injectionNode.containsAspect(ASTInjectionAspect.class)) {
                injectionNode.addAspect(new ASTInjectionAspect());
            }
            injectionNode.getAspect(ASTInjectionAspect.class).setAssignmentType(ASTInjectionAspect.InjectionAssignmentType.FIELD);

            return componentBuilderFactory.buildActivityRegistrationGenerator(delegateASTReference, methods);
        }
    }

    private RegistrationGeneratorFactory buildActivityDelegateRegistrationGeneratorFactory(ASTType listenerInterface) {

        ImmutableList.Builder<ASTMethod> delegateMethods = ImmutableList.builder();
        for (ASTMethod method : listenerInterface.getMethods()) {
            if (method.isAnnotated(CallThrough.class)) {
                delegateMethods.add(method);
            }
        }

        return new ActivityDelegateRegistrationGeneratorFactory(delegateMethods.build());
    }

    @Override
    public void analyzeType(InjectionNode injectionNode, final ASTType astType, AnalysisContext context) {
        analyze(astType, astType, injectionNode, context);
    }

    @Override
    public void analyzeMethod(InjectionNode injectionNode, ASTType concreteType, final ASTMethod astMethod, AnalysisContext context) {
        analyze(astMethod, astMethod.getReturnType(), injectionNode, context);
    }

    @Override
    public void analyzeField(InjectionNode injectionNode, ASTType concreteType, final ASTField astField, AnalysisContext context) {
        analyze(astField, astField.getASTType(), injectionNode, context);
    }

    private <T> void analyze(ASTBase astBase, ASTType astType, InjectionNode injectionNode, AnalysisContext context) {
        if (astBase.isAnnotated(RegisterListener.class)) {
            ASTAnnotation registerAnnotation = astBase.getASTAnnotation(RegisterListener.class);

            ASTType[] interfaces = registerAnnotation.getProperty("interfaces", ASTType[].class);

            List<ASTType> interfaceList = new ArrayList<ASTType>();
            if (interfaces != null) {
                interfaceList.addAll(Arrays.asList(interfaces));
            }

            List<RegistrationGenerator> generators = getGeneratorFactories(injectionNode, astBase, astType, interfaceList, registerAnnotation, context);

            if (!generators.isEmpty()) {
                RegistrationAspect registrationAspect = getRegistrationAspect(injectionNode);
                registrationAspect.addRegistrationBuilders(generators);
            }
        }
    }

    private ImmutableList<RegistrationGenerator> getGeneratorFactories(InjectionNode injectionNode, ASTBase astBase, ASTType astType, List<ASTType> interfaceList, ASTAnnotation registerAnnotation, AnalysisContext context) {

        ImmutableList.Builder<RegistrationGenerator> generators = ImmutableList.builder();

        for (Map.Entry<ASTType, RegistrationGeneratorFactory> generatorFactoryEntry : generatorFactories.entrySet()) {
            if ((interfaceList.isEmpty() || interfaceList.contains(generatorFactoryEntry.getKey()))
                    && astType.inheritsFrom(generatorFactoryEntry.getKey())) {
                generators.add(generatorFactoryEntry.getValue().buildRegistrationGenerator(injectionNode, astBase, registerAnnotation, context));
            }
        }

        return generators.build();
    }

    private InjectionNode buildViewInjectionNode(final ASTAnnotation registerAnnotation, AnalysisContext context) {

        ASTType viewType = astClassFactory.getType(View.class);
        ASTType atViewType = astClassFactory.getType(org.androidtransfuse.annotations.View.class);
        ASTAnnotation viewRegistrationAnnotation = new ASTAnnotationPropertyReplacement(registerAnnotation, atViewType);

        return injectionPointFactory.buildInjectionNode(Collections.singleton(viewRegistrationAnnotation), viewType, context);
    }

    private static final class ASTAnnotationPropertyReplacement implements ASTAnnotation {

        private final ASTAnnotation annotation;
        private final ASTType astType;

        private ASTAnnotationPropertyReplacement(ASTAnnotation annotation, ASTType astType) {
            this.annotation = annotation;
            this.astType = astType;
        }

        @Override
        public <T> T getProperty(String name, Class<T> type) {
            return annotation.getProperty(name, type);
        }

        @Override
        public ASTType getASTType() {
            return astType;
        }

        @Override
        public Collection<String> getPropertyNames() {
            return annotation.getPropertyNames();
        }
    }

    private RegistrationAspect getRegistrationAspect(InjectionNode injectionNode) {
        if (!injectionNode.containsAspect(RegistrationAspect.class)) {
            injectionNode.addAspect(new RegistrationAspect());
        }
        return injectionNode.getAspect(RegistrationAspect.class);
    }
}