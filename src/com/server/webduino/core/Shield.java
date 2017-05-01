package com.server.webduino.core;

import com.server.webduino.core.sensors.Actuator;
import com.server.webduino.core.sensors.SensorBase;
import com.server.webduino.core.sensors.commands.ActuatorCommand;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//import static com.server.webduino.core.sensors.SensorBase.Status_Offline;

public class Shield extends httpClient {

    private static Logger LOGGER = Logger.getLogger(Shield.class.getName());

    protected int id;
    protected String MACAddress;
    protected String boardName;
    protected Date lastUpdate;
    protected List<SensorBase> sensors = new ArrayList<>();
    public URL url;
    public int port;

    private ShieldSettings settings = new ShieldSettings();

    public static final String updateStatus_updated = "updated";
    public static final String updateStatus_updating = "updating";
    public static final String updateStatus_notUpdated = "notupdated";

    private String settingsStatus = updateStatus_notUpdated;
    private String sensorStatus = updateStatus_notUpdated;

    public String getSettingStatus() {
        return settingsStatus;
    }

    public String getSensorStatus() {
        return sensorStatus;
    }

    protected String statusUpdatePath = "/sensorstatus";

    public Shield() {
    }

    public boolean postCommand(Command command) { //

        LOGGER.info("postCommand:");

        return Core.publish("fromServer/shield/" + id + "/postcommand", command.getJSON().toString());
    }

    public boolean requestSensorStatusUpdate() { //

        LOGGER.info("requestStatusUpdate:");

        sensorStatus = updateStatus_updating;

        return Core.publish("fromServer/shield/" + id, "updatesensorstatusrequest");
    }

    public boolean requestSettingUpdate() { //

        LOGGER.info("requestSettingUpdate:");

        settingsStatus = updateStatus_updating;

        Core.publish("fromServer/shield/" + id, "updatesettingstatusrequest");
        return true;
    }

    protected Result call(String method, String param, String path) {

        LOGGER.info("call: " + method + "," + param + "," + path);
        LOGGER.info("url: " + url.toString());

        Result result = null;
        if (method.equals("GET")) {
            result = callGet(param, path, url);
        } else if (method.equals("POST")) {
            result = callPost(param, path, url);
        }

        LOGGER.info("end call");
        return result;
    }



    /*public boolean sensorsIsNotUpdated() {

        Date currentDate = Core.getDate();
        boolean res = false;
        for (SensorBase sensors : sensors) {
            //SensorBase s = Shields.getSensorFromId(id);
            if (sensors.lastUpdate == null || (currentDate.getTime() - sensors.lastUpdate.getTime()) > (30*1000) ) {
                sensors.onlinestatus = Status_Offline;
                res = true;
            }
        }
        return res;
    }

    public boolean actuatorsIsNotUpdated() {

        Date currentDate = Core.getDate();
        boolean res = false;
        for (Actuator actuator : actuators) {
            //SensorBase s = Shields.getActuatorFromId(id);
            if (actuator.lastUpdate == null || (currentDate.getTime() - actuator.lastUpdate.getTime()) > (30*1000) ) {
                actuator.onlinestatus = Status_Offline;
                res = true;
            }
        }
        return res;
    }*/

    public boolean FromJson(JSONObject json) {

        try {
            Date date = Core.getDate();
            lastUpdate = date;
            if (json.has("MAC"))
                MACAddress = json.getString("MAC");
            if (json.has("shieldName"))
                boardName = json.getString("shieldName");
            if (json.has("localport"))
                port = json.getInt("localport");
            else
                port = 80;
            if (json.has("localIP")) {
                try {
                    url = new URL("http://" + json.getString("localIP"));
                    if (url.equals(new URL("http://0.0.0.0"))) {
                        LOGGER.info("url error: " + url.toString());
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.info("url error: " + e.toString());
                    return false;
                }
            }
            if (json.has("sensors")) {
                JSONArray jsonArray = json.getJSONArray("sensors");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject j = jsonArray.getJSONObject(i);
                    if (j.has("type")) {
                        String type = j.getString("type");
                        String name = "";
                        String subaddress = "";
                        String pin = "";
                        boolean enabled = true;
                        if (j.has("name"))
                            name = j.getString("name");
                        if (j.has("addr"))
                            subaddress = j.getString("addr");
                        if (j.has("pin"))
                            pin = j.getString("pin");
                        if (j.has("enabled"))
                            enabled = j.getBoolean("enabled");

                        SensorBase sensor = SensorFactory.createSensor(type, name, subaddress, 0, 0, pin, enabled);
                        if (sensor == null) {
                            continue;
                        } else {

                            if (j.has("childsensors")) {
                                JSONArray tempSensorArray = j.getJSONArray("childsensors");
                                for (int k = 0; k < tempSensorArray.length(); k++) {


                                    String childSubaddress = "";
                                    if (tempSensorArray.getJSONObject(k).has("addr"))
                                        childSubaddress = tempSensorArray.getJSONObject(k).getString("addr");

                                    String childName = "";
                                    if (tempSensorArray.getJSONObject(k).has("name"))
                                        childName = tempSensorArray.getJSONObject(k).getString("name");

                                    if (tempSensorArray.getJSONObject(k).has("id"))
                                        id = tempSensorArray.getJSONObject(k).getInt("id");

                                    if (tempSensorArray.getJSONObject(k).has("enabled"))
                                        enabled = tempSensorArray.getJSONObject(k).getBoolean("enabled");


                                    SensorBase childSensor = SensorFactory.createSensor("temperature", childName, childSubaddress, id, 0, "", enabled);
                                    if (childSensor != null)
                                        sensor.addChildSensor(childSensor);
                                }
                            }
                        }
                        sensors.add(sensor);
                    }
                }
            }
            /*if (json.has("actuators")) {
                JSONArray jsonArray = json.getJSONArray("actuators");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject j = jsonArray.getJSONObject(i);
                    if (j.has("type")) {
                        String type = j.getString("type");
                        Actuator actuator;
                        if (type.equals("heater")) {
                            actuator = (Actuator) new HeaterActuator();
                        } else if (type.equals("current")) {
                            actuator = (Actuator) new ReleActuator();
                        } else {
                            continue;
                        }
                        if (j.has("name"))
                            actuator.name = j.getString("name");
                        if (j.has("addr"))
                            actuator.subaddress = j.getString("addr");
                        if (j.has("type"))
                            actuator.type = j.getString("type");
                        actuators.add(actuator);
                    }
                }
            }*/

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LOGGER.info("json error: " + e.toString());
            return false;
        }
        return true;
    }


    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", id);
            if (boardName != null)
                json.put("boardname", boardName);
            if (MACAddress != null)
                json.put("macaddres", MACAddress);
            if (url != null)
                json.put("url", url);
            json.put("port", port);
            JSONArray jarray = new JSONArray();
            for (SensorBase sensor : sensors) {
                //SensorBase sensors = Shields.getSensorFromId(id);
                if (sensor != null)
                    jarray.put(sensor.getJson());
            }
            json.put("sensorIds", jarray);

            /*jarray = new JSONArray();
            for (SensorBase actuator : actuators) {
                //SensorBase actuator = Shields.getActuatorFromId(id);
                if (actuator != null)
                    jarray.put(actuator.getJson());
            }
            json.put("actuatorIds", jarray);*/

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public boolean updateSettings(JSONObject json) {

        Date date = Core.getDate();

        if (settings.updateFromJson(date, json)) {
            settingsStatus = updateStatus_updated;
            return true;
        } else {
            settingsStatus = updateStatus_notUpdated;
            return false;
        }
    }

    public SensorBase getFromSubaddress(String subaddress) {

        for (SensorBase sensor : sensors) {
            if (sensor.getSubaddress().equals(subaddress))
                return sensor;
            for (SensorBase child : sensor.childSensors) {
                if (child.getSubaddress().equals(subaddress))
                    return child;
            }
        }
        return null;
    }

    boolean updateSensors(JSONArray jsonArray) {

        Date date = Core.getDate();
        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                JSONObject json = jsonArray.getJSONObject(i);
                String subaddress = "";
                if (json.has("addr")) {
                    subaddress = json.getString("addr");
                }
                SensorBase sensor = getFromSubaddress(subaddress);
                if (sensor != null) {
                    sensor.updateFromJson(date, json);

                    if (json.has("childsensors")) {
                        JSONArray jsonChildSensorArray = json.getJSONArray("childsensors");
                        for (int k = 0; k < jsonChildSensorArray.length(); k++) {
                            JSONObject childSensor = jsonChildSensorArray.getJSONObject(k);
                            if (childSensor.has("addr")) {
                                subaddress = childSensor.getString("addr");
                                SensorBase child = getFromSubaddress(subaddress);
                                if (child != null)
                                    child.updateFromJson(date, childSensor);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                sensorStatus = updateStatus_notUpdated;
            }
        }
        sensorStatus = updateStatus_updated;
        return true;
    }


    public JSONObject getShieldSettingJson() {
        return settings.getJson();
    }

    public JSONObject getShieldSensorsJson() {

        JSONObject json = new JSONObject();
        try {
            json.put("shieldid", id);

            JSONArray jarray = new JSONArray();
            for (SensorBase sensor : sensors) {
                if (sensor != null)
                    jarray.put(sensor.getJson());
            }

            json.put("sensors", jarray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
