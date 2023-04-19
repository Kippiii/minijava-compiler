package RegisterAllocator;

import assem.Instruction;
import assem.LabelInstruction;
import graph.AbstractAssemFlowGraph;
import graph.Node;
import tree.NameOfLabel;
import tree.NameOfTemp;

import java.util.*;

public class AssemFlowGraph extends AbstractAssemFlowGraph {
    private List<Instruction> assembly;
    private List<NameOfTemp> temps;
    private List<Node> nodes;
    Map<String, Instruction> instMap;
    Map<NameOfLabel, Node> labelMap;
    List<Set<NameOfTemp>> in;
    List<Set<NameOfTemp>> out;

    private Node newNode(Instruction inst) {
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
        this.instMap = null;
        this.in = new ArrayList<Set<NameOfTemp>>(assembly.size());
        this.out = new ArrayList<Set<NameOfTemp>>(assembly.size());
        for (Instruction inst : assembly) {
            Node n = this.newNode(inst);
            nodes.add(n);
        }
        this.addEdges();
        this.genLiveliness();
    }

    public void addEdges() {
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
        for (int i = 0; i < this.assembly.size(); i++) {
            this.in.set(i, new HashSet<NameOfTemp>());
            this.out.set(i, new HashSet<NameOfTemp>());
        }
        int curNum = 0;
        int added = 0;
        do {
            added = 0;
            int newNum = 0;
            for (int i = 0; i < this.assembly.size(); i++) {
                Node n = this.nodes.get(i);
                for (NameOfTemp t : this.use(n)) {
                    this.in.get(i).add(t);
                }
                for (NameOfTemp t : this.out.get(i)) {
                    if (!this.def(n).contains(t)) {
                        this.in.get(i).add(t);
                    }
                }

                for (Node s : n.succ()) {
                    for (NameOfTemp t : this.in.get(i)) {
                        this.out.get(i).add(t);
                    }
                }
                newNum += this.in.get(i).size() + this.out.get(i).size();
            }
            added = newNum - curNum;
            curNum = newNum;
        } while (added > 0);
    }

    public Instruction instruction(Node n) {
        // TODO
    }

}
