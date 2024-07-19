/*
 * Copyright 2024 the original author or authors.
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

package org.gradle.internal.debug;

import com.google.common.base.Supplier;
import org.apache.commons.lang.StringUtils;
import org.gradle.internal.InternalTransformer;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicInteger;

public class Debug {
    public static final boolean ENABLED = false;

    private static final ThreadLocal<AtomicInteger> LEVEL = new ThreadLocal<AtomicInteger>() {
        @Override
        protected AtomicInteger initialValue() {
            return new AtomicInteger();
        }
    };

    public static void println(Object messageSource) {
        if (!ENABLED) {
            return;
        }
        String prefix = StringUtils.repeat(" ", LEVEL.get().get() * 2);
        System.out.println(prefix + asString(messageSource));
    }

    private static String asString(Object obj) {
        if (obj instanceof Supplier<?>) {
            return ((Supplier<?>) obj).get().toString();
        }
        return obj.toString();
    }

    public static void trace(Object message) {
        if (!ENABLED) {
            return;
        }
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        System.out.println("[" + Thread.currentThread().getName() + "] " + asString(message));
        for (StackTraceElement element : trace) {
            if (element.getClassName().contains("org.gradle") && !element.getMethodName().startsWith("invoke")) {
                System.out.println("\t*" + element);
            }
        }
    }

    @Nullable
    public static <I, O> O goInto(@Nullable I instance, InternalTransformer<O, I> action) {
        if (!ENABLED || instance == null) {
            return null;
        }
        LEVEL.get().incrementAndGet();
        try {
            return action.transform(instance);
        } finally {
            LEVEL.get().decrementAndGet();
        }
    }
}

