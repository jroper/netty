/*
 * Copyright 2011 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.util.internal;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.Deflater;


/**
 * Utility that detects various properties specific to the current runtime
 * environment, such as Java version and the availability of the
 * {@code sun.misc.Unsafe} object.
 * <p>
 * You can disable the use of {@code sun.misc.Unsafe} if you specify
 * the system property <strong>io.netty.noUnsafe</strong>.
 */
public final class DetectionUtil {

    private static final int JAVA_VERSION = javaVersion0();
    private static final boolean HAS_UNSAFE = hasUnsafe(AtomicInteger.class.getClassLoader());
    private static final boolean IS_WINDOWS;

    static {
        String os = System.getProperty("os.name").toLowerCase();
        // windows
        IS_WINDOWS = os.indexOf("win") >= 0;
    }

    /**
     * Return <code>true</code> if the JVM is running on Windows
     */
    public static boolean isWindows() {
        return IS_WINDOWS;
    }

    public static boolean hasUnsafe() {
        return HAS_UNSAFE;
    }

    public static int javaVersion() {
        return JAVA_VERSION;
    }

    private static boolean hasUnsafe(ClassLoader loader) {
        String value = SystemPropertyUtil.get("io.netty.noUnsafe");
        if (value != null) {
            return false;
        }

        // Legacy properties
        value = SystemPropertyUtil.get("io.netty.tryUnsafe");
        if (value == null) {
            value = SystemPropertyUtil.get("org.jboss.netty.tryUnsafe", "true");
        }
        if (!"true".equalsIgnoreCase(value)) {
            return false;
        }

        try {
            Class<?> unsafeClazz = Class.forName("sun.misc.Unsafe", true, loader);
            return hasUnsafeField(unsafeClazz);
        } catch (Exception e) {
            // Ignore
        }
        return false;
    }

    private static boolean hasUnsafeField(final Class<?> unsafeClass) throws PrivilegedActionException {
        return AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
            @Override
            public Boolean run() throws Exception {
                unsafeClass.getDeclaredField("theUnsafe");
                return true;
            }
        });
    }

    private static int javaVersion0() {
        // Android
        try {
            Class.forName("android.app.Application", false, ClassLoader.getSystemClassLoader());
            return 6;
        } catch (Exception e) {
            // Ignore
        }

        try {
            Deflater.class.getDeclaredField("SYNC_FLUSH");
            return 7;
        } catch (Exception e) {
            // Ignore
        }

        return 6;
    }

    private DetectionUtil() {
        // only static method supported
    }
}
