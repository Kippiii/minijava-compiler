package RegisterAllocator;

import assem.Instruction;
import assem.LabelInstruction;
import graph.AbstractAssemFlowGraph;
import graph.Node;
import tree.NameOfLabel;
import tree.NameOfTemp;

import java.util.*;

public class AssemFlowGraph extends AbstractAssemFlowGraph {
    /**
     * Represents a graph where nodes represent assembly instructions and edges represent possible instruction orderings
     * @param assembly - The list of instructions in the assembly
     * @param temps - The list of temporaries used in the assembly
     * @param nodes - The list of nodes in the graph
     * @param instMap - A map from node strings to their corresponding instructions
     * @param labelMap - A mapping from labels to their corresponding nodes
     * @param in - The list of live-in temporaries at each node
     * @param out - The list of live-out temporaries at each node
     */
    List<Instruction> assembly;
    List<NameOfTemp> temps;
    List<Node> nodes;
    Map<String, Instruction> instMap;
    Map<NameOfLabel, Node> labelMap;
    List<Set<NameOfTemp>> in;
    List<Set<NameOfTemp>> out;

    private Node newNode(Instruction inst) {
        /**
         * Creates a new node in the graph to represent a given instruction
         * @param inst - The instruction in question
         * @return The new node corresponding to that instruction
         */
        Node n = this.newNode();
        instMap.put(n.toString(), inst);
        if (inst instanceof LabelInstruction) {
            this.labelMap.put(((LabelInstruction) inst).label, n);
        }
        return n;
    }

    public AssemFlowGraph(List<Instruction> assembly, List<NameOfTemp> temps) {
        this.assembly = assembly;
        this.temps = temps;
        this.nodes = new ArrayList<Node>();
        this.instMap = new HashMap<String, Instruction>();
        this.labelMap = new HashMap<NameOfLabel, Node>();
        this.in = new ArrayList<Set<NameOfTemp>>();
        this.out = new ArrayList<Set<NameOfTemp>>();
        for (Instruction inst : assembly) {
            Node n = this.newNode(inst);
            nodes.add(n);
        }
        this.addEdges();
        this.genLiveliness();
    }

    public void addEdges() {
        /**
         * Iterates over the assembly code of this graph to create edges between instructions that could follow the other
         */
        for (int i = 0; i < this.assembly.size(); i++) {
            Instruction fromInst = this.assembly.get(i);
            Node fromNode = this.nodes.get(i);
            if (fromInst.jumps() != null && fromInst.jumps().size() > 0) {
                for (NameOfLabel label : fromInst.jumps()) {
                    Node toNode = this.labelMap.get(label);
                    this.addEdge(fromNode, toNode);
                }
            } else {
                if (i != this.assembly.size() - 1) {
                    Node toNode = this.nodes.get(i+1);
                    this.addEdge(fromNode, toNode);
                }
            }
        }
    }

    public void genLiveliness() {
        /**
         * Populates the live-in and live-out temporaries for each instruction
         */
        for (int i = 0; i < this.assembly.size(); i++) {
            this.in.add(new HashSet<NameOfTemp>());
            this.out.add(new HashSet<NameOfTemp>());
        }
        int curNum = 0;
        int added = 0;
        do {
            added = 0;
            int newNum = 0;
            for (int i = 0; i < this.assembly.size(); i++) {
                Node n = this.nodes.get(i);
                for (NameOfTemp t : this.use(n)) {
                    if (t != null) {
                        this.in.get(i).add(t);
                    }
                }
                for (NameOfTemp t : this.out.get(i)) {
                    if (t != null && !this.def(n).contains(t)) {
                        this.in.get(i).add(t);
                    }
                }

                for (Node s : n.succ()) {
                    for (NameOfTemp t : this.in.get(this.nodes.indexOf(s))) {
                        if (t != null) {
                            this.out.get(i).add(t);
                        }
                    }
                }
                newNum += this.in.get(i).size() + this.out.get(i).size();
            }
            added = newNum - curNum;
            curNum = newNum;
        } while (added > 0);
    }

    public Instruction instruction(Node n) {
        /**
         * Returns the instruction that corresponds to each node
         * @param n - The node in question
         * @return The instruction corresponding to the node
         */
        return this.instMap.get(n.toString());
    }

    @Override
    public String toString() {
        /**
         * Creats a string version of liveliness info of the graph for use with debugging
         * @return The string version of the liveliness info
         */
        String s = "";
        for (int i = 0; i < this.assembly.size(); i++) {
            s += i + "(" + this.assembly.get(i).format() + "):\n";
            s += "\tIn:";
            for (NameOfTemp t : this.in.get(i)) {
                s += t.toString() + ", ";
            }
            s += "\n";
            s += "\tOut:";
            for (NameOfTemp t : this.out.get(i)) {
                s += t.toString() + ", ";
            }
            s += "\n";
        }
        return s;
    }

}
