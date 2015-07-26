package org.jgrapht.alg.util;

import java.util.HashMap;
import java.util.Map;

public class Extension<T, E> {
    private ExtensionFactory<E> extensionFactory;
    private Map<T, E>           extensions = new HashMap<T, E>();

    public interface ExtensionFactory<E> {
        E create();
    }

    public Extension(ExtensionFactory<E> factory) {
        this.extensionFactory = factory;
    }

    public static abstract class BaseExtension {
        public BaseExtension() {}
    }

    public E createInstance() {
        return extensionFactory.create();
    }

    public E get(T t)
    {
        if (extensions.containsKey(t))
            return extensions.get(t);

        E x = createInstance();
        extensions.put(t, x);
        return x;
    }

    public static class ExtensionManagerInstantiationException extends RuntimeException {
        Exception exception;
        public ExtensionManagerInstantiationException(Exception e) {
            exception = e;
        }
    }
}
