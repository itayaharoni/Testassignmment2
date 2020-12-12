package api;

public class GeoLocation implements geo_location {
    private double x;
    private double y;
    private double z;
    public GeoLocation(){
        x=y=z=0;
    }
    public GeoLocation(geo_location g){
        x=g.x();
        y=g.y();
        z=g.z();
    }
    public GeoLocation(double x,double y, double z){
        this.x=x;
        this.y=y;
        this.z=z;
    }
    @Override
    public double x() {
        return x;
    }

    @Override
    public double y() {
        return y;
    }

    @Override
    public double z() {
        return z;
    }

    @Override
    public double distance(geo_location g) {
        return Math.sqrt(Math.pow(x-g.x(),2)+Math.pow(y-g.y(),2)+Math.pow(z-g.z(),2));
    }

    @Override
    public String toString() {
        return "{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
