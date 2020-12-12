package gameClient.util;

import api.directed_weighted_graph;
import api.edge_data;
import api.geo_location;
import api.node_data;
import org.json.JSONObject;

import java.util.List;

public class MyAgent {
    public static final double EPS = 0.0001;
    private static int _count = 0;
    private static int _seed = 3331;
    private int _id;
    private geo_location _pos;
    private double _speed;
    private edge_data _curr_edge;
    private node_data _curr_node;
    private directed_weighted_graph _gg;
    private MyPokemon _curr_fruit;
    private long _sg_dt;
    private double _value;
    private int src;
    private int dest;
    private List<node_data> path;
    private long timeForNext;
    public List<node_data> getPath() {
        return path;
    }

    public long getTimeForNext() {
        return timeForNext;
    }

    public void setTimeForNext(long timeForNext) {
        this.timeForNext = timeForNext;
    }

    public void setPath(List<node_data> path) {
        this.path = path;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void set_pos(geo_location _pos) {
        this._pos = _pos;
    }

    public void set_speed(double _speed) {
        this._speed = _speed;
    }

    public void set_curr_edge(edge_data _curr_edge) {
        this._curr_edge = _curr_edge;
    }


    public void set_gg(directed_weighted_graph _gg) {
        this._gg = _gg;
    }

    public void set_value(double _value) {
        this._value = _value;
    }

    public void setSrc(int src) {
        this.src = src;
    }

    public void setDest(int dest) {
        this.dest = dest;
    }

    public int get_id() {
        return _id;
    }

    public geo_location get_pos() {
        return _pos;
    }

    public double get_speed() {
        return _speed;
    }

    public node_data get_curr_node() {
        return _curr_node;
    }

    public directed_weighted_graph get_gg() {
        return _gg;
    }

    public int getSrc() {
        return src;
    }

    public int getDest() {
        return dest;
    }

    public MyAgent(directed_weighted_graph g, int start_node) {
        src=-1;
        dest=-1;
        _gg = g;
        setMoney(0);
        this._curr_node = _gg.getNode(start_node);
        _pos = _curr_node.getLocation();
        _id = -1;
        setSpeed(0);
    }
    public void update(String json) {
        JSONObject line;
        try {
            line = new JSONObject(json);
            JSONObject ttt = line.getJSONObject("Agent");
            int id = ttt.getInt("id");
            if(id==this.getID() || this.getID() == -1) {
                if(this.getID() == -1) {_id = id;}
                double speed = ttt.getDouble("speed");
                String p = ttt.getString("pos");
                Point3D pp = new Point3D(p);
                int src = ttt.getInt("src");
                int dest = ttt.getInt("dest");
                double value = ttt.getDouble("value");
                this._pos = pp;
                this.setCurrNode(src);
                this.setSpeed(speed);
                this.setNextNode(dest);
                this.setMoney(value);
                this.setSrc(src);
                this.setDest(dest);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    //@Override
    public int getSrcNode() {return this._curr_node.getKey();}
    public String toJSON() {
        String ans = "{\"Agent\":{"
                + "\"id\":"+this._id+","
                + "\"value\":"+this._value+","
                + "\"src\":"+this._curr_node.getKey()+","
                + "\"dest\":"+dest+","
                + "\"speed\":"+this.getSpeed()+","
                + "\"pos\":\""+_pos.toString()+"\""
                + "}"
                + "}";
        return ans;
    }
    private void setMoney(double v) {_value = v;}

    public boolean setNextNode(int dest) {
        boolean ans = false;
        int src = this._curr_node.getKey();
        this._curr_edge = _gg.getEdge(src, dest);
        if(_curr_edge!=null) {
            ans=true;
        }
        else {_curr_edge = null;}
        return ans;
    }
    public void setCurrNode(int src) {
        this._curr_node = _gg.getNode(src);
    }
    public boolean isMoving() {
        return this._curr_edge!=null;
    }
    public String toString() {
        return toJSON();
    }
    public String toString1() {
        String ans=""+this.getID()+","+_pos+", "+isMoving()+","+this.getValue();
        return ans;
    }
    public int getID() {
        // TODO Auto-generated method stub
        return this._id;
    }

    public geo_location getLocation() {
        // TODO Auto-generated method stub
        return _pos;
    }


    public double getValue() {
        // TODO Auto-generated method stub
        return this._value;
    }



    public int getNextNode() {
        int ans = -2;
        if(this._curr_edge==null) {
            ans = -1;}
        else {
            ans = this._curr_edge.getDest();
        }
        return ans;
    }

    public double getSpeed() {
        return this._speed;
    }

    public void setSpeed(double v) {
        this._speed = v;
    }
    public MyPokemon get_curr_fruit() {
        return _curr_fruit;
    }
    public void set_curr_fruit(MyPokemon curr_fruit) {
        this._curr_fruit = curr_fruit;
    }
    public void set_SDT(long ddtt) {
        long ddt = ddtt;
        if(this._curr_edge!=null) {
            double w = get_curr_edge().getWeight();
            geo_location dest = _gg.getNode(get_curr_edge().getDest()).getLocation();
            geo_location src = _gg.getNode(get_curr_edge().getSrc()).getLocation();
            double de = src.distance(dest);
            double dist = _pos.distance(dest);
            if(this.get_curr_fruit().get_edge()==this.get_curr_edge()) {
                dist = _curr_fruit.getLocation().distance(this._pos);
            }
            double norm = dist/de;
            double dt = w*norm / this.getSpeed();
            ddt = (long)(1000.0*dt);
        }
        this.set_sg_dt(ddt);
    }

    public edge_data get_curr_edge() {
        return this._curr_edge;
    }
    public long get_sg_dt() {
        return _sg_dt;
    }
    public void set_sg_dt(long _sg_dt) {
        this._sg_dt = _sg_dt;
    }
}
