package similarity;

//package org.jgrapht.demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jgrapht.*;
import org.jgrapht.graph.*;

public class Similarity {

	public static void main(String[] args) {
		Similarity h = new Similarity();
		String values[] = new String[] { "0.01", "0.05", "0.1", "0.2", "0.5",
				"0.8" };

		for (int i = 0; i < values.length; i++) {
			String filename = "data/missing_connections_random_" + values[i]+ ".txt";

			System.out.println("FILENAME "+ filename);
			DirectedGraph<String, DefaultEdge> g2 = h.readGraph(filename);
			DirectedGraph<String, DefaultEdge> g1 = h
					.readGraph("data/web-Stanford.txt");

			System.out.println("Vertex Edge Overlap between the Graphs is  "
					+ h.vertexEdgeOverlap(g1, g2));

			HashMap<String, Double> pageRank1 = h.PageRankTopology(g1);
			HashMap<String, Double> pageRank2 = h.PageRankTopology(g2);
			HashMap<String, Integer> ranking1 = h.rank(pageRank1); // Sorts the
																	// values
			HashMap<String, Integer> ranking2 = h.rank(pageRank2);
			System.out.println("Vertex Ranking Overlap between the Graphs is  "
					+ h.VertexRanking(ranking1, ranking2, g1, g2, pageRank1,
							pageRank2));

		}

	}

	public double maxPageRank(HashMap<String, Double> pageRank1) {
		Double maxPageRank = 0.0;
		Double pageRank;
		for (Map.Entry<String, Double> entry : pageRank1.entrySet()) {
			pageRank = entry.getValue();
			if (pageRank > maxPageRank)
				maxPageRank = pageRank;

		}

		return maxPageRank;
	}

	public double VertexRanking(HashMap<String, Integer> ranking1,
			HashMap<String, Integer> ranking2,
			DirectedGraph<String, DefaultEdge> g1,
			DirectedGraph<String, DefaultEdge> g2,
			HashMap<String, Double> pageRank1, HashMap<String, Double> pageRank2) {
		double sum = 0.0;
		Set<String> nodes1 = g1.vertexSet();
		int numberOfNodes = 0;
		Double maxPageRank = 0.0;

		for (String s : nodes1) {
			Double pageRank;
			if (g2.containsVertex(s)) {
				pageRank = (pageRank1.get(s) + pageRank2.get(s)) / 2;
				sum += (Math.pow((double) (ranking1.get(s) - ranking2.get(s)),
						2.0)) * pageRank;

			}

			else {

				pageRank = pageRank1.get(s);
				sum += (Math.pow((double) (ranking1.get(s)
						- g2.vertexSet().size() - 1), 2.0))
						* pageRank;

			}
			if (pageRank > maxPageRank)
				maxPageRank = pageRank;
			numberOfNodes++;

			// System.out.println(" Sum " + sum);

		}
		Set<String> nodes2 = g2.vertexSet();
		for (String s : nodes2) {
			if (!g1.containsVertex(s)) {
				Double pageRank = pageRank2.get(s);
				sum += Math.pow(
						(double) (g1.vertexSet().size() + 1 - ranking2.get(s)),
						2.0) * pageRank;
				if (pageRank > maxPageRank)
					maxPageRank = pageRank;
				numberOfNodes++;
				// System.out.println(" Sum " + sum);

			}
		}

		Double D = ((numberOfNodes * (Math.pow(numberOfNodes, 2.0) - 1)) / 3.0)
				* maxPageRank;

		sum = 2 * sum / D;

		System.out.println("Number of nodes " + numberOfNodes + " Sum " + sum
				+ " Max page Rank " + maxPageRank);

		return (1 - sum);

	}

	public HashMap<String, Integer> rank(HashMap<String, Double> ranking) {
		TreeMap<String, Double> sorted_ranking = this.SortedList(ranking);
		int i = 1;
		HashMap<String, Integer> ranking_v = new HashMap<String, Integer>();
		for (Map.Entry<String, Double> entry : sorted_ranking.entrySet()) {
			String key = entry.getKey();
			ranking_v.put(key, i);
			i++;
		}

		return ranking_v;
	}

	public HashMap<Integer, Integer> rankReverse(HashMap<String, Double> ranking) {
		TreeMap<String, Double> sorted_ranking = this.SortedList(ranking);
		int i = 1;
		HashMap<Integer, Integer> ranking_v = new HashMap<Integer, Integer>();
		for (Map.Entry<String, Double> entry : sorted_ranking.entrySet()) {
			String key = entry.getKey();
			ranking_v.put(i, Integer.parseInt(key));
			i++;
		}

		return ranking_v;
	}

	public DirectedGraph<String, DefaultEdge> readGraph(String filename) {
		DirectedGraph<String, DefaultEdge> g = new DefaultDirectedGraph<String, DefaultEdge>(
				DefaultEdge.class);
		BufferedReader br = null;

		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader(filename));

			while ((sCurrentLine = br.readLine()) != null) {
				String[] edge = sCurrentLine.split("\t");
				if (!g.containsVertex(edge[0])) {
					g.addVertex(edge[0]);
				}
				if (!g.containsVertex(edge[1])) {
					g.addVertex(edge[1]);
				}
				g.addEdge(edge[0], edge[1]);

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return g;
	}

	public double vertexEdgeOverlap(DirectedGraph<String, DefaultEdge> g1,
			DirectedGraph<String, DefaultEdge> g2) {

		int n1 = g1.vertexSet().size();
		int n2 = g2.vertexSet().size();
		long e1 = g1.edgeSet().size();
		long e2 = g2.edgeSet().size();
		int commonEdges = 0;
		int commonVertices = 0;
		double vertexEdgeOverlap = 0.0;

		Set<String> nodes = g1.vertexSet();
		for (String s : nodes) {

			if (g2.containsVertex(s))
				commonVertices++;
		}

		Set<DefaultEdge> edgeset1 = g1.edgeSet();

		for (DefaultEdge ed1 : edgeset1) {
			String v1 = g1.getEdgeSource(ed1);
			String v2 = g1.getEdgeTarget(ed1);

			if (g2.containsEdge(v1, v2))
				commonEdges++;

		}

		vertexEdgeOverlap = 2 * (double) (commonVertices + commonEdges)
				/ (n1 + n2 + e1 + e2);
		System.out.println("N1 = " + n1 + " N2 " + n2 + " E1 " + e1 + " E2 "
				+ e2 + " Common Edges " + commonEdges + " Common vertices "
				+ commonVertices + " Answer " + vertexEdgeOverlap);

		return vertexEdgeOverlap;

	}

	public HashMap<String, Double> PageRankTopology(
			DirectedGraph<String, DefaultEdge> topology) {
		HashMap<String, Double> ranking = new HashMap<String, Double>();

		for (String vertex : topology.vertexSet()) {
			double value;
			if (topology.outDegreeOf(vertex) == 0)
				value = 0;
			else
				value = (double) (1 / topology.outDegreeOf(vertex));
			ranking.put(vertex, value);
		}

		for (int i = 1; i < 20; i++) {
			HashMap<String, Double> lasIterationRanking = new HashMap<String, Double>(
					ranking);
			for (String vertex : topology.vertexSet()) {
				Set<DefaultEdge> inbound = topology.incomingEdgesOf(vertex);
				double iterationRanking = 0.0;
				for (DefaultEdge defaultEdge : inbound) {
					String inboundVertex = vertex.equals(topology
							.getEdgeSource(defaultEdge)) ? topology
							.getEdgeTarget(defaultEdge) : topology
							.getEdgeSource(defaultEdge);

					iterationRanking += lasIterationRanking.get(inboundVertex)
							/ topology.outDegreeOf(inboundVertex);
				}
				ranking.put(vertex, iterationRanking);
			}
		}

		return ranking;

	}

	public TreeMap<String, Double> SortedList(HashMap<String, Double> ranking) {
		ValueComparator bvc = new ValueComparator(ranking);
		TreeMap<String, Double> sorted_map = new TreeMap<String, Double>(bvc);
		sorted_map.putAll(ranking);
		return sorted_map;
	}
}

class ValueComparator implements Comparator<String> {

	Map<String, Double> base;

	public ValueComparator(Map<String, Double> base) {
		this.base = base;
	}

	// Note: this comparator imposes orderings that are inconsistent with
	// equals.
	public int compare(String a, String b) {
		if (base.get(a) <= base.get(b)) {
			return -1;
		} else {
			return 1;
		} // returning 0 would merge keys
	}
}
