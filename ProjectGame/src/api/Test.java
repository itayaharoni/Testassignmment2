package api;

public class Test {
    public static void main(String[] args) {
        DWGraph_DS g=new DWGraph_DS();
        for (int i = 0; i < 10; i++) {
            g.addNode(new NodeData(i));
        }
        for (int i = 0; i < 9; i++) {
            g.connect(i,i+1,i+Math.random()+1);
        }
        g.connect(9,0,1.2);
        DWGraph_Algo gAlgo=new DWGraph_Algo();
        gAlgo.init(g);
        System.out.println(gAlgo.isConnected());
        System.out.println(gAlgo.shortestPathDist(0,3));
        DWGraph_Algo copyAlgo=new DWGraph_Algo();
        copyAlgo.init(gAlgo.copy());
        System.out.println(copyAlgo.shortestPath(0,3));
        copyAlgo.save("JsonTest");
        gAlgo.load("data\\A0");
        System.out.println(gAlgo.shortestPath(0,3));
    }
}
