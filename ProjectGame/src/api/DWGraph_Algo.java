package api;

import com.google.gson.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.*;

public class DWGraph_Algo implements dw_graph_algorithms{
    private directed_weighted_graph graph;
    private HashMap<Integer,nodeData_Algo> vertices;
    @Override
    public void init(directed_weighted_graph g) {
        graph=g;
    }

    @Override
    public directed_weighted_graph getGraph() {
        return graph;
    }

    @Override
    public directed_weighted_graph copy() {
        directed_weighted_graph copyGraph=new DWGraph_DS();
        for (node_data v: graph.getV()) {
            copyGraph.addNode(new NodeData(v));
        }
        for (node_data copyV: copyGraph.getV()) {
            for (edge_data e: graph.getE(copyV.getKey())) {
                copyGraph.connect(e.getSrc(),e.getDest(),e.getWeight());
            }
        }
        return copyGraph;
    }
    public directed_weighted_graph reverseGraph(){
        directed_weighted_graph reverse=new DWGraph_DS();
        for (node_data v: graph.getV()) {
            reverse.addNode(new NodeData(v));
        }
        for (node_data reverseV: reverse.getV()) {
            for (edge_data e: graph.getE(reverseV.getKey())) {
                reverse.connect(e.getDest(),e.getSrc(),e.getWeight());
            }
        }
        return reverse;
    }

    @Override
    public boolean isConnected() {
        if(graph==null) return true;
        if(graph.nodeSize()==0 || graph.nodeSize()==1) return true;
        for (node_data v: graph.getV()) {
            v.setTag(-1);
        }
        Iterator<node_data> itr=graph.getV().iterator();
        node_data randomVertex=itr.next();
        ArrayDeque<node_data> queue = new ArrayDeque<node_data>();
        randomVertex.setTag(0);
        queue.add(randomVertex);
        while(!queue.isEmpty()){
            Iterator<edge_data> eItr=graph.getE(queue.getFirst().getKey()).iterator();
            while(eItr.hasNext()){
                edge_data e=eItr.next();
                if(graph.getNode(e.getDest()).getTag()==-1){
                    graph.getNode(e.getDest()).setTag(0);
                    queue.addLast(graph.getNode(e.getDest()));
                }
            }
            if (!queue.isEmpty()) queue.pollFirst();
        }
        for (node_data v: graph.getV()) {
            if (v.getTag()==-1) return false;
        }
        directed_weighted_graph reverseGraph=reverseGraph();
        for (node_data reverseV: reverseGraph.getV()) {
            reverseV.setTag(-1);
        }
        randomVertex.setTag(0);
        queue.add(randomVertex);
        while (!queue.isEmpty()){
            Iterator<edge_data> itrE=reverseGraph.getE(queue.getFirst().getKey()).iterator();
            while (itrE.hasNext()){
                edge_data reverseE=itrE.next();
                if(reverseGraph.getNode(reverseE.getDest()).getTag()==-1){
                    reverseGraph.getNode(reverseE.getDest()).setTag(0);
                    queue.addLast(reverseGraph.getNode(reverseE.getDest()));
                }
            }
            if (!queue.isEmpty()) queue.pollFirst();
        }
        for (node_data reverseV: reverseGraph.getV()) {
            if (reverseV.getTag()==-1) return false;
        }
        return true;
    }

    @Override
    public double shortestPathDist(int src, int dest) {
        if(graph.getNode(src)==null || graph.getNode(dest)==null) return -1;
        if(src==dest) return 0;
        vertices=new HashMap<Integer, nodeData_Algo>();
        for(node_data v: graph.getV()){
            vertices.put(v.getKey(),new nodeData_Algo(v));
        }
        vertices.get(src).setTag(0);
        PriorityQueue<nodeData_Algo> queue=new PriorityQueue<nodeData_Algo>(new nodeDataAlgoComparator());
        queue.add(vertices.get(src));
        while (!queue.isEmpty()){
            Iterator<edge_data> itrE = graph.getE(queue.peek().getN().getKey()).iterator();
            while (itrE.hasNext()){
                edge_data e=itrE.next();
                if (vertices.get(e.getDest()).getTag()>e.getWeight()+vertices.get(e.getSrc()).getTag()){
                    vertices.get(e.getDest()).setTag(e.getWeight()+vertices.get(e.getSrc()).getTag());
                    queue.add(vertices.get(e.getDest()));
                }
            }
            if (!queue.isEmpty()) queue.poll();
        }
        if (vertices.get(dest).getTag()==Double.MAX_VALUE) return -1;
        return vertices.get(dest).getTag();
    }

    @Override
    public List<node_data> shortestPath(int src, int dest) {
        if (graph.getNode(src)==null || graph.getNode(dest)==null) return null;
        LinkedList<node_data> path=new LinkedList<node_data>();
        if (src==dest){
            path.add(graph.getNode(src));
            return path;
        }
        if(shortestPathDist(src,dest)==-1) return null;
        path.add(graph.getNode(dest));
        directed_weighted_graph reverseGraph=reverseGraph();
        while(path.getFirst().getKey()!=src){
            Iterator<edge_data> itrE=reverseGraph.getE(path.getFirst().getKey()).iterator();
            while (itrE.hasNext()){
                edge_data reverseE=itrE.next();
                if(vertices.get(reverseE.getSrc()).getTag()==reverseE.getWeight()+vertices.get(reverseE.getDest()).getTag()){
                    path.addFirst(graph.getNode(reverseE.getDest()));
                }
            }
        }
        return path;
    }

    @Override
    public boolean save(String file) {
        Gson gson=new Gson();
        String json=gson.toJson(graph);
        try {
            PrintWriter pw=new PrintWriter(new File(file));
            pw.write(json);
            pw.close();
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    private class DWGraphDeserializer implements JsonDeserializer<directed_weighted_graph>{

        @Override
        public directed_weighted_graph deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            directed_weighted_graph g=new DWGraph_DS();
            JsonObject jsonObject= jsonElement.getAsJsonObject();
            JsonArray nodes=jsonObject.get("Nodes").getAsJsonArray();
            JsonArray edges=jsonObject.get("Edges").getAsJsonArray();
            for (int i = 0; i < nodes.size(); i++) {
                JsonObject vertex=nodes.get(i).getAsJsonObject();
                g.addNode(new NodeData(vertex.get("id").getAsInt()));
                String s[]=vertex.get("pos").getAsString().split(",");
                g.getNode(vertex.get("id").getAsInt()).setLocation(new GeoLocation(Double.parseDouble(s[0]),Double.parseDouble(s[1]),Double.parseDouble(s[2])));
            }
            for (int i = 0; i < edges.size(); i++) {
                JsonObject edge=edges.get(i).getAsJsonObject();
                g.connect(edge.get("src").getAsInt(),edge.get("dest").getAsInt(),edge.get("w").getAsDouble());
            }
            return g;
        }
    }
    @Override
    public boolean load(String file) {
        directed_weighted_graph copy=graph;
        GsonBuilder gsonBuilder=new GsonBuilder();
        DWGraphDeserializer dwgDS= new DWGraphDeserializer();
        gsonBuilder.registerTypeAdapter(directed_weighted_graph.class,dwgDS);
        Gson customGson=gsonBuilder.create();
        try {
            FileReader reader=new FileReader(file);
            graph=customGson.fromJson(reader,directed_weighted_graph.class);
            return true;
        } catch (FileNotFoundException e) {
            graph=copy;
            return false;
        }
    }
    public boolean load2(String file){
        directed_weighted_graph copy=graph;
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            DWGraphDeserializer dwgDS = new DWGraphDeserializer();
            gsonBuilder.registerTypeAdapter(directed_weighted_graph.class, dwgDS);
            Gson customGson = gsonBuilder.create();
            graph = customGson.fromJson(file, directed_weighted_graph.class);
            return true;
        }catch (Exception e){
            graph=copy;
            return false;
        }
    }
    private class nodeDataAlgoComparator implements Comparator<nodeData_Algo> {

        @Override
        public int compare(nodeData_Algo o1, nodeData_Algo o2) {
            if(o1.getTag()<o2.getTag()) return -1;
            if(o1.getTag()>o2.getTag()) return 1;
            return 0;
        }
    }
    private class nodeData_Algo {
        private node_data n;
        private double tag;
        public nodeData_Algo(node_data n){
            this.n=n;
            this.tag=Double.MAX_VALUE;
        }

        public node_data getN() {
            return n;
        }

        public double getTag() {
            return tag;
        }

        public void setN(node_data n) {
            this.n = n;
        }

        public void setTag(double tag) {
            this.tag = tag;
        }
    }
}
