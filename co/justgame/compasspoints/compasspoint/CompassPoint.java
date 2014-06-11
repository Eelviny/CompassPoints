package co.justgame.compasspoints.compasspoint;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class CompassPoint implements Comparable<CompassPoint> {

    private double X;
    private double Y;
    private double Z;
    private int position;

    private String world;
    private String name;

    public CompassPoint(){

        X = 0;
        Y = 0;
        Z = 0;
        world = "world";
        name = "DEFAULT";
    }

    public CompassPoint(int X, int Y, int Z, World world, String name){

        this.X = X+.5;
        this.Y = Y+.5;
        this.Z = Z+.5;
        this.world = world.getName();
        this.name = name;
    }

    @Override
    public int hashCode(){
        return Integer.getInteger(this.getX().toString() + this.getY().toString() + this.getZ().toString());
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof CompassPoint){
            CompassPoint compassPoint = (CompassPoint) o;

            if(compassPoint.getWorld().equals(this.getWorld()) && compassPoint.getName().equals(this.getName())
                    && compassPoint.getX().equals(this.getX()) && compassPoint.getY().equals(this.getY())
                    && compassPoint.getZ().equals(this.getZ()))
                return true;
            else
                return false;
        }else{
            return false;
        }
    }

    public Double getX(){
        return this.X;
    }

    public Double getY(){
        return this.Y;
    }

    public Double getZ(){
        return this.Z;
    }

    public World getWorld(){
        return Bukkit.getWorld(this.world);
    }

    public String getWorldName(){
        return this.world;
    }

    public String getName(){
        return this.name;
    }

    public int getPosition(){
        return this.position;
    }

    public Location getLocation(){
        return new Location(this.getWorld(), this.getX(), this.getY(), this.getZ());
    }

    public Location getHeadLocation(){
        return new Location(this.getWorld(), this.getX(), this.getY() + 1.0, this.getZ());
    }

    public void setX(double X){
        this.X = X;
    }

    public void setY(double Y){
        this.Y = Y;
    }

    public void setZ(double Z){
        this.Z = Z;
    }

    public void setWorld(String w){
        this.world = w;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setPosition(int i){
        this.position = i;
    }

    @Override
    public int compareTo(CompassPoint o){

        if(o.equals(this)){
            return 0;
        }else if(o.getPosition() < this.getPosition()){
            return 1;
        }else if(o.getPosition() > this.getPosition()){
            return -1;
        }else{
            return 1;
        }
    }

}
