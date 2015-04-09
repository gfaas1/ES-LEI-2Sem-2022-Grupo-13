package org.jgrapht.ext;

import java.util.Map;

public interface VertexProvider<V> {

   V buildVertex(String label, Map<String, String> attributes);

}
