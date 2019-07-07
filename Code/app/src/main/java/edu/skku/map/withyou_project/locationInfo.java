package edu.skku.map.withyou_project;

import java.util.HashMap;
import java.util.Map;

public class locationInfo {
    public String ReOrVol;
    public Double latitude;
    public Double longitude;
    public String name;

    public locationInfo(){}
    public locationInfo(String ReOrVol, Double latitude, Double longitude,String name)
    {
        this.ReOrVol = ReOrVol;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("ReOrVol",ReOrVol);
        result.put("latitude",latitude);
        result.put("longitude",longitude);
        result.put("name",name);
        return result;
    }

}
