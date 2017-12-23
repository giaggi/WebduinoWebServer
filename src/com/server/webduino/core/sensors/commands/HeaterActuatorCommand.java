package com.server.webduino.core.sensors.commands;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

/**
 * Created by Giacomo Spanò on 29/12/2016.
 */
public class HeaterActuatorCommand extends Command {

    private static final Logger LOGGER = Logger.getLogger(HeaterActuatorCommand.class.getName());

    public static final String Command_KeepTemperature = "keeptemperature"; // "programoff";
    public static final String Command_Off = "off";
    public static final String Command_SendTemperature = "sendtemperature"; // "sendtemperature";
    public static final String Command_Manual = "manual"; // "sendtemperature";

    public int actuatorid;
    public long duration;
    public double targetTemperature;
    public int scenario;
    public int timeInterval;
    public int zone;
    public double temperature;
    public int actionid;
    public String date;
    public String enddate;

    public HeaterActuatorCommand(JSONObject json) throws JSONException {
        super(json);
    }

    @Override
    public void fromJson(JSONObject json) throws JSONException {

            if (json.has("actuatorid"))
                actuatorid = json.getInt("actuatorid");
            if (json.has("shieldid"))
                shieldid = json.getInt("shieldid");
            if (json.has("command"))
                command = json.getString("command");
            if (json.has("duration"))
                duration = json.getInt("duration");
            if (json.has("target"))
                targetTemperature = json.getDouble("target");
            if (json.has("temperature"))
                temperature = json.getDouble("temperature");
            if (json.has("actionid"))
                actionid = json.getInt("actionid");
            if (json.has("zone"))
                zone = json.getInt("zone");
            if (json.has("date"))
                date = json.getString("date");
            if (json.has("enddate"))
                enddate = json.getString("enddate");
    }

    @Override
    public JSONObject getJSON() {

        JSONObject json = new JSONObject();

        try {
            json.put("actuatorid", actuatorid);
            json.put("uuid",uuid);
            if (command.equals(HeaterActuatorCommand.Command_KeepTemperature)) {

                json.put("command", HeaterActuatorCommand.Command_KeepTemperature);
                json.put("duration", duration);
                json.put("target", targetTemperature);
                json.put("zone", zone);
                json.put("temperature", temperature);
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                json.put("actionid", actionid);
                json.put("date", date);
                json.put("enddate", enddate);

            } else if (command.equals(HeaterActuatorCommand.Command_Off)) {

                json.put("command", HeaterActuatorCommand.Command_Off);
                json.put("duration", duration);
                json.put("scenario", scenario);
                json.put("timeinterval", timeInterval);
                json.put("zone", zone);
                json.put("actionid", actionid);
                json.put("date", "");

            } else if (command.equals(HeaterActuatorCommand.Command_Manual)) {

                json.put("command", HeaterActuatorCommand.Command_Manual);
                json.put("duration", duration);
                json.put("target", targetTemperature);
                json.put("zone", zone);
                json.put("temperature", temperature);
                json.put("date", date);
                json.put("enddate", enddate);

            } else if (command.equals(HeaterActuatorCommand.Command_SendTemperature)) {

                json.put("command", HeaterActuatorCommand.Command_SendTemperature);
                json.put("temperature", temperature);

            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }


}
