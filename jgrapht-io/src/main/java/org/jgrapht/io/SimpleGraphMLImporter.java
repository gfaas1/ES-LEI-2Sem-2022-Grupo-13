/*
 * (C) Copyright 2016-2017, by Dimitrios Michail and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package org.jgrapht.io;

import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.jgrapht.Graph;
import org.jgrapht.alg.util.Pair;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Imports a graph from a GraphML data source.
 * 
 * <p>
 * This is a simple implementation with supports only a limited set of features of the GraphML
 * specification. For a more rigorous parser use {@link GraphMLImporter}. This version is oriented
 * towards parsing speed.
 * 
 * 
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 * 
 * @author Dimitrios Michail
 */
public class SimpleGraphMLImporter<V, E>
    implements
    GraphImporter<V, E>
{
    private static final String GRAPHML_SCHEMA_FILENAME = "graphml.xsd";
    private static final String XLINK_SCHEMA_FILENAME = "xlink.xsd";

    private boolean schemaValidation;

    private Optional<BiConsumer<Pair<Graph<V, E>, String>, Attribute>> graphAttributeConsumer;
    private Optional<BiConsumer<Pair<V, String>, Attribute>> vertexAttributeConsumer;
    private Optional<BiConsumer<Pair<E, String>, Attribute>> edgeAttributeConsumer;

    /**
     * Constructs a new importer.
     */
    public SimpleGraphMLImporter()
    {
        this(null, null, null);
    }

    /**
     * Constructs a new importer.
     * 
     * @param vertexAttributeConsumer consumer for vertex attributes. The consumer will receive as a
     *        first argument a composite containing the graph vertex together with the attribute
     *        key. The second argument will be the actual attribute.
     * @param edgeAttributeConsumer consumer for edge attributes. The consumer will receive as a
     *        first argument a composite containing the graph edge together with the attribute key.
     *        The second argument will be the actual attribute.
     * @param graphAttributeConsumer consumer for graph attributes. The consumer will receive as a
     *        first argument a composite containing the graph together with the attribute key. The
     *        second argument will be the actual attribute.
     */
    public SimpleGraphMLImporter(
        BiConsumer<Pair<V, String>, Attribute> vertexAttributeConsumer,
        BiConsumer<Pair<E, String>, Attribute> edgeAttributeConsumer,
        BiConsumer<Pair<Graph<V, E>, String>, Attribute> graphAttributeConsumer)
    {
        this.schemaValidation = true;
        this.vertexAttributeConsumer = Optional.ofNullable(vertexAttributeConsumer);
        this.edgeAttributeConsumer = Optional.ofNullable(edgeAttributeConsumer);
        this.graphAttributeConsumer = Optional.ofNullable(graphAttributeConsumer);
    }

    /**
     * Whether the importer validates the input
     * 
     * @return true if the importer validates the input
     */
    public boolean isSchemaValidation()
    {
        return schemaValidation;
    }

    /**
     * Set whether the importer should validate the input
     * 
     * @param schemaValidation value for schema validation
     */
    public void setSchemaValidation(boolean schemaValidation)
    {
        this.schemaValidation = schemaValidation;
    }

    /**
     * Import a graph.
     * 
     * <p>
     * The provided graph must be able to support the features of the graph that is read. For
     * example if the GraphML file contains self-loops then the graph provided must also support
     * self-loops. The same for multiple edges.
     * 
     * @param graph the output graph
     * @param input the input reader
     * @throws ImportException in case an error occurs, such as I/O or parse error
     */
    @Override
    public void importGraph(Graph<V, E> graph, Reader input)
        throws ImportException
    {
        try {
            // parse
            XMLReader xmlReader = createXMLReader();
            GraphMLHandler handler = new GraphMLHandler(graph);
            xmlReader.setContentHandler(handler);
            xmlReader.setErrorHandler(handler);
            xmlReader.parse(new InputSource(input));
        } catch (Exception se) {
            throw new ImportException("Failed to parse GraphML", se);
        }
    }

    private XMLReader createXMLReader()
        throws ImportException
    {
        try {
            SchemaFactory schemaFactory =
                SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            // create parser
            SAXParserFactory spf = SAXParserFactory.newInstance();
            if (schemaValidation) {
                // load schema
                InputStream xsdStream =
                    Thread.currentThread().getContextClassLoader().getResourceAsStream(
                        GRAPHML_SCHEMA_FILENAME);
                if (xsdStream == null) {
                    throw new ImportException("Failed to locate GraphML xsd");
                }
                InputStream xlinkStream =
                    Thread.currentThread().getContextClassLoader().getResourceAsStream(
                        XLINK_SCHEMA_FILENAME);
                if (xlinkStream == null) {
                    throw new ImportException("Failed to locate XLink xsd");
                }
                Source[] sources = new Source[2];
                sources[0] = new StreamSource(xlinkStream);
                sources[1] = new StreamSource(xsdStream);
                Schema schema = schemaFactory.newSchema(sources);

                spf.setSchema(schema);
            }
            spf.setNamespaceAware(true);
            SAXParser saxParser = spf.newSAXParser();

            // create reader
            return saxParser.getXMLReader();
        } catch (Exception se) {
            throw new ImportException("Failed to parse GraphML", se);
        }
    }

    // content handler
    private class GraphMLHandler
        extends
        DefaultHandler
    {
        private static final String GRAPH = "graph";
        private static final String GRAPH_ID = "id";
        private static final String GRAPH_EDGE_DEFAULT = "edgedefault";
        private static final String NODE = "node";
        private static final String NODE_ID = "id";
        private static final String EDGE = "edge";
        private static final String EDGE_ID = "id";
        private static final String EDGE_SOURCE = "source";
        private static final String EDGE_TARGET = "target";
        private static final String ALL = "all";
        private static final String KEY = "key";
        private static final String KEY_FOR = "for";
        private static final String KEY_ATTR_NAME = "attr.name";
        private static final String KEY_ATTR_TYPE = "attr.type";
        private static final String KEY_ID = "id";
        private static final String DEFAULT = "default";
        private static final String DATA = "data";
        private static final String DATA_KEY = "key";

        private Graph<V, E> graph;
        private Map<String, V> nodes;

        // parser state
        private int insideDefault;
        private int insideKey;
        private int insideData;
        private int insideGraph;
        private int insideNode;
        private V currentNode;
        private int insideEdge;
        private E currentEdge;
        private Key currentKey;
        private String currentDataKey;
        private String currentDataValue;
        private Map<String, Key> nodeValidKeys;
        private Map<String, Key> edgeValidKeys;
        private Map<String, Key> graphValidKeys;

        public GraphMLHandler(Graph<V, E> graph)
        {
            this.graph = Objects.requireNonNull(graph);
        }

        @Override
        public void startDocument()
            throws SAXException
        {
            nodes = new HashMap<>();
            insideDefault = 0;
            insideKey = 0;
            insideData = 0;
            insideGraph = 0;
            insideNode = 0;
            currentNode = null;
            insideEdge = 0;
            currentEdge = null;
            currentKey = null;
            currentDataKey = null;
            currentDataValue = null;
            nodeValidKeys = new HashMap<>();
            edgeValidKeys = new HashMap<>();
            graphValidKeys = new HashMap<>();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException
        {
            switch (localName) {
            case GRAPH:
                if (insideGraph > 0) {
                    throw new IllegalArgumentException(
                        "This importer does not support nested graphs");
                }
                insideGraph++;
                findAttribute(GRAPH_ID, attributes)
                    .ifPresent(value -> notifyGraphAttribute(graph, GRAPH_ID, value));
                findAttribute(GRAPH_EDGE_DEFAULT, attributes)
                    .ifPresent(value -> notifyGraphAttribute(graph, GRAPH_EDGE_DEFAULT, value));
                break;
            case NODE:
                if (insideNode > 0 || insideEdge > 0) {
                    throw new IllegalArgumentException(
                        "Nodes cannot be inside other nodes or edges");
                }
                insideNode++;
                String nodeId = findAttribute(NODE_ID, attributes).orElseThrow(
                    () -> new IllegalArgumentException("Node must have an identifier"));
                V vertex = nodes.get(nodeId);
                if (vertex == null) {
                    vertex = graph.addVertex();
                    nodes.put(nodeId, vertex);
                }
                currentNode = vertex;
                notifyVertexAttribute(currentNode, NODE_ID, nodeId);
                break;
            case EDGE:
                if (insideNode > 0 || insideEdge > 0) {
                    throw new IllegalArgumentException(
                        "Edges cannot be inside other nodes or edges");
                }
                insideEdge++;
                String sourceId = findAttribute(EDGE_SOURCE, attributes)
                    .orElseThrow(() -> new IllegalArgumentException("Edge source missing"));
                String targetId = findAttribute(EDGE_TARGET, attributes)
                    .orElseThrow(() -> new IllegalArgumentException("Edge target missing"));
                V source = nodes.computeIfAbsent(sourceId, k -> graph.addVertex());
                V target = nodes.computeIfAbsent(targetId, k -> graph.addVertex());
                currentEdge = graph.addEdge(source, target);
                notifyEdgeAttribute(currentEdge, EDGE_SOURCE, sourceId);
                notifyEdgeAttribute(currentEdge, EDGE_TARGET, targetId);
                findAttribute(EDGE_ID, attributes)
                    .ifPresent(value -> notifyEdgeAttribute(currentEdge, EDGE_ID, value));
                break;
            case KEY:
                insideKey++;
                String keyId = findAttribute(KEY_ID, attributes)
                    .orElseThrow(() -> new IllegalArgumentException("Key id missing"));
                String keyAttrName = findAttribute(KEY_ATTR_NAME, attributes)
                    .orElseThrow(() -> new IllegalArgumentException("Key attribute name missing"));
                currentKey = new Key(
                    keyId, keyAttrName, findAttribute(KEY_ATTR_TYPE, attributes)
                        .map(AttributeType::create).orElse(AttributeType.STRING),
                    findAttribute(KEY_FOR, attributes).orElse("ALL"));
                break;
            case DEFAULT:
                insideDefault++;
                break;
            case DATA:
                insideData++;
                findAttribute(DATA_KEY, attributes).ifPresent(data -> currentDataKey = data);
                break;
            default:
                break;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName)
            throws SAXException
        {
            switch (localName) {
            case GRAPH:
                insideGraph--;
                break;
            case NODE:
                currentNode = null;
                insideNode--;
                break;
            case EDGE:
                currentEdge = null;
                insideEdge--;
                break;
            case KEY:
                insideKey--;
                registerKey();
                currentKey = null;
                break;
            case DEFAULT:
                insideDefault--;
                break;
            case DATA:
                if (--insideData == 0) {
                    notifyData();
                    currentDataValue = null;
                    currentDataKey = null;
                }
                break;
            default:
                break;
            }
        }

        @Override
        public void characters(char ch[], int start, int length)
            throws SAXException
        {
            if (insideData == 1) {
                currentDataValue = new String(ch, start, length);
            }
        }

        @Override
        public void warning(SAXParseException e)
            throws SAXException
        {
            throw e;
        }

        public void error(SAXParseException e)
            throws SAXException
        {
            throw e;
        }

        public void fatalError(SAXParseException e)
            throws SAXException
        {
            throw e;
        }

        private Optional<String> findAttribute(String localName, Attributes attributes)
        {
            for (int i = 0; i < attributes.getLength(); i++) {
                String attrLocalName = attributes.getLocalName(i);
                if (attrLocalName.equals(localName)) {
                    return Optional.ofNullable(attributes.getValue(i));
                }
            }
            return Optional.empty();
        }

        private void notifyVertexAttribute(V v, String key, String value)
        {
            if (value != null) {
                vertexAttributeConsumer.ifPresent(
                    c -> c.accept(Pair.of(v, key), DefaultAttribute.createAttribute(value)));
            }
        }

        private void notifyEdgeAttribute(E e, String key, String value)
        {
            if (value != null) {
                edgeAttributeConsumer.ifPresent(
                    c -> c.accept(Pair.of(e, key), DefaultAttribute.createAttribute(value)));
            }
        }

        private void notifyGraphAttribute(Graph<V, E> g, String key, String value)
        {
            if (value != null) {
                graphAttributeConsumer.ifPresent(
                    c -> c.accept(Pair.of(g, key), DefaultAttribute.createAttribute(value)));
            }
        }

        private void notifyData()
        {
            if (currentDataKey == null || currentDataValue == null) {
                return;
            }

            if (currentNode != null) {
                Key key = nodeValidKeys.get(currentDataKey);
                if (key != null) {
                    vertexAttributeConsumer.ifPresent(
                        c -> c.accept(
                            Pair.of(currentNode, key.attributeName),
                            new DefaultAttribute<>(currentDataValue, key.type)));
                }
            }
            if (currentEdge != null) {
                Key key = edgeValidKeys.get(currentDataKey);
                if (key != null) {
                    edgeAttributeConsumer.ifPresent(
                        c -> c.accept(
                            Pair.of(currentEdge, key.attributeName),
                            new DefaultAttribute<>(currentDataValue, key.type)));
                }
            } 
            if (graph != null) {
                Key key = graphValidKeys.get(currentDataKey);
                if (key != null) {
                    graphAttributeConsumer.ifPresent(
                        c -> c.accept(
                            Pair.of(graph, key.attributeName),
                            new DefaultAttribute<>(currentDataValue, key.type)));
                }
            }
        }

        private void registerKey()
        {
            if (currentKey.isValid()) {
                switch (currentKey.target) {
                case NODE:
                    nodeValidKeys.put(currentKey.id, currentKey);
                    break;
                case EDGE:
                    edgeValidKeys.put(currentKey.id, currentKey);
                    break;
                case GRAPH:
                    graphValidKeys.put(currentKey.id, currentKey);
                    break;
                case ALL:
                    nodeValidKeys.put(currentKey.id, currentKey);
                    edgeValidKeys.put(currentKey.id, currentKey);
                    graphValidKeys.put(currentKey.id, currentKey);
                    break;
                }
            }
        }

    }

    private static class Key
    {
        String id;
        String attributeName;
        String target;
        AttributeType type;

        public Key(String id, String attributeName, AttributeType type, String target)
        {
            this.id = id;
            this.attributeName = attributeName;
            this.type = type;
            this.target = target;
        }

        public boolean isValid()
        {
            return id != null && attributeName != null && target != null;
        }

    }

}
