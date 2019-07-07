package edu.skku.map.withyou_project;

import java.util.HashMap;
import java.util.Map;

public class memberInfo {
    public String id;
    public String name;
    public String passwd;
    public String gmail;
    public String ReOrVol;

    public memberInfo() { }
    public memberInfo(String id, String name, String passwd,String gmail, String ReOrVol)
    {
        this.id = id;
        this.name = name;
        this.passwd = passwd;
        this.gmail = gmail;
        this.ReOrVol =ReOrVol ;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id",id);
        result.put("name",name);
        result.put("passwd",passwd);
        result.put("gmail",gmail);
        result.put("ReOrVol",ReOrVol);
        return result;
    }

}
