package gameClient.util;

import api.edge_data;
import gameClient.CL_Pokemon;
import org.json.JSONObject;

public class MyPokemon {
    private edge_data _edge;
    private double _value;
    private int _type;
    private Point3D _pos;
    private int chase;
    private double min_dist;
    private int min_ro;

    public void set_value(double _value) {
        this._value = _value;
    }

    public void set_type(int _type) {
        this._type = _type;
    }

    public void set_pos(Point3D _pos) {
        this._pos = _pos;
    }

    public void setChase(int chase) {
        this.chase = chase;
    }

    public double get_value() {
        return _value;
    }

    public int get_type() {
        return _type;
    }

    public Point3D get_pos() {
        return _pos;
    }

    public int getChase() {
        return chase;
    }

    public MyPokemon(Point3D p, int t, double v, double s, edge_data e) {
        chase=-1;
        _type = t;
        _value = v;
        set_edge(e);
        _pos = p;
        min_dist = -1;
        min_ro = -1;
    }
    public static CL_Pokemon init_from_json(String json) {
        CL_Pokemon ans = null;
        try {
            JSONObject p = new JSONObject(json);
            int id = p.getInt("id");

        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return ans;
    }
    public String toString() {return "{value="+_value+", type="+_type+", pos="+_pos+", chase="+chase+"}";}
    public edge_data get_edge() {
        return _edge;
    }

    public void set_edge(edge_data _edge) {
        this._edge = _edge;
    }

    public Point3D getLocation() {
        return _pos;
    }
    public int getType() {return _type;}
    public double getValue() {return _value;}

    public double getMin_dist() {
        return min_dist;
    }

    public void setMin_dist(double mid_dist) {
        this.min_dist = mid_dist;
    }

    public int getMin_ro() {
        return min_ro;
    }

    public void setMin_ro(int min_ro) {
        this.min_ro = min_ro;
    }
}
