package com.server.webduino.core.webduinosystem.scenario.actions;

import com.server.webduino.core.SampleAsyncCallBack;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by giaco on 02/04/2018.
 */
public class ActionCommand {
    public String command;
    public String name;

    boolean hastarget = false;
    double mintargetvalue = 0.0;
    double maxtargetvalue = 30.0;
    String targetname = "Target";

    boolean haszone = false;
    String zonename = "Target";

    boolean hasparam = false;
    String paramname = "paramname";
    double paramlen = 100;

    boolean hasstatus = false;
    String statusname = "stato";

    public ActionCommand(String command, String name) {
        this.command = command;
        this.name = name;
    }

    public void addTarget(String name, int min, int max) {
        hastarget = true;
        targetname = name;
        mintargetvalue = min;
        maxtargetvalue = max;
    }
    public void addZone(String name) {
        haszone = true;
        zonename = name;
    }
    public void addParam(String name, int len) {
        hasparam = true;
        paramname = name;
        paramlen = len;
    }

    public void addStatus(String name) {
        hasstatus = true;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("command", command);
        json.put("name", name);

        if (hastarget) {
            json.put("targetvalue",true);
            json.put("mintargetvalue",mintargetvalue);
            json.put("maxtargetvalue",maxtargetvalue);
            json.put("targetname",targetname);
        }

        if (haszone) {
            json.put("zone",true);
            json.put("zonename",zonename);
        }

        if (hasparam) {
            json.put("param",true);
            json.put("paramname",paramname);
            json.put("paramlen",paramlen);
        }

        if (hasstatus) {
            json.put("status",true);
            json.put("statusname",statusname);
        }


        return json;
    }

}
