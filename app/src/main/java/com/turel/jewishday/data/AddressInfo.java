package com.turel.jewishday.data;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Haim.Turkel on 10/30/2015.
 */
public class AddressInfo {
    public static String LOCATIONS_KEY = "LOCATIONS";
    public static String LOCATIONS_INDEX_KEY = "LOCATIONS_INDEX";

    private String title;
    private String address;

    private TimeZone zone;
    private double latitude = 0;
    private double longitude = 0;
    private double altitude = 0;

    public AddressInfo(String title, String address, double latitude, double longitude, double altitude, TimeZone zone) {
        this.title = title;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.zone = zone;
    }

    public AddressInfo(String title, String address, double latitude, double longitude, TimeZone zone) {
        this(title,address,latitude,longitude,0,zone);
    }

    public AddressInfo(JSONObject jsonAddress) throws JSONException {
        this.title = jsonAddress.getString("title");
        this.address = jsonAddress.getString("address");
        this.latitude = jsonAddress.getDouble("latitude");
        this.longitude = jsonAddress.getDouble("longitude");
        this.zone = TimeZone.getTimeZone(jsonAddress.getString("timezone"));
    }

    public TimeZone getZone() {
        return zone;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }


    public String getTitle() {
        return title;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("title", title);
            obj.put("address", address);
            obj.put("latitude", latitude);
            obj.put("longitude", longitude);
            obj.put("timezone",zone.getID());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }


    public static String toJson(List<AddressInfo> list) throws JSONException {
        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            jsonArray.put(list.get(i).toJSON());
        }
        jsonObj.put("root",jsonArray);
        return jsonObj.toString();
    }

    public static List<AddressInfo> fromJson(String json) throws JSONException {
        List<AddressInfo> list = new ArrayList<>();
        JSONObject jsonObj = new JSONObject(json);
        JSONArray jsonArray = jsonObj.getJSONArray("root");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonAddress = (JSONObject)jsonArray.get(i);
            AddressInfo address = new AddressInfo(jsonAddress);
            list.add(address);
        }
        return list;
    }
}
