package org.jgrapht.ext;

import java.util.Map;

public interface EdgeProvider<V, E> {

   E buildEdge(V from, V to, String label, Map<String, String> attributes);

}
