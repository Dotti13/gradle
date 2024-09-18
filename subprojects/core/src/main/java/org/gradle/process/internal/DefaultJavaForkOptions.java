/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.process.internal;

import org.gradle.api.Action;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.internal.file.FileCollectionFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.internal.file.PathToFileResolver;
import org.gradle.internal.jvm.Jvm;
import org.gradle.process.CommandLineArgumentProvider;
import org.gradle.process.JavaDebugOptions;
import org.gradle.process.JavaForkOptions;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.gradle.process.internal.util.MergeOptionsUtil.containsAll;
import static org.gradle.process.internal.util.MergeOptionsUtil.getHeapSizeMb;
import static org.gradle.process.internal.util.MergeOptionsUtil.normalized;

public abstract class DefaultJavaForkOptions extends DefaultProcessForkOptions implements JavaForkOptionsInternal {
    private final JvmOptions options;

    @Inject
    public DefaultJavaForkOptions(PathToFileResolver resolver, FileCollectionFactory fileCollectionFactory, JavaDebugOptions debugOptions) {
        super(resolver);
        options = new JvmOptions(fileCollectionFactory, debugOptions);
        getDefaultCharacterEncoding().convention(options.getDefaultCharacterEncoding());
        getEnableAssertions().convention(options.getEnableAssertions());
        getDebug().convention(options.getDebug());
    }

    @Override
    public Provider<List<String>> getAllJvmArgs() {
        return getJvmArgs().zip(getJvmArgumentProviders(), (args, providers) -> {
            JvmOptions copy = options.createCopy();
            copy.copyFrom(this);
            return copy.getAllJvmArgs();
        });
    }

    @Override
    public abstract ListProperty<String> getJvmArgs();

    @Override
    public JavaForkOptions jvmArgs(Iterable<?> arguments) {
        for (Object argument : arguments) {
            getJvmArgs().add(argument.toString());
        }
        return this;
    }

    @Override
    public JavaForkOptions jvmArgs(Object... arguments) {
        jvmArgs(Arrays.asList(arguments));
        return this;
    }

    @Override
    public abstract ListProperty<CommandLineArgumentProvider> getJvmArgumentProviders();

    @Override
    public abstract MapProperty<String, Object> getSystemProperties();

    @Override
    public JavaForkOptions systemProperties(Map<String, ?> properties) {
        getSystemProperties().putAll(properties);
        return this;
    }

    @Override
    public JavaForkOptions systemProperty(String name, Object value) {
        getSystemProperties().put(name, value);
        return this;
    }

    @Override
    public abstract ConfigurableFileCollection getBootstrapClasspath();

    @Override
    public JavaForkOptions bootstrapClasspath(Object... classpath) {
        getBootstrapClasspath().from(classpath);
        return this;
    }

    @Override
    public abstract Property<String> getMinHeapSize();

    @Override
    public abstract Property<String> getMaxHeapSize();

    @Override
    public abstract Property<String> getDefaultCharacterEncoding();

    @Override
    public abstract Property<Boolean> getEnableAssertions();

    @Override
    public abstract Property<Boolean> getDebug();

    @Override
    public JavaDebugOptions getDebugOptions() {
        return options.getDebugOptions();
    }

    @Override
    public void debugOptions(Action<JavaDebugOptions> action) {
        action.execute(options.getDebugOptions());
    }

    @Override
    protected Map<String, ?> getInheritableEnvironment() {
        // Filter out any environment variables that should not be inherited.
        return Jvm.getInheritableEnvironmentVariables(super.getInheritableEnvironment());
    }

    @Override
    public JavaForkOptions copyTo(JavaForkOptions target) {
        super.copyTo(target);
        target.getJvmArgs().empty();
        target.getJvmArgs().set(getJvmArgs());
        target.getSystemProperties().set(getSystemProperties());
        target.getMinHeapSize().set(getMaxHeapSize());
        target.getMaxHeapSize().set(getMaxHeapSize());
        target.bootstrapClasspath(getBootstrapClasspath());
        target.getEnableAssertions().set(getEnableAssertions());
        JvmOptions.copyDebugOptions(this.getDebugOptions(), target.getDebugOptions());
        target.getJvmArgumentProviders().set(getJvmArgumentProviders());
        return this;
    }

    @Override
    public boolean isCompatibleWith(JavaForkOptions options) {
        if (hasJvmArgumentProviders(this) || hasJvmArgumentProviders(options)) {
            throw new UnsupportedOperationException("Cannot compare options with jvmArgumentProviders.");
        }
        return getDebug() == options.getDebug()
            && getEnableAssertions() == options.getEnableAssertions()
            && normalized(getExecutable()).equals(normalized(options.getExecutable()))
            && getWorkingDir().equals(options.getWorkingDir())
            && normalized(getDefaultCharacterEncoding().getOrNull()).equals(normalized(options.getDefaultCharacterEncoding().getOrNull()))
            && getHeapSizeMb(getMinHeapSize().getOrNull()) >= getHeapSizeMb(options.getMinHeapSize().getOrNull())
            && getHeapSizeMb(getMaxHeapSize().getOrNull()) >= getHeapSizeMb(options.getMaxHeapSize().getOrNull())
            && normalized(getJvmArgs().getOrNull()).containsAll(normalized(options.getJvmArgs().getOrNull()))
            && containsAll(getSystemProperties().get(), options.getSystemProperties().get())
            && containsAll(getEnvironment(), options.getEnvironment())
            && getBootstrapClasspath().getFiles().containsAll(options.getBootstrapClasspath().getFiles());
    }

    @Override
    public void checkDebugConfiguration(Iterable<?> arguments) {
        options.checkDebugConfiguration(arguments);
    }

    @Override
    public void setExtraJvmArgs(Iterable<?> arguments) {
        options.setExtraJvmArgs(arguments);
    }

    private static boolean hasJvmArgumentProviders(JavaForkOptions forkOptions) {
        return forkOptions instanceof DefaultJavaForkOptions
            && hasJvmArgumentProviders((DefaultJavaForkOptions) forkOptions);
    }

    private static boolean hasJvmArgumentProviders(DefaultJavaForkOptions forkOptions) {
        return !isNullOrEmpty(forkOptions.getJvmArgumentProviders().get());
    }

    private static <T> boolean isNullOrEmpty(List<T> list) {
        return list == null || list.isEmpty();
    }
}
