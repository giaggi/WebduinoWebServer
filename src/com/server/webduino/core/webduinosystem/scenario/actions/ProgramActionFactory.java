package com.server.webduino.core.webduinosystem.scenario.actions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by giaco on 12/05/2017.
 */
public class ProgramActionFactory {

    public static final String DELAYALARM = "delayalarm";
    public static final String KEEPTEMPERATURE = "keeptemperature";
    public static final String KEEPOFF = "keepoff";
    public static final String VOIPCALL = "voipcall";
    public static final String TRIGGERSTATUS = "triggerstatus";

    public ProgramAction createProgramAction(int id, int programtimerangeid, String type, String name, String description, int priority, int actuatorid, double targevalue, double thresholdvalue,
                                             int zoneId, int seconds, boolean enabled) throws Exception {
        ProgramAction programActions = null;
        if (type.equals(DELAYALARM)) {
            programActions = new DelayAlarmProgramActions(id, programtimerangeid, type, name, description, priority, actuatorid, targevalue, thresholdvalue,
                    zoneId, seconds, enabled);
        } else if (type.equals(VOIPCALL)) {
            programActions = new VoIPCallProgramAction(id, programtimerangeid, type, name, description, priority, actuatorid, targevalue, thresholdvalue,
                    zoneId, seconds, enabled);
        } else if (type.equals(TRIGGERSTATUS)) {
            programActions = new TriggerStatusProgramAction(id, programtimerangeid, type, name, description, priority, actuatorid, targevalue, thresholdvalue,
                    zoneId, seconds, enabled);
        } else if (type.equals(KEEPTEMPERATURE)) {
            programActions = new KeepTemperatureProgramAction(id, programtimerangeid, type, name, description, priority, actuatorid, targevalue, thresholdvalue,
                    zoneId, seconds, enabled);
        } else if (type.equals(KEEPOFF)) {
            programActions = new KeepOffProgramActions(id, programtimerangeid, type, name, description, priority, actuatorid, targevalue, thresholdvalue,
                    zoneId, seconds, enabled);
        } else if (type.equals("immediatealarm") || type.equals("perimetrale") || type.equals("path") || type.equals("24hours")) {
            programActions = new ProgramAction(id, programtimerangeid, type, name, description, priority, actuatorid, targevalue, thresholdvalue,
            zoneId, seconds, enabled);
        } else if (type.equals("instruction")) { // istruzione generica vuota per inserimento nuoiva
            programActions = new ProgramAction(id, programtimerangeid, type, name, description, priority, actuatorid, targevalue, thresholdvalue,
                    zoneId, seconds, enabled);
        } else {
            throw new Exception("type:" + type + "does not exist");
        }



        programActions.init();
        return programActions;
    }

    public ProgramAction fromResultSet(Connection conn, ResultSet resultSet) throws Exception {
        int id = resultSet.getInt("id");
        int timerangeid = resultSet.getInt("timerangeid");
        String type = resultSet.getString("type");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        int actuatorid = resultSet.getInt("actuatorid");
        double targetvalue = (double) resultSet.getDouble("targetvalue");
        double thresholdvalue = resultSet.getDouble("thresholdvalue");
        int zoneId = resultSet.getInt("zoneid");
        int seconds = 0;
        Time time = resultSet.getTime("time");
        if (time != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(time);
            seconds = cal.get(Calendar.SECOND);
        }
        Boolean enabled = resultSet.getBoolean("enabled");
        int priority = resultSet.getInt("priority");


        try {
            ProgramAction action = createProgramAction(id, timerangeid, type, name, description, priority, actuatorid, targetvalue, thresholdvalue, zoneId, seconds, enabled);
            action.conditions = readConditions(conn,action.id);
            action.actions = readActions(conn,action.id);
            return action;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private List<Condition> readConditions(Connection conn, int programactionid) throws Exception {

        List<Condition> list = new ArrayList<>();
        String sql;
        Statement stmt4 = conn.createStatement();
        sql = "SELECT * FROM scenarios_conditions WHERE programactionid=" + programactionid + " ;";
        ResultSet resultSet = stmt4.executeQuery(sql);
        ProgramActionFactory factory = new ProgramActionFactory();
        while (resultSet.next()) {

            Condition condition = new Condition(conn,resultSet);
            if (condition != null)
                list.add(condition);
        }
        resultSet.close();
        stmt4.close();
        return list;
    }

    private List<Action> readActions(Connection conn, int programactionid) throws Exception {

        List<Action> list = new ArrayList<>();
        String sql;
        Statement stmt4 = conn.createStatement();
        sql = "SELECT * FROM scenarios_actions WHERE programactionid=" + programactionid + " ;";
        ResultSet resultSet = stmt4.executeQuery(sql);
        ProgramActionFactory factory = new ProgramActionFactory();
        while (resultSet.next()) {

            Action action = new Action(conn,resultSet);
            if (action != null)
                list.add(action);
        }
        resultSet.close();
        stmt4.close();
        return list;
    }

    public ProgramAction fromJson(JSONObject json) throws Exception {

            String type = "";
            if (json.has("type"))
                type = json.getString("type");
            else
                throw new JSONException("type key missing");

            int id = 0, timerangeid = 0, actuatorid = 0, zoneId = 0, seconds = 0, priority = 0;
            String name = "";
            String description = "";
            double targetvalue = 0.0;
            double thresholdvalue = 0.0;
            boolean enabled = true;

            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Calendar myCal = Calendar.getInstance();
            myCal.set(Calendar.YEAR, 0);
            myCal.set(Calendar.MONTH, 0);
            myCal.set(Calendar.DAY_OF_MONTH, 0);
            myCal.set(Calendar.HOUR_OF_DAY, 0);
            myCal.set(Calendar.MINUTE, 0);
            myCal.set(Calendar.SECOND, 0);

            if (json.has("id"))
                id = json.getInt("id");
            if (json.has("timerangeid")) timerangeid = json.getInt("timerangeid");
            if (json.has("name")) name = json.getString("name");
            if (json.has("description")) description = json.getString("description");
            if (json.has("priority")) priority = json.getInt("priority");
            if (json.has("actuatorid")) actuatorid = json.getInt("actuatorid");
            if (json.has("targetvalue")) targetvalue = json.getDouble("targetvalue");
            if (json.has("thresholdvalue")) thresholdvalue = json.getDouble("thresholdvalue");
            if (json.has("zoneid")) zoneId = json.getInt("zoneid");
            /*if (json.has("seconds")) {
                DateFormat timeFormat = new SimpleDateFormat("HH:mm");
                String timeStr = json.getString("seconds");
                try {
                    Date time = timeFormat.parse(timeStr);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(time);
                    seconds = cal.get(Calendar.SECOND);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }*/
            if (json.has("enabled")) enabled = json.getBoolean("enabled");


        ProgramAction action = null;
        try {
            action = createProgramAction(id, timerangeid, type, name, description, priority, actuatorid, targetvalue, thresholdvalue,
                    zoneId, seconds, enabled);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return action;
    }

    public static JSONArray getProgramIntructionTypesJSONArray() {
        JSONArray jsonArray = new JSONArray();
        JSONObject json;
        try {
            json = new JSONObject();
            json.put("instruction", DELAYALARM);
            json.put("description", "Delayalarm");
            jsonArray.put(json);

            json = new JSONObject();
            json.put("instruction", VOIPCALL);
            json.put("description", "VoipCall");
            jsonArray.put(json);

            json = new JSONObject();
            json.put("instruction", TRIGGERSTATUS);
            json.put("description", "triggerstatus");
            jsonArray.put(json);

            json = new JSONObject();
            json.put("instruction", KEEPTEMPERATURE);
            json.put("description", "Keeptemperature");
            jsonArray.put(json);

            json = new JSONObject();
            json.put("instruction", KEEPOFF);
            json.put("description", "Keepoff");
            jsonArray.put(json);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }
}
