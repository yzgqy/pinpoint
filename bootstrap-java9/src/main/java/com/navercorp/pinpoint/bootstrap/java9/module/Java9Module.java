/*
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.bootstrap.java9.module;

import com.navercorp.pinpoint.bootstrap.module.JavaModule;

import java.lang.instrument.Instrumentation;
import java.util.Map;
import java.util.Set;

/**
 * @author Woonduk Kang(emeroad)
 */
public class Java9Module implements JavaModule {

//    private final ModuleLogger logger = ModuleLogger.getLogger(Java9Module.class.getName());
    private final Instrumentation instrumentation;
    private final Module module;


    Java9Module(Instrumentation instrumentation, Module module) {
        if (instrumentation == null) {
            throw new NullPointerException("instrumentation must not be null");
        }
        this.instrumentation = instrumentation;
        this.module = module;
    }

    @Override
    public boolean isSupported() {
        return true;
    }

    @Override
    public boolean isNamed() {
        return this.module.isNamed();
    }

    @Override
    public String getName() {
        return this.module.getName();

    }

    @Override
    public void addReads(JavaModule targetJavaModule) {
        final Java9Module target = checkJavaModule(targetJavaModule);

//        logger.info("addReads module:" + module.getName() +" target:" + target);
        // for debug
        final Set<Module> readModules = Set.of(target.module);
        RedefineModuleUtils.addReads(instrumentation, module, readModules);
    }

    @Override
    public void addExports(String packageName, JavaModule targetJavaModule) {
        if (packageName == null) {
            throw new NullPointerException("packageName must not be null");
        }
         final Java9Module target = checkJavaModule(targetJavaModule);

//        logger.info("addExports module:" + module.getName() + " pkg:" + packageName + " target:" + target);
        final Map<String, Set<Module>> extraModules = Map.of(packageName, Set.of(target.module));
        RedefineModuleUtils.addExports(instrumentation, module, extraModules);
    }

    private Java9Module checkJavaModule(JavaModule targetJavaModule) {
        if (targetJavaModule == null) {
            throw new NullPointerException("targetJavaModule must not be null");
        }
        if (targetJavaModule instanceof Java9Module) {
            return (Java9Module) targetJavaModule;
        }
        throw new ModuleException("invalid JavaModule: " + targetJavaModule.getClass());
    }

    @Override
    public void addOpens(String packageName, JavaModule javaModule) {
        if (packageName == null) {
            throw new NullPointerException("packageName must not be null");
        }
        final Java9Module target = checkJavaModule(javaModule);

//        logger.info("addExports module:" + module.getName() + " pkg:" + packageName + " target:" + target);

        final Map<String, Set<Module>> extraOpens = Map.of(packageName, Set.of(target.module));
        RedefineModuleUtils.addOpens(instrumentation, module, extraOpens);
    }


    @Override
    public void addUses(Class<?> target) {
        if (target == null) {
            throw new NullPointerException("target must not be null");
        }
//        logger.info("addUses module:" + module.getName() +" target:" + target);
        // for debug
        final Set<Class<?>> extraUses = Set.of(target);
        RedefineModuleUtils.addUses(instrumentation, module, extraUses);
    }

    @Override
    public boolean isExported(String packageName, JavaModule targetJavaModule) {
        if (packageName == null) {
            throw new NullPointerException("packageName must not be null");
        }
        final Java9Module target = checkJavaModule(targetJavaModule);
        return module.isExported(packageName, target.module);
    }

    @Override
    public boolean isOpen(String packageName, JavaModule targetJavaModule) {
        if (packageName == null) {
            throw new NullPointerException("packageName must not be null");
        }
        final Java9Module target = checkJavaModule(targetJavaModule);
        return module.isOpen(packageName, target.module);
    }

    @Override
    public boolean canRead(JavaModule targetJavaModule) {
        final Java9Module target = checkJavaModule(targetJavaModule);
        return this.module.canRead(target.module);
    }

    @Override
    public boolean canRead(Class<?> targetClazz) {
        return this.module.canUse(targetClazz);
    }


    @Override
    public ClassLoader getClassLoader() {
        return module.getClassLoader();
    }

    @Override
    public String toString() {
        return module.toString();
    }


}
