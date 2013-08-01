package org.jgrapht.alg;

import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.alg.util.UnionFind;

/**
 * Used to calculate Tarjan's Lowest Common Ancestors Algorithm
 * 
 * @author Leo Crawford
 */

public class TarjanLowestCommonAncestor<V, E> {

    private Graph<V, E> g;

    /** Create an instance with a reference to the graph that we will find LCAs for */
    TarjanLowestCommonAncestor(Graph<V, E> g) {
	this.g = g;
    }

    /**
     * Calculate the LCM between <code>a</code> and <code>b</code> treating <code>start</code> as the root we want to
     * search from.
     */
    public V calculate(V start, V a, V b) {
	LcaRequestResponse<V> lrr = new LcaRequestResponse<V>(a, b);
	new Worker(lrr).calculate(start);
	return lrr.getLca();
    }
    
    /**
     * Calculate the LCMs between a set of pairs (<code>a</code> and <code>b</code>) treating <code>start</code> as the root we want to
     * search from, and setting the LCA of each pair in its LCA field
     */
    public void calculate(V start, LcaRequestResponse<V>... lrr) {
	new Worker(lrr).calculate(start);
    }

    /* The worker class keeps the state whilst doing calculations. */
    private class Worker {

	// The implementation of makeFind as referred to by <block>It uses the MakeSet, Find, and Union functions of a
	// disjoint-set forest. MakeSet(u) removes u to a singleton set, Find(u) returns the standard representative of
	// the set containing u, and Union(u,v) merges the set containing u with the set containing v. </block>
	// (http://en.wikipedia.org/wiki/Tarjan's_off-line_lowest_common_ancestors_algorithm)
	private UnionFind<V> uf = new UnionFind<V>(Collections.<V> emptySet());
	// the ancestors. instead of <code>u.ancestor = x</code> we do <code>ancestors.put(u,x)</code>
	private Map<V, V> ancestors = new HashMap<V, V>();
	// instead of u.colour = black we do black.add(u)
	private Set<V> black = new HashSet<V>();
	// the two vertex that we want to find the LCA for
	private LcaRequestResponse<V>[] lrr;

	private Worker(LcaRequestResponse<V>... lrr) {
	    this.lrr = lrr;
	}

	/**
	 * Calculates the LCM as described by
	 * http://en.wikipedia.org/wiki/Tarjan's_off-line_lowest_common_ancestors_algorithm<code>
	 function TarjanOLCA(u)
	   MakeSet(u);
	   u.ancestor := u;
	   for each v in u.children do
	     TarjanOLCA(v);
	     Union(u,v);
	     Find(u).ancestor := u;
	   u.colour := black;
	   for each v such that {u,v} in P do
	     if v.colour == black
	       print "Tarjan's Lowest Common Ancestor of " + u + " and " + v + " is " + Find(v).ancestor + ".";
	</code>
	 * 
	 * @param u
	 *            the starting node (called recursively)
	 * @return the LCM if found, if not null
	 */
	private void calculate(final V u) {
	    uf.addElement(u);
	    ancestors.put(u, u);
	    for (E vEdge : g.edgesOf(u)) {
		if (g.getEdgeSource(vEdge).equals(u)) {
		    V v = g.getEdgeTarget(vEdge);
		    calculate(v);
		    uf.union(u, v);
		    ancestors.put(uf.find(u), u);
		}
		black.add(u);
		for (LcaRequestResponse<V> current : lrr)
		    if (current.getLca() == null) {
			if (black.contains(current.getA()) && current.getB().equals(u)) {
			    current.setLca(ancestors.get(uf.find(current.getA())));
			}
			if (black.contains(current.getB()) && current.getA().equals(u)) {
			    current.setLca(ancestors.get(uf.find(current.getB())));
			}
		    }
	    }
	}
    }

    public static class LcaRequestResponse<V> {
	private V a, b, lca;

	public LcaRequestResponse(V a, V b) {
	    this.a = a;
	    this.b = b;
	}

	public V getA() {
	    return a;
	}

	public V getB() {
	    return b;
	}

	public V getLca() {
	    return lca;
	}

	void setLca(V lca) {
	    this.lca = lca;
	}

    }
}
