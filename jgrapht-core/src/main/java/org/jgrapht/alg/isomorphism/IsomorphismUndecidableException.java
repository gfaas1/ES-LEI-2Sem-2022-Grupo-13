package org.jgrapht.alg.isomorphism;

public class IsomorphismUndecidableException extends Exception {
    public IsomorphismUndecidableException() {}

    public IsomorphismUndecidableException(String message) {
        super (message);
    }

    public IsomorphismUndecidableException(Throwable cause) {
        super (cause);
    }

    public IsomorphismUndecidableException(String message, Throwable cause) {
        super (message, cause);
    }
}
