package gameClient.util;

import api.directed_weighted_graph;
import api.edge_data;
import api.geo_location;
import api.node_data;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyArena {
    public static final double EPS1 = 0.001, EPS2=EPS1*EPS1, EPS=EPS2;
    private directed_weighted_graph _gg;
    private List<MyAgent> _agents;
    private List<MyPokemon> _pokemons;
    private List<String> _info;
    private static Point3D MIN = new Point3D(0, 100,0);
    private static Point3D MAX = new Point3D(0, 100,0);

    public MyArena() {;
        _info = new ArrayList<String>();
    }
    private MyArena(directed_weighted_graph g, List<MyAgent> r, List<MyPokemon> p) {
        _gg = g;
        this.setAgents(r);
        this.setPokemons(p);
    }
    public void setPokemons(List<MyPokemon> f) {
        this._pokemons = f;
    }
    public void setAgents(List<MyAgent> f) {
        this._agents = f;
    }
    public void setGraph(directed_weighted_graph g) {this._gg =g;}//init();}
    private void init( ) {
        MIN=null; MAX=null;
        double x0=0,x1=0,y0=0,y1=0;
        Iterator<node_data> iter = _gg.getV().iterator();
        while(iter.hasNext()) {
            geo_location c = iter.next().getLocation();
            if(MIN==null) {x0 = c.x(); y0=c.y(); x1=x0;y1=y0;MIN = new Point3D(x0,y0);}
            if(c.x() < x0) {x0=c.x();}
            if(c.y() < y0) {y0=c.y();}
            if(c.x() > x1) {x1=c.x();}
            if(c.y() > y1) {y1=c.y();}
        }
        double dx = x1-x0, dy = y1-y0;
        MIN = new Point3D(x0-dx/10,y0-dy/10);
        MAX = new Point3D(x1+dx/10,y1+dy/10);

    }
    public List<MyAgent> getAgents() {return _agents;}
    public List<MyPokemon> getPokemons() {return _pokemons;}
    public MyAgent findMyAgent(int id){
        for (MyAgent a: _agents) {
            if(a.getID()==id) return a;
        }
        return null;
    }
    public void updateTime(MyAgent a){
        a.setTimeForNext(((long)((a.get_curr_edge().getWeight()/a.getSpeed())*1000)));
    }
    public void updateTime(long time){
        for (MyAgent a: _agents) {
            if(a.getSrc()!=a.getDest() && a.getSrc()!=-1 && a.getDest()!=-1){
                a.setTimeForNext(time-(long)((_gg.getEdge(a.getSrc(),a.getDest()).getWeight()/a.getSpeed())*1000));
            }
        }
    }
    public  long getMinTime(){
        long min=Long.MAX_VALUE;
        for (MyAgent a:_agents) {
            if(a.getTimeForNext()<min) min=a.getTimeForNext();
        }
        return min;
    }
    public directed_weighted_graph getGraph() {
        return _gg;
    }
    public List<String> get_info() {
        return _info;
    }
    public void set_info(List<String> _info) {
        this._info = _info;
    }

    ////////////////////////////////////////////////////
    public static List<MyAgent> getAgents(String aa, directed_weighted_graph gg) {
        ArrayList<MyAgent> ans = new ArrayList<MyAgent>();
        try {
            JSONObject ttt = new JSONObject(aa);
            JSONArray ags = ttt.getJSONArray("Agents");
            for(int i=0;i<ags.length();i++) {
                MyAgent c = new MyAgent(gg,0);
                c.update(ags.get(i).toString());
                ans.add(c);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ans;
    }
    public void updatePokemon(String s){
        boolean flag=true;
        try {
            JSONObject ttt = new JSONObject(s);
            JSONArray ags = ttt.getJSONArray("Pokemons");
            for (int i = 0; i < ags.length(); i++) {
                JSONObject pp = ags.getJSONObject(i);
                JSONObject pk = pp.getJSONObject("Pokemon");
                int t = pk.getInt("type");
                double v = pk.getDouble("value");
                String p = pk.getString("pos");
                MyPokemon f = new MyPokemon(new Point3D(p), t, v, 0, null);
                    if (!_pokemons.contains(f)) {
                        _pokemons.add(f);
                        updateEdge(f, _gg);
                    }
                }
        }
        catch (JSONException e) {e.printStackTrace();}
    }
    public  void updateAgents(String s){
        try {
            JSONObject jsonObject=new JSONObject(s);
            JSONArray agents_arr= jsonObject.getJSONArray("Agents");
            for (int i = 0; i < agents_arr.length(); i++) {
             String agent_string=agents_arr.get(i).toString();
             JSONObject line = new JSONObject(agent_string);
             JSONObject agent = line.getJSONObject("Agent");
             int id = agent.getInt("id");
                for (MyAgent a: _agents) {
                    if(a.getID()==id){
                        a.set_value(agent.getDouble("value"));
                        a.setSrc(agent.getInt("src"));
                        a.setSpeed(agent.getDouble("speed"));
                        a.set_pos(new Point3D(agent.getString("pos")));
                        a.setDest(agent.getInt("dest"));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static ArrayList<MyPokemon> json2Pokemons(String fs) {
        ArrayList<MyPokemon> ans = new  ArrayList<MyPokemon>();
        try {
            JSONObject ttt = new JSONObject(fs);
            JSONArray ags = ttt.getJSONArray("Pokemons");
            for(int i=0;i<ags.length();i++) {
                JSONObject pp = ags.getJSONObject(i);
                JSONObject pk = pp.getJSONObject("Pokemon");
                int t = pk.getInt("type");
                double v = pk.getDouble("value");
                String p = pk.getString("pos");
                MyPokemon f = new MyPokemon(new Point3D(p), t, v, 0, null);
                ans.add(f);
            }
        }
        catch (JSONException e) {e.printStackTrace();}
        return ans;
    }
    public static void updateEdge(MyPokemon fr, directed_weighted_graph g) {
        //	oop_edge_data ans = null;
        Iterator<node_data> itr = g.getV().iterator();
        while(itr.hasNext()) {
            node_data v = itr.next();
            Iterator<edge_data> iter = g.getE(v.getKey()).iterator();
            while(iter.hasNext()) {
                edge_data e = iter.next();
                boolean f = isOnEdge(fr.getLocation(), e,fr.getType(), g);
                if(f) {fr.set_edge(e);}
            }
        }
    }

    private static boolean isOnEdge(geo_location p, geo_location src, geo_location dest ) {

        boolean ans = false;
        double dist = src.distance(dest);
        double d1 = src.distance(p) + p.distance(dest);
        if(dist>d1-EPS2) {ans = true;}
        return ans;
    }
    private static boolean isOnEdge(geo_location p, int s, int d, directed_weighted_graph g) {
        geo_location src = g.getNode(s).getLocation();
        geo_location dest = g.getNode(d).getLocation();
        return isOnEdge(p,src,dest);
    }
    private static boolean isOnEdge(geo_location p, edge_data e, int type, directed_weighted_graph g) {
        int src = g.getNode(e.getSrc()).getKey();
        int dest = g.getNode(e.getDest()).getKey();
        if(type<0 && dest>src) {return false;}
        if(type>0 && src>dest) {return false;}
        return isOnEdge(p,src, dest, g);
    }

    private static Range2D GraphRange(directed_weighted_graph g) {
        Iterator<node_data> itr = g.getV().iterator();
        double x0=0,x1=0,y0=0,y1=0;
        boolean first = true;
        while(itr.hasNext()) {
            geo_location p = itr.next().getLocation();
            if(first) {
                x0=p.x(); x1=x0;
                y0=p.y(); y1=y0;
                first = false;
            }
            else {
                if(p.x()<x0) {x0=p.x();}
                if(p.x()>x1) {x1=p.x();}
                if(p.y()<y0) {y0=p.y();}
                if(p.y()>y1) {y1=p.y();}
            }
        }
        Range xr = new Range(x0,x1);
        Range yr = new Range(y0,y1);
        return new Range2D(xr,yr);
    }
    public static Range2Range w2f(directed_weighted_graph g, Range2D frame) {
        Range2D world = GraphRange(g);
        Range2Range ans = new Range2Range(world, frame);
        return ans;
    }

}
