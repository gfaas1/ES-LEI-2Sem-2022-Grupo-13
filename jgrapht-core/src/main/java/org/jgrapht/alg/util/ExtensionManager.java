package org.jgrapht.alg.util;

import java.util.HashMap;
import java.util.Map;

public class ExtensionManager<T, E> {
    private Class<E>    extensionKlass;
    private Map<T, E>   extensions = new HashMap<T, E>();

    public ExtensionManager(Class<E> extensionKlass) {
        this.extensionKlass = extensionKlass;
    }

    public static abstract class BaseExtension {
        public BaseExtension() {}
    }

    private E createInstance() throws IllegalAccessException, InstantiationException {
        return extensionKlass.newInstance();
    }

    public E get(T t)
    {
        try {

            if (extensions.containsKey(t))
                return extensions.get(t);

            E x = createInstance();

            extensions.put(t, x);

            return x;

        } catch (IllegalAccessException e) {
            throw new ExtensionManagerInstantiationException(e);
        } catch (InstantiationException e) {
            throw new ExtensionManagerInstantiationException(e);
        }
    }

    public static class ExtensionManagerInstantiationException extends RuntimeException {
        Exception exception;
        public ExtensionManagerInstantiationException(Exception e) {
            exception = e;
        }
    }
}
