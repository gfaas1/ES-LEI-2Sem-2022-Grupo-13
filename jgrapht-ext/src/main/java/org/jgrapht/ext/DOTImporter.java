package org.jgrapht.ext;

import java.util.*;

import org.jgrapht.Graph;

public class DOTImporter<V,E> {

   private VertexProvider<V> vertexProvider;
   private EdgeProvider<V, E> edgeProvider;

   public DOTImporter(VertexProvider<V> vertexProvider, EdgeProvider<V, E> edgeProvider)
   {
      this.vertexProvider = vertexProvider;
      this.edgeProvider = edgeProvider;
   }

   public void read(String input, Graph<V, E> graph) throws ImportException
   {


      if (input == null || input.isEmpty()) {
         throw new ImportException("Dot string was empty");
      }

      String[] lines = input.split("[;\r\n]");

      validateLines(lines);

      // cache of vertexes added to the graph.
      Map<String, V> vertexes = new HashMap<String, V>();

      for(int lineIndex = 1; lineIndex < lines.length - 1; lineIndex ++ ) {

         // with \r\n or just ;\n line ends we get blanks. Filter here.
         if(lines[lineIndex].trim().isEmpty()) {
            continue;
         }

         String line = lines[lineIndex].trim();
         if (line.startsWith("//") || line.startsWith("#")) {
            // line comment so ignore
            // TODO: block comments
         } else if (!line.contains("[") && line.contains("=")) {
            throw new ImportException("graph level properties are not currently supported.");
         } else if (!line.contains("-")) {
            // probably a vertex
            Map<String, String> attributes = extractAttributes(line);

            String id = line.trim();
            int bracketIndex = line.indexOf('[');
            if (bracketIndex > 0) {
               id = line.substring(0, line.indexOf('[')).trim();
            }

            String label = attributes.get("label");
            if (label == null) {
               label = id;
            }

            V existing = vertexes.get(id);
            if (existing == null) {
               V vertex = vertexProvider.buildVertex(label, attributes);
               graph.addVertex(vertex);
               vertexes.put(id, vertex);
            } else {
              throw new ImportException("out of order input");
            }
         } else {
            Map<String, String> attributes = extractAttributes(line);

            List<String> ids = extractEdgeIds(line);

            // for each pair of ids in the list create an edge.
            for(int i = 0; i < ids.size() - 1; i++) {
               V v1 = getVertex(ids.get(i), vertexes, graph);
               V v2 = getVertex(ids.get(i+1), vertexes, graph);

               E edge = edgeProvider.buildEdge(v1, v2, attributes.get("label"), attributes);
               graph.addEdge(v1, v2, edge);
            }
         }

      }

   }

   private void validateLines(String[] lines) throws ImportException
   {
      if(lines.length < 2) {
         throw new ImportException("Dot string was invalid");
      }
      // validate the first line
      String[] firstLine = lines[0].split(" ", 3);
      if(firstLine.length != 3) {
         throw new ImportException("not enough parts on first line");
      }

      if (!firstLine[0].equals("digraph") && !firstLine[0].equals("graph")) {
         throw new ImportException("unknown graph type");
      }
   }

   // if a vertex id doesn't already exist create one for it with no attributes.
   private V getVertex(String id, Map<String, V> vertexes, Graph<V, E> graph)
   {
      V v = vertexes.get(id);
      if (v == null) {
         v = vertexProvider.buildVertex(id, new HashMap<String, String>());
         graph.addVertex(v);
         vertexes.put(id, v);
      }
      return v;
   }

   private List<String> extractEdgeIds(String line)
   {
      String idChunk = line;
      int bracketIndex = line.indexOf('[');
      if (bracketIndex > 1) {
         idChunk = idChunk.substring(0, bracketIndex).trim();
      }
      int index = 0;
      List<String> ids = new ArrayList<String>();
      while(index < idChunk.length()) {
         int nextSpace = idChunk.indexOf(' ', index);
         String chunk;
         if ( nextSpace > 0) { // is this the last chunk
            chunk = idChunk.substring(index, nextSpace);
            index = nextSpace + 1;
         } else {
            chunk = idChunk.substring(index);
            index = idChunk.length() + 1;
         }
         if(!chunk.equals("--") && !chunk.equals("->")) {  // a label then?
            ids.add(chunk);
         }

      }

      return ids;
   }

   private Map<String, String> extractAttributes(String line)
   {
      Map<String, String> attributes = new HashMap<String, String>();
      int bracketIndex = line.indexOf("[");
      if (bracketIndex > 0) {
         attributes = splitAttributes(
               line.substring(bracketIndex + 1, line.lastIndexOf(']')).trim()
         );
      }
      return attributes;
   }

   //  type="SeenIn" last-seen="2015-04-07T14:30:00.000-0100"
   private Map<String, String> splitAttributes(String input)
   {
      int index = 0;
      Map<String, String> result = new HashMap<String, String>();
      while(index < input.length()) {
         int nextEquals = input.indexOf('=', index);
         String key = input.substring(index, nextEquals).trim();
         int firstQuote = input.indexOf('\"', nextEquals) + 1;
         int secondQuote = input.indexOf('\"', firstQuote);
         String value = input.substring(firstQuote, secondQuote);
         result.put(key, value);
         index = secondQuote+1;
      }
      return result;
   }

}
