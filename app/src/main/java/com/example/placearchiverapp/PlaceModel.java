package com.example.placearchiverapp;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class PlaceModel {
    public String LocName;
    public Double Longitude;
    public Double Latitude;
    public String Description;
    public String ImageName;
    public PlaceModel(String LocName,String Description,String ImageName,Double Longitude,Double Latitude){
        this.LocName=LocName;
        this.Description=Description;
        this.ImageName=ImageName;
        this.Latitude=Latitude;
        this.Longitude=Longitude;
    }
   public PlaceModel(){}
}
