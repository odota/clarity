package skadistats.clarity;

import skadistats.clarity.decoder.Util;
import sun.misc.Unsafe;

import java.lang.reflect.Constructor;

public class Platform {

    private static final Architecture architecture;
    private static final Unsafe unsafe;

    static {
        architecture = System.getProperty("os.arch").indexOf("64") != -1 ? Architecture.IA64 : Architecture.IA32;

        Unsafe u = null;
        if (classForName("sun.misc.Unsafe") != null) {
            try {
                Constructor<Unsafe> unsafeConstructor = Unsafe.class.getDeclaredConstructor();
                unsafeConstructor.setAccessible(true);
                u = unsafeConstructor.newInstance();
            } catch (Exception e) {
                Util.uncheckedThrow(e);
            }
        }
        unsafe = u;
    }

    private static Class<?> classForName(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Unsafe getUnsafe() {
        return unsafe;
    }

    public static Architecture getArchitecture() {
        return architecture;
    }

    public enum Architecture {
        IA32,
        IA64
    }

}
