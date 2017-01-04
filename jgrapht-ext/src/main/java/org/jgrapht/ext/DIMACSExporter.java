/*
 * (C) Copyright 2016-2016, by Dimitrios Michail and Contributors.
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
package org.jgrapht.ext;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.jgrapht.Graph;

/**
 * Exports a graph into DIMACS format.
 *
 * <p>
 * For a description of the format see <a href="http://dimacs.rutgers.edu/Challenges/">
 * http://dimacs.rutgers.edu/Challenges</a>.
 * </p>
 * 
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Dimitrios Michail
 */
public class DIMACSExporter<V, E>
    implements GraphExporter<V, E>
{
    private final static String DEFAULT_DIMACS_PROBLEM = "sp";

    private final ComponentNameProvider<V> vertexIDProvider;
    private final Set<Parameter> parameters;
    private final String problem;

    /**
     * Parameters that affect the behavior of the exporter.
     */
    public enum Parameter
    {
        /**
         * If set the exporter outputs edge weights
         */
        EXPORT_EDGE_WEIGHTS
    }

    /**
     * Constructs a new exporter.
     */
    public DIMACSExporter()
    {
        this(new IntegerComponentNameProvider<>());
    }

    /**
     * Constructs a new exporter with a given vertex ID provider.
     *
     * @param vertexIDProvider for generating vertex IDs. Must not be null.
     */
    public DIMACSExporter(ComponentNameProvider<V> vertexIDProvider)
    {
        this(vertexIDProvider, DEFAULT_DIMACS_PROBLEM);
    }

    /**
     * Constructs a new exporter with a given vertex ID provider.
     *
     * @param vertexIDProvider for generating vertex IDs. Must not be null.
     * @param problem the DIMACS problem to use, like sp, max, etc.
     */
    public DIMACSExporter(ComponentNameProvider<V> vertexIDProvider, String problem)
    {
        this.vertexIDProvider =
            Objects.requireNonNull(vertexIDProvider, "Vertex id provider cannot be null");
        this.problem = Objects.requireNonNull(problem, "DIMACS problem cannot be null");
        this.parameters = new HashSet<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exportGraph(Graph<V, E> g, Writer writer)
    {
        PrintWriter out = new PrintWriter(writer);

        out.println("c");
        out.println("c Generated using JGraphT");
        out.println("c");
        out.println("p " + problem + " " + g.vertexSet().size() + " " + g.edgeSet().size());

        boolean exportEdgeWeights = parameters.contains(Parameter.EXPORT_EDGE_WEIGHTS);

        for (E edge : g.edgeSet()) {
            out.print("a ");
            out.print(vertexIDProvider.getName(g.getEdgeSource(edge)));
            out.print(" ");
            out.print(vertexIDProvider.getName(g.getEdgeTarget(edge)));
            if (exportEdgeWeights) {
                out.print(" ");
                out.print(Double.toString(g.getEdgeWeight(edge)));
            }
            out.println();
        }

        out.flush();
    }

    /**
     * Return if a particular parameter of the exporter is enabled
     * 
     * @param p the parameter
     * @return {@code true} if the parameter is set, {@code false} otherwise
     */
    public boolean isParameter(Parameter p)
    {
        return parameters.contains(p);
    }

    /**
     * Set the value of a parameter of the exporter
     * 
     * @param p the parameter
     * @param value the value to set
     */
    public void setParameter(Parameter p, boolean value)
    {
        if (value) {
            parameters.add(p);
        } else {
            parameters.remove(p);
        }
    }

}
