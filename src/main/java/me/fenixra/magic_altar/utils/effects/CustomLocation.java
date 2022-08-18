package me.fenixra.magic_altar.utils.effects;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class CustomLocation {
    private Location origin;
    private Location targetLocation;
    private Entity targetEntity;
    private double offsetX=0.0;
    private double offsetY=0.0;
    private double offsetZ=0.0;
    private float pitchOffset=0;
    private float yawOffset=0;
    public CustomLocation( Location location){
        this.targetLocation=location;
        origin=location.clone();
    }
    public CustomLocation( Entity targetEntity){
        this.targetEntity=targetEntity;
        targetLocation=targetEntity.getLocation();
        origin=targetLocation.clone();
    }

    public Location getLocation(){
        if(targetEntity==null||!targetEntity.isValid()){
            targetLocation=origin.clone();
            targetLocation.add(offsetX,offsetY,offsetZ);
            targetLocation.setYaw(targetLocation.getYaw()+yawOffset);
            targetLocation.setPitch(targetLocation.getPitch()+pitchOffset);
        }else {
            targetLocation = targetEntity.getLocation();
            targetLocation.setYaw(targetLocation.getYaw()+yawOffset);
            targetLocation.setPitch(targetLocation.getPitch()+pitchOffset);
            targetLocation.add(offsetX,offsetY,offsetZ);
        }
        return targetLocation;
    }

    public Location getOrigin(){
        return origin;
    }
    public Entity getTargetEntity(){
        if(!targetEntity.isValid()) return null;
        return targetEntity;
    }
    public CustomLocation setOffsetX(double value){
        offsetX=value;
        return this;
    }
    public CustomLocation setOffsetY(double value){
        offsetY=value;
        return this;
    }
    public CustomLocation setOffsetZ(double value){
        offsetZ=value;
        return this;
    }
    public CustomLocation setOffsetYaw(float value){
        yawOffset=value;
        return this;
    }
    public CustomLocation setOffsetPitch(float value){
        pitchOffset=value;
        return this;
    }

    @Override
    public CustomLocation clone(){
        try {
            CustomLocation loc=(CustomLocation) (super.clone());

            loc.origin=origin.clone();
            if(loc.targetLocation!=null) loc.targetLocation = targetLocation.clone();
            return loc;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

}
