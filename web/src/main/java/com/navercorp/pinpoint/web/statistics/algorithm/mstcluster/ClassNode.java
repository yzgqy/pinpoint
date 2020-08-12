package com.navercorp.pinpoint.web.statistics.algorithm.mstcluster;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Getter
@Setter
@ToString
public class ClassNode {
    private String className;
    private boolean visited;
    private List<NodeWeightPair> neighbors;

    public ClassNode() {
    }

    public ClassNode(String className){
        this.className = className;
        this.visited = false;
        this.neighbors = new ArrayList<>();
    }

    public int getDegree(){
        return neighbors.size();
    }

    public double getCombinedWeight(){
        return this.neighbors.stream().mapToDouble(n ->{
            return n.getWeight();
        }).sum();
    }

    public void deleteNeighborWithClassName(String className){
        for(Iterator<NodeWeightPair> iterator = neighbors.iterator(); iterator.hasNext();){
            if(iterator.next().getNode().getClassName().equals(className));
            iterator.remove();
            return;
        }
    }

    public void addNeighborWithWeight(ClassNode neighbor, double score){
        neighbors.add(new NodeWeightPair(neighbor, score));
    }

    public void deleteNeighborWithId(String className,String minNodeName){
        if(this.className.equals(minNodeName)) return;
        if(neighbors.size()>1) {
            for (Iterator<NodeWeightPair> iterator = neighbors.iterator(); iterator.hasNext(); ) {
                NodeWeightPair neighbor = iterator.next();
//                if (neighbor.getNode().getClassName().equals(minNodeName))
//                    continue;
                if (neighbor.getNode().getClassName().equals(className)) {
                    iterator.remove();
                    return;
                }
            }
        }

    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassNode)) return false;

        ClassNode classNode = (ClassNode) o;

        return className.equals(classNode.className);

    }

    @Override
    public int hashCode() {
        return className.hashCode();
    }

    @Override
    public String toString() {
        return "ClassNode{" +
                "className='" + className + '\'' +
                ", visited=" + visited +
                ", neighbors=" + neighbors.size() +
                '}';
    }
}
