package RegisterAllocator;

import graph.InterferenceGraph;
import graph.Node;
import tree.NameOfTemp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class AssemInterferenceGraph extends InterferenceGraph {
    /**
     * Represents an interference graph between temporaries, where temporaries are node, and there is an edge between
     * them if the two temporaries cannot populate the same register
     * @param flowGraph - The flow graph corresponding to the method
     * @param temps - The list of temporaries
     * @param neighbors - The number of neighbors that each temporary has in the graph
     * @param maxNeighbors - The maximum number of neighbors that one temporary can have
     * @param removed - A stack containing the temporaries that are currently removed form the graph
     * @param colors - A list of all colors that temporaries can be colored with
     * @param coloring - A map from each temporary to a color (the output)
     */
    AssemFlowGraph flowGraph;
    List<NameOfTemp> temps;
    Map<NameOfTemp, Integer> neighbors;
    int maxNeighbors;
    Stack<NameOfTemp> removed;
    List<String> colors;
    Map<NameOfTemp, String> coloring;

    public AssemInterferenceGraph(AssemFlowGraph flowGraph) {
        this.flowGraph = flowGraph;
        this.temps = flowGraph.temps;
        this.build();
    }

    public Map<NameOfTemp, String> color(List<String> colors) {
        /**
         * The main driving code. It attempts to color every node in the graph such that there is not an edge between
         * two nodes of the same color
         * @param colors - The list of all possible colors
         * @return A mapping from temporaries to colors (the coloring)
         */
        this.neighbors = new HashMap<NameOfTemp, Integer>();
        for (NameOfTemp t : this.temps) {
            this.neighbors.put(t, this.getNode(t).degree());
        }
        this.maxNeighbors = colors.size();
        this.removed = new Stack<NameOfTemp>();
        this.colors = colors;
        this.coloring = new HashMap<NameOfTemp, String>();
        for (NameOfTemp t : this.temps) {
            this.coloring.put(t, null);
        }

        for (NameOfTemp t : this.temps) {
            if (this.colors.contains(t.toString())) {
                this.coloring.replace(t, t.toString());
            }
        }

        while (this.simplify());
        for (NameOfTemp t : this.temps) {
            if (this.coloring.get(t) == null && !this.removed.contains(t)) {
                // TODO ERROR (Non-precolored node left)
                System.err.println(t.toString() + " not added to stack");
                return null;
            }
        }

        while (this.select());
        if (!this.removed.empty()) {
            // TODO ERROR (Node not colored)
            System.err.println("Some nodes not colored");
            return null;
        }

        return this.coloring;
    }

    void build() {
        /**
         * Builds the interference graph from the flow graph
         */
        for (NameOfTemp t : this.temps) {
            this.ensureNode(t);
        }
        for (int i = 0; i < this.flowGraph.nodes.size(); i++) {
            Node n = this.flowGraph.nodes.get(i);
            for (NameOfTemp t1 : this.flowGraph.out.get(i)) {
                if (t1 == null) {
                    continue;
                }
                for (NameOfTemp t2 : this.flowGraph.out.get(i)) {
                    if (t2 == null || t1 == t2) {
                        continue;
                    }
                    this.addEdge(this.getNode(t1), this.getNode(t2));
                }
            }
        }
    }

    boolean simplify() {
        /**
         * Removes a node from the graph that is not precolored and has less than `maxNeighbors' neighbors
         * @return True if a node was successfully removed from the graph
         */
        for (NameOfTemp t : temps) {
            if (this.isPrecolored(this.getNode(t)))
                continue;
            if (this.neighbors.get(t) == -1)
                continue;
            if (this.neighbors.get(t) >= this.maxNeighbors)
                continue;
            for (Node adj : this.getNode(t).succ()) {
                if (this.neighbors.get(this.getTemp(adj)) >= 0) {
                    this.neighbors.replace(this.getTemp(adj), this.neighbors.get(this.getTemp(adj)) - 1);
                }
            }
            this.neighbors.replace(t, -1);
            this.removed.push(t);
            return true;
        }
        return false;
    }

    boolean isPrecolored(Node n) {
        /**
         * Determines if a node is precolored
         * @param n - The node in question
         * @return Whether the node is precolored
         */
        return this.coloring.get(this.getTemp(n)) != null;
    }

    boolean select() {
        /**
         * Removes a node from the stack, puts it back into the graph, and greedily colors it
         * @return True if an element was successfully added to the graph and colored such that there are no conflicts
         */
        if (this.removed.empty())
            return false;
        NameOfTemp cur = this.removed.pop();
        for (String color : this.colors) {
            if (this.attemptColor(cur, color)) {
                return true;
            }
        }
        this.removed.push(cur);
        return false;
    }

    boolean attemptColor(NameOfTemp t, String color) {
        /**
         * Attempts to color a temporary with a given color
         * @param t - The temporary attempting to be colored
         * @param color - The color that the temporary is attempting to be colored with
         * @return Whether the temporary was successfully colored
         */
        for (Node adj : this.getNode(t).succ()) {
            NameOfTemp adjT = this.getTemp(adj);
            if (this.coloring.get(adjT) != null && this.coloring.get(adjT).equals(color)) {
                return false;
            }
        }
        this.coloring.replace(t, color);
        return true;
    }

}
