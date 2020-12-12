package api;

import java.util.Collection;
import java.util.HashMap;

public class DWGraph_DS implements directed_weighted_graph {
    private HashMap<Integer,node_data> graph;
    private HashMap<Integer,HashMap<Integer,edge_data>> Edges;
    private int mc;
    private int EdgeSize;
    public DWGraph_DS(){
        graph=new HashMap<Integer, node_data>();
        Edges=new HashMap<Integer, HashMap<Integer, edge_data>>();
        EdgeSize=0;
        mc=0;
    }
    @Override
    public node_data getNode(int key) {
        return graph.get(key);
    }

    @Override
    public edge_data getEdge(int src, int dest) {
        if(graph.get(src)==null || graph.get(dest)==null) return null;
        return Edges.get(src).get(dest);
    }

    @Override
    public void addNode(node_data n) {
        if(graph.get(n.getKey())==null) {
            node_data Vertex = new NodeData(n);
            graph.put(Vertex.getKey(), Vertex);
            HashMap<Integer, edge_data> edgeV = new HashMap<Integer, edge_data>();
            Edges.put(Vertex.getKey(), edgeV);
            mc++;
        }
    }

    @Override
    public void connect(int src, int dest, double w) {
        if(src==dest || w<=0) return;
        if(graph.get(src)==null || graph.get(dest)==null) return;
            Edges.get(src).put(dest, new EdgeData(src, dest, w));
            EdgeSize++;
            mc++;
    }

    @Override
    public Collection<node_data> getV() {
        return graph.values();
    }

    @Override
    public Collection<edge_data> getE(int node_id) {
        return Edges.get(node_id).values();
    }

    @Override
    public node_data removeNode(int key) {
        if(graph.get(key)==null) return null;
        if(Edges.get(key).size()==0){
            Edges.remove(key);
            return graph.remove(key);
        }
        for (node_data v: getV()) {
            for (edge_data e: getE(v.getKey())) {
                if(e.getDest()==key){
                    Edges.get(v.getKey()).remove(key);
                    EdgeSize--;
                }
            }
        }
        EdgeSize-=Edges.get(key).size();
        Edges.remove(key);
        mc++;
        return graph.remove(key);
    }

    @Override
    public edge_data removeEdge(int src, int dest) {
        if(graph.get(src)==null || graph.get(dest)==null) return null;
        if(Edges.get(src).get(dest)==null) return null;
        return Edges.get(src).remove(dest);
    }

    @Override
    public int nodeSize() {
        return graph.size();
    }

    @Override
    public int edgeSize() {
        return EdgeSize;
    }

    @Override
    public int getMC() {
        return mc;
    }

    @Override
    public String toString() {
        return "DWGraph_DS{" +
                "graph=" + graph +
                ", Edges=" + Edges +
                ", mc=" + mc +
                ", EdgeSize=" + EdgeSize +
                '}';
    }
}
