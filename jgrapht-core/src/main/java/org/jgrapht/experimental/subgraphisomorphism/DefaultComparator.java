package org.jgrapht.experimental.subgraphisomorphism;

import java.util.Comparator;

/**
 * This is a default implementation for the check on semantic equality between
 * vertices or edges.
 * 
 * @author Fabian Späh
 */

public class DefaultComparator<T> implements Comparator<T> {

    @Override
    public int compare(T arg0, T arg1) {
        return 0;
    }

}
