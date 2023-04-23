package RegisterAllocator;

import graph.InterferenceGraph;
import graph.Node;
import tree.NameOfTemp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class AssemInterferenceGraph extends InterferenceGraph {
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
        for (NameOfTemp t : this.temps) {
            this.ensureNode(t);
        }
        for (int i = 0; i < this.flowGraph.nodes.size(); i++) {
            Node n = this.flowGraph.nodes.get(i);
            for (NameOfTemp t1 : this.flowGraph.use(n)) {
                if (t1 == null) {
                    continue;
                }
                for (NameOfTemp t2 : this.flowGraph.out.get(i)) {
                    if (t2 == null) {
                        continue;
                    }
                    this.addEdge(this.getNode(t1), this.getNode(t2));
                }
            }
        }
    }

    boolean simplify() {
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
        return this.coloring.get(this.getTemp(n)) != null;
    }

    boolean select() {
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
