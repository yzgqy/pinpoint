package com.navercorp.pinpoint.web.statistics.algorithm.mstcluster;

import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.util.*;
import java.util.stream.Collectors;

public class MSTCluster {
    private final static ComponentComparator componentComparator = new ComponentComparator();
    private final static ClassNodeComparator classNodeComparator = new ClassNodeComparator();
    private final static WeightedEdgeComparator weightedEdgeComparator = new WeightedEdgeComparator();

    private MSTCluster(){

    }

    public static Set<Component> clusterWithSplit(SpanningTreeAlgorithm.SpanningTree<WeightedEdge> edges, int splitThreshold, int numServices){
//        return  new HashSet<>(ConnectedComponents.connectedComponents(computeClusters(edges, numServices)));
        List<Component> components = ConnectedComponents.connectedComponents(computeClusters(edges, numServices));

        while (components.size()>0){
            components.sort(componentComparator);

            Collections.reverse(components);

            Component largest = components.get(0);

            if(largest.getSize()>splitThreshold){
                components.remove(0);
                List<Component> split = splitByDegree(largest);
                components.addAll(split);
                if(split.size()==1)
                    return new HashSet<>(components);
            }else{
                return new HashSet<>(components);
            }
        }
        return new HashSet<>(components);
    }

    private static List<Component> splitByDegree(Component component){
        List<ClassNode> nodes = component.getNodes();
        nodes.sort(classNodeComparator);
        Collections.reverse(nodes);

        //所有孤立点
        List<NodeWeightPair> isolateNodes = new ArrayList<>();

        ClassNode nodeToRemove = nodes.get(0);
        List<NodeWeightPair> neighborsRemoveNode = nodeToRemove.getNeighbors();
        NodeWeightPair minNode = new NodeWeightPair();
        double minWeight = 9999999;
        for(NodeWeightPair nodeWeightPair:neighborsRemoveNode){
            if(nodeWeightPair.getWeight()<minWeight){
                minWeight = nodeWeightPair.getWeight();
                minNode = nodeWeightPair;
            }
            if(nodeWeightPair.getNode().getNeighbors().size() == 1){
                isolateNodes.add(nodeWeightPair);
            }
        }
        nodes.remove(0);
        final String minNodeName = minNode.getNode().getClassName();

        nodes.forEach(node -> {
            node.deleteNeighborWithId(nodeToRemove.getClassName(),minNodeName);
        });

        //孤立点中加入权重最小的边
        isolateNodes.add(minNode);
        //设置删除的点的边
        nodeToRemove.setNeighbors(isolateNodes);
        //加入图中
        nodes.add(nodeToRemove);

        List<Component> connectedComponents = ConnectedComponents.connectedComponentsFromNodes(nodes);

        return connectedComponents.stream().filter(c -> c.getSize() > 1).collect(Collectors.toList());
    }

    public static List<WeightedEdge> computeClusters(SpanningTreeAlgorithm.SpanningTree<WeightedEdge> edges, int numServices){
        Set<WeightedEdge> edgeSet = new HashSet<>();
        Iterator<WeightedEdge> iterator = edges.iterator();
        while (iterator.hasNext()){
            edgeSet.add(iterator.next());
        }

        List<WeightedEdge> edgeList = edgeSet.stream().collect(Collectors.toList());
        List<WeightedEdge> oldList = null;

        Collections.sort(edgeList, weightedEdgeComparator);

        Collections.reverse(edgeList);

        int numConnectedComponents = 1;
        int lastNumConnectedComponents = 1;
        int wantNumComponent = numServices;

        //不该删的边
        List<WeightedEdge> edgeTempList = new ArrayList<>();
        do{
            oldList = new ArrayList<>(edgeList);

            WeightedEdge edgeTemp = edgeList.get(0);
            edgeList.remove(0);

            numConnectedComponents = ConnectedComponents.numberOfComponents(edgeList);

            if(lastNumConnectedComponents==numConnectedComponents){
                edgeTempList.add(edgeTemp);
            }
            if(lastNumConnectedComponents > numConnectedComponents){
                edgeTempList.add(edgeTemp);
//                return oldList;
            }else {
                lastNumConnectedComponents = numConnectedComponents;
            }


        }while ((numConnectedComponents < wantNumComponent)&&(!edgeList.isEmpty()));
        edgeList.addAll(edgeTempList);
        return edgeList;
    }

}
