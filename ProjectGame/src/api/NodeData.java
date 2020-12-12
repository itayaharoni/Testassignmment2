package api;

public class NodeData implements node_data {
    private int key;
    private geo_location pos;
    private double weight;
    private String info;
    private int tag;
    public NodeData(int key){
        this.key=key;
        pos=new GeoLocation();
        weight=0;
        info="";
        tag=-1;
    }
    public NodeData(node_data n){
        key=n.getKey();
        pos=new GeoLocation(n.getLocation());
        weight=n.getWeight();
        tag=n.getTag();
        info=n.getInfo();
    }
    @Override
    public int getKey() {
        return key;
    }

    @Override
    public geo_location getLocation() {
        return pos;
    }

    @Override
    public void setLocation(geo_location p) {
        pos=new GeoLocation(p);
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public void setWeight(double w) {
        weight=w;
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public void setInfo(String s) {
        info=s;
    }

    @Override
    public int getTag() {
        return tag;
    }

    @Override
    public void setTag(int t) {
        tag=t;
    }

    @Override
    public String toString() {
        return "{" +
                "key=" + key +
                ", pos=" + pos +
                ", weight=" + weight +
                ", info='" + info + '\'' +
                ", tag=" + tag +
                '}';
    }
}
