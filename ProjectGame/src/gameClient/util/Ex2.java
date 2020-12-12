package gameClient.util;

import Server.Game_Server_Ex2;
import api.*;
import gameClient.Arena;

import java.util.List;

import static java.lang.Thread.onSpinWait;
import static java.lang.Thread.sleep;

public class Ex2 implements Runnable {
    private static MyArena ar;
    public static void main(String[] args) {
        Thread client = new Thread(new Ex2());
        client.start();

    }

    @Override
    public void run() {
        int scenario_num = 0;
        game_service game = Game_Server_Ex2.getServer(scenario_num);
        game.login(030303030);
        ar=new MyArena();
        DWGraph_Algo graph_algo=new DWGraph_Algo();
        graph_algo.load2(game.getGraph());
        ar.setGraph(graph_algo.getGraph());
        ar.setPokemons(MyArena.json2Pokemons(game.getPokemons()));
        for (MyPokemon p: ar.getPokemons()) {
            MyArena.updateEdge(p,graph_algo.getGraph());
            game.addAgent(p.get_edge().getSrc());
        }
        ar.setAgents(MyArena.getAgents(game.getAgents(),graph_algo.getGraph()));
        for (MyAgent a: ar.getAgents()) {
            for (MyPokemon p : ar.getPokemons()) {
                if(p.get_edge().getSrc()==a.getSrc() && p.getChase()==-1){
                    gottaCatchHim(p,a,graph_algo);
                    a.setDest(a.getSrc());
                    break;
                }
            }
        }
        game.startGame();
        long dt2=60000;
        while (game.isRunning()){
            System.out.println("Path is: "+ar.getAgents().get(0).getPath());
            ar.updatePokemon(game.getPokemons());
            ar.updateAgents(game.getAgents());
            MyAgent Ash=readyToCatch(ar.getAgents());
            if(readyToCatch(ar.getAgents())!=null){
                try {
                    Ash.set_sg_dt(timeFromPokemon(graph_algo.getGraph(),Ash.get_curr_fruit().get_pos(),Ash.get_curr_fruit().get_edge(),Ash.getSpeed()));
                    ar.updateTime(Ash);
                    dt2=game.timeToEnd()-Ash.getTimeForNext();
                    game.chooseNextEdge(Ash.getID(),Ash.get_curr_fruit().get_edge().getDest());
                    Ash.setDest(Ash.get_curr_fruit().get_edge().getDest());
                    double  val=Ash.getValue();
                    if(Ash.get_sg_dt()>0) {
                        long tt=(game.timeToEnd()-Ash.get_sg_dt());
                        if(Ash.get_sg_dt()-20>0) sleep((long)(Ash.get_sg_dt()-20));
                        while(tt<game.timeToEnd()+10){
                            game.move();
                            sleep(5);
                            ar.updateAgents(game.getAgents());
                            if(Ash.getValue()>val) {
                                System.out.println("CAUGHT THE BITCH");
                                ar.getPokemons().remove(Ash.get_curr_fruit());
                                break;
                            }
                        }
                        System.out.println(game.getPokemons());
                        System.out.println("Ash poke: "+Ash.get_curr_fruit()+" on edge"+Ash.get_curr_fruit().get_edge());
                        System.out.println("===============out of loop==========");
                        if(Ash.getValue()<=val) Ash.get_curr_fruit().setChase(-1);
                    }
                    if(Ash.get_sg_dt()==0) game.move();
                    long dt3=game.timeToEnd()-dt2;
                    if(dt3>0) {
                        sleep(dt3);
                    }
                    game.move();
                    ar.updatePokemon(game.getPokemons());
                    System.out.println("POKEMON UPDATE"+ar.getPokemons().get(0).get_edge());
                    Ash.setSrc(Ash.get_curr_edge().getDest());
                    System.out.println(Ash.getDest());
                    chooseNextPoke(Ash);
          //          if(Ash.get_curr_edge().getSrc()==Ash.getSrc()&&game.isRunning()) continue;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ar.updateAgents(game.getAgents());
            System.out.println(game.getAgents());
            MyAgent Misty=null;
            do {
                System.out.println("AGENT IS STUCK");
                Misty = isAgentStuck();
                if (Misty != null) {
                    chooseNextNode(Misty,game);
                }
            }while(Misty!=null);
            if(readyToCatch(ar.getAgents())!=null && game.isRunning()) continue;
                System.out.println(game.getAgents());
            ar.updateTime(game.timeToEnd());
            long dt=game.timeToEnd()-ar.getMinTime();
            try {
                if(dt>0) sleep(dt);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            game.move();
            System.out.println(game.timeToEnd());
            System.out.println(game.getAgents());
        }
        System.out.println(game);
    }
    public static MyAgent isAgentStuck(){
        for (MyAgent a: ar.getAgents()) {
            if(a.getDest()==-1 && a.getSrc()!=a.get_curr_edge().getSrc()) return a;
        }
        return null;
    }
    public static void chooseNextNode(MyAgent a,game_service game){
        List<node_data> path= a.getPath();
        for (int i = 0; i < path.size()-1; i++) {
            if(path.get(i).getKey()==a.getSrc()){
                a.setDest(path.get(i+1).getKey());
                game.chooseNextEdge(a.getID(),a.getDest());
                game.move();
                break;
        }

        }
    }
    public static void chooseNextPoke(MyAgent agent){
        DWGraph_Algo ga=new DWGraph_Algo();
        MyPokemon chosenOne=null;
        double minDist=Double.MAX_VALUE;
        double currDist=0;
        ga.init(ar.getGraph());
        for (MyPokemon p: ar.getPokemons()) {
            if(p.getChase()==-1) {
                currDist = ga.shortestPathDist(agent.getSrc(), p.get_edge().getSrc());
                if (currDist != -1) {
                    if (currDist < minDist) {
                        minDist = currDist;
                        chosenOne = p;
                    }
                }
            }
        }
        System.out.println("THE CHOSE IS :" +chosenOne.get_edge()+chosenOne);
        gottaCatchHim(chosenOne,agent,ga);
    }
    public static void gottaCatchHim(MyPokemon p, MyAgent a, dw_graph_algorithms ga){
        a.setDest(-1);
        a.setCurrNode(a.getSrc());
        a.set_curr_fruit(p);
        a.set_gg(ga.getGraph());
        a.set_curr_edge(p.get_edge());
        a.set_sg_dt(timeFromPokemon(ga.getGraph(),p.get_pos(),p.get_edge(),a.getSpeed()));
        p.setChase(0);
        a.setPath(ga.shortestPath(a.getSrc(),p.get_edge().getSrc()));
    }
    public static MyAgent readyToCatch(List<MyAgent> list){
        for (MyAgent a: list) {
            if(a.getSrc()==a.get_curr_edge().getSrc()) return a;
        }
        return null;
    }
    public static long timeFromPokemon(directed_weighted_graph g, geo_location p_pos, edge_data p_edge, double speed){
        long dt=0;
        double distNodes=g.getNode(p_edge.getSrc()).getLocation().distance(g.getNode(p_edge.getDest()).getLocation());
        double dist=g.getNode(p_edge.getSrc()).getLocation().distance(p_pos);
        double ratio=dist/distNodes;
        double ratioOnEdge=ratio*p_edge.getWeight();
        dt=(long)((ratioOnEdge/speed)*1000);
        return dt;
    }
}
