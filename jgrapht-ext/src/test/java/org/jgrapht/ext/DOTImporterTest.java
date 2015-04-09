package org.jgrapht.ext;

import java.io.StringWriter;
import java.util.Map;

import junit.framework.*;
import org.jgrapht.graph.*;

public class DOTImporterTest extends TestCase
{

   public void testUndirectedWithLabels() throws ImportException {
      String input = "graph G {\n"
                     + "  1 [ label=\"abc123\" ];\n"
                     + "  2 [ label=\"fred\" ];\n"
                     + "  1 -- 2;\n"
                     + "}";

      Multigraph<String, DefaultEdge> expected
            = new Multigraph<String, DefaultEdge>(DefaultEdge.class);
      expected.addVertex("abc123");
      expected.addVertex("fred");
      expected.addEdge("abc123", "fred");


      DOTImporter<String, DefaultEdge> importer = buildImporter();

      Multigraph<String, DefaultEdge> result
            = new Multigraph<String, DefaultEdge>(DefaultEdge.class);
      importer.read(input, result);

      Assert.assertEquals(expected.toString(), result.toString());

      Assert.assertEquals(2, result.vertexSet().size());
      Assert.assertEquals(1, result.edgeSet().size());

   }

   public void testDirectedNoLabels() throws ImportException {
      String input = "digraph graphname {\r\n"
                     + "     a -> b -> c;\r\n"
                     + "     b -> d;\r\n"
                     + " }";

      DirectedMultigraph<String, DefaultEdge> expected
            = new DirectedMultigraph<String, DefaultEdge>(DefaultEdge.class);
      expected.addVertex("a");
      expected.addVertex("b");
      expected.addVertex("c");
      expected.addVertex("d");
      expected.addEdge("a", "b");
      expected.addEdge("b", "c");
      expected.addEdge("b", "d");


      DOTImporter<String, DefaultEdge> importer = buildImporter();

      DirectedMultigraph<String, DefaultEdge> result
            = new DirectedMultigraph<String, DefaultEdge>(DefaultEdge.class);
      importer.read(input, result);

      Assert.assertEquals(expected.toString(), result.toString());

      Assert.assertEquals(4, result.vertexSet().size());
      Assert.assertEquals(3, result.edgeSet().size());

   }

   public void testMultiLinksUndirected() throws ImportException {
      String input = "graph G {\n"
                     + "  1 [ label=\"bob\" ];\n"
                     + "  2 [ label=\"fred\" ];\n"
              // the extra label will be ignored but not cause any problems.
                     + "  1 -- 2 [ label=\"friend\"];\n"
                     + "  1 -- 2;\n"
                     + "}";

      Multigraph<String, DefaultEdge> expected
            = new Multigraph<String, DefaultEdge>(DefaultEdge.class);
      expected.addVertex("bob");
      expected.addVertex("fred");
      expected.addEdge("bob", "fred", new DefaultEdge());
      expected.addEdge("bob", "fred", new DefaultEdge());


      DOTImporter<String, DefaultEdge> importer = buildImporter();

      Multigraph<String, DefaultEdge> result
            = new Multigraph<String, DefaultEdge>(DefaultEdge.class);
      importer.read(input, result);

      Assert.assertEquals(expected.toString(), result.toString());

      Assert.assertEquals(2, result.vertexSet().size());
      Assert.assertEquals(2, result.edgeSet().size());
   }

   public void testExportImportLoop() throws ImportException {
      DirectedMultigraph<String, DefaultEdge> start
            = new DirectedMultigraph<String, DefaultEdge>(DefaultEdge.class);
      start.addVertex("a");
      start.addVertex("b");
      start.addVertex("c");
      start.addVertex("d");
      start.addEdge("a", "b");
      start.addEdge("b", "c");
      start.addEdge("b", "d");

      DOTExporter<String, DefaultEdge> exporter
            = new DOTExporter<String, DefaultEdge>(new VertexNameProvider<String>() {
         @Override
         public String getVertexName(String vertex) {
            return vertex;
         }
      }, null, new IntegerEdgeNameProvider<DefaultEdge>());

      DOTImporter<String, DefaultEdge> importer = buildImporter();
      StringWriter writer = new StringWriter();

      exporter.export(writer, start);

      DirectedMultigraph<String, DefaultEdge> result
            = new DirectedMultigraph<String, DefaultEdge>(DefaultEdge.class);

      importer.read(writer.toString(), result);

      Assert.assertEquals(start.toString(), result.toString());

      Assert.assertEquals(4, result.vertexSet().size());
      Assert.assertEquals(3, result.edgeSet().size());


   }

   public void testEmptyString()
   {
      testGarbage("", "Dot string was empty");
   }

   public void testGarbageStringEnoughLines()
   {
      String input = "jsfhg kjdsf hgkfds\n"
                     + "fdsgfdsgfd\n"
                     + "gfdgfdsgfdsg\n"
                     + "jdhgkjfdshgsjkhl\n";

      testGarbage(input, "unknown graph type");
   }

   public void testGarbageStringInvalidFirstLine()
   {
      String input = "jsfhgkjdsfhgkfds\n"
                     + "fdsgfdsgfd\n";

      testGarbage(input, "not enough parts on first line");
   }

   public void testGarbageStringNotEnoughLines()
   {
      String input = "jsfhgkjdsfhgkfds\n";

      testGarbage(input, "Dot string was invalid");
   }

   private void testGarbage(String input, String expected) {
      DirectedMultigraph<String, DefaultEdge> result
            = new DirectedMultigraph<String, DefaultEdge>(DefaultEdge.class);
      DOTImporter<String, DefaultEdge> importer = buildImporter();
      try {
         importer.read(input, result);
         Assert.fail("Should not get here");
      } catch (ImportException e) {
         Assert.assertEquals(expected, e.getMessage());
      }
   }


   private DOTImporter<String, DefaultEdge> buildImporter() {
      return new DOTImporter<String, DefaultEdge>(
            new VertexProvider<String>() {
               @Override
               public String buildVertex(String label,
                                         Map<String, String> attributes) {
                  return label;
               }
            },
            new EdgeProvider<String, DefaultEdge>() {
               @Override
               public DefaultEdge buildEdge(String from,
                                            String to,
                                            String label,
                                            Map<String, String> attributes) {
                  return new DefaultEdge();
               }
            }
      );
   }
}
