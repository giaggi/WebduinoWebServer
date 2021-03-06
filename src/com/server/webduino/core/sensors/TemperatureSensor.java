package com.server.webduino.core.sensors;

import com.server.webduino.core.Core;
import com.server.webduino.core.datalog.TemperatureSensorDataLog;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class TemperatureSensor extends SensorBase {

    private static Logger LOGGER = Logger.getLogger(TemperatureSensor.class.getName());

    public static String temperaturesensortype = "temperaturesensor";
    //private double temperature;
    private double avTemperature;

    public TemperatureSensor(int id, String name, String description, String subaddress, int shieldid, String pin, boolean enabled) {

        super(id, name, description, subaddress, shieldid, pin, enabled);
        type = temperaturesensortype;
        hasvalue = true;
        valuetype = "Temperatura";
        valueunit = "°C";
        /*hasDoubleValue = true;
        doubleValue = .0;
        minDoubleValue = .0;
        maxDoubleValue = 30.0;
        stepDoubleValue = .1;*/
        datalog = new TemperatureSensorDataLog(id);
    }

    /*public boolean sendEvent(String eventtype) {
        if (super.sendEvent(eventtype) || eventtype == TemperatureEvents)
            return true;
        return false;
    }*/

    @Override
    public void writeDataLog(String event) {
        datalog.writelog(event, this);
    }

    public double getAvTemperature() {
        return avTemperature;
    }

    public void setAvTemperature(double avTemperature) {

        LOGGER.info("setAvTemperature: " + avTemperature);
        LOGGER.info("oldAvTemperature= " + avTemperature);

        double oldAvtemperature = this.avTemperature;
        this.avTemperature = avTemperature;
        // Notify everybody that may be interested.
        for (SensorListener listener : listeners) {
            if (listener instanceof TemperatureSensorListener) {
                TemperatureSensorListener l = (TemperatureSensorListener) listener;
                l.changeAvTemperature(id, avTemperature);
            }
        }
    }

    public double getTemperature() {
        return getValue();
    }

    public void setTemperature(double temperature) {

        LOGGER.info("setTemperature");

        //double oldtemperature = this.value;
        //this.value = temperature;
        this.setValue(temperature);
        //this.doubleValue = temperature;

        //TemperatureSensorDataLog dl = new TemperatureSensorDataLog();
        datalog.writelog("updateFromJson", this);
        // Notify everybody that may be interested.
        for (SensorListener listener : listeners) {
            /*if (listener instanceof TemperatureSensorListener) { // da rimuovere
                ((TemperatureSensorListener) listener).onUpdateTemperature(getId(), temperature, oldtemperature);
            }*/
            listener.onChangeValue(this, temperature);
        }
    }

    @Override
    public void updateFromJson(Date date, JSONObject json) {

        super.updateFromJson(date, json);
        LOGGER.info("updateFromJson json=" + json.toString());
        try {
            /*if (json.has("avtemperature"))
                setAvTemperature(json.getDouble("avtemperature"));*/
            if (json.has("temp"))
                setTemperature(json.getDouble("temp"));


            JSONObject jsonstatus = new JSONObject();
            try {
                jsonstatus.put("sensorid", id);
                jsonstatus.put("shieldid", shieldid);
                jsonstatus.put("name", name);
                jsonstatus.put("description", description);
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                jsonstatus.put("date", df.format(Core.getDate()));
                jsonstatus.put("value", getValue());
                jsonstatus.put("valuetextxx", valuetext);
                jsonstatus.put("valuetype", valuetype);
                jsonstatus.put("valueunit", valueunit);
                jsonstatus.put("lastUpdate", lastUpdate);
                jsonstatus.put("type", type);
                //json.put("zoneid", zoneId);
                //json.put("zonesensorid", remoteSensorId);

                String message = "";
                message += jsonstatus.toString();
                /*message = "{";
                message += "\"id\":" + "\"" + id + "\"";
                message += ", \"name\":" + "\"" + name + "\"";
                message += ", \"description\":" + "\"" + description + "\"";
                message += ", \"date\":" + "\"" + df.format(Core.getDate()) + "\"";
                message += ", \"lastUpdate\":" + "\"" + lastUpdate + "\"";
                message += ", \"type\":" + "\"" + type + "\"";
                message += ", \"value\":" + getValue();
                message += ", \"temperature\":" + "\"" + getValue() + "\"" ;
                message += "}";*/
                Core.updateHomeAssistant("homeassistant/sensor/"+ id , message);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }


            //updateHomeAssistant("homeassistant/sensor/" + id + "/availability", "online");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LOGGER.info("json error: " + e.toString());
            writeDataLog("updateFromJson error");
        }

    }

    @Override
    public void getJSONField(JSONObject json) {
        try {
            json.put("temperature", getTemperature());
            json.put("avtemperature", getAvTemperature());
            //json.put("value", getTemperature());
            //json.put("valuetext", getTemperature() + "°C");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface TemperatureSensorListener extends SensorBase.SensorListener {
        public String TemperatureEvents = "temperature event";

        void onUpdateTemperature(int sensorId, double temperature, double oldtemperature);

        void changeAvTemperature(int sensorId, double avTemperature);
    }
}
