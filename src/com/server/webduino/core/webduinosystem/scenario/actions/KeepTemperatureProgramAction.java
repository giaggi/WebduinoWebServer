package com.server.webduino.core.webduinosystem.scenario.actions;

import com.server.webduino.core.Command;
import com.server.webduino.core.Core;
import com.server.webduino.core.sensors.HeaterActuator;
import com.server.webduino.core.sensors.commands.HeaterActuatorCommand;
import com.server.webduino.core.sensors.SensorBase;
import com.server.webduino.core.webduinosystem.zones.Zone;
import org.json.JSONException;
import org.json.JSONObject;
import sun.management.Sensor;

import java.text.SimpleDateFormat;

/**
 * Created by giaco on 17/05/2017.
 */
public class KeepTemperatureProgramAction extends ProgramAction /*implements SensorBase.SensorListener*/ /*implements Command.CommandListener*/ {

    private double targetTemperature;
    private int duration = 1000;
    private double temperature;
    private HeaterListenerClass heaterListener = new HeaterListenerClass();

    public KeepTemperatureProgramAction(int id, int programtimerangeid, String type, String name, String description, int priority, int actuatorid, double targevalue, double thresholdvalue,
                                        int zoneId, int seconds, boolean enabled) {
        super(id, programtimerangeid, type, name, description, priority, actuatorid, targevalue, thresholdvalue,
                zoneId, seconds, enabled);

        targetTemperature = targetvalue;

    }

    @Override
    public void start() {
        super.start();
        requestZoneStatusUpdate();

        SensorBase sensor = Core.getSensorFromId(actuatorid);
        if (sensor != null) {
            if (sensor instanceof HeaterActuator) {
                HeaterActuator heater = (HeaterActuator) sensor;
                heater.addListener(heaterListener);
            }
        } else {

        }

    }

    private void requestZoneStatusUpdate() {
        Zone zone = Core.getZoneFromId(zoneId);
        if (zone != null) {
            zone.requestSensorStatusUpdate();
            zone.addListener(this);
        }
    }

    @Override
    public void stop() {
        super.stop();
        Zone zone = Core.getZoneFromId(zoneId);
        if (zone != null) {
            zone.removeListener(this);
        }

        SensorBase sensor = Core.getSensorFromId(actuatorid);
        if (sensor != null) {
            sensor.deleteListener(heaterListener);
        }
    }

    @Override
    public void finalize() {
        stop();
        System.out.println("Book instance is getting destroyed");
    }

    @Override
    public String getStatus() {
        String status;
        Zone zone = Core.getZoneFromId(zoneId);
        if (zone != null) {
            status = "Zona: " + zoneId + "." + zone.getName() + " - Temperatura: " + zone.getTemperature() + " °C";
            status += " - Target: " + targetTemperature + " °C";
        } else {
            status = zone + "not found";
        }

        status += " - Actuator: ";
        SensorBase sensor = Core.getSensorFromId(actuatorid);
        if (sensor != null && sensor instanceof HeaterActuator) {

            status += sensor.getId() + "." + sensor.getName();
            status += " - Status: " + ((HeaterActuator) sensor).getStatus();

            status += " - Relè: ";
            if (((HeaterActuator)sensor).getReleStatus())
                status += "acceso";
            else
                status += "spento";
        } else {
            status += actuatorid + " not found";
        }

        return status;
    }

    @Override
    public void onTemperatureChange(int zoneId, double newTemperature, double oldTemperature) {

        temperature = newTemperature;
        if (!active) return;


        Command.CommandResult result;

        try {
            JSONObject json = new JSONObject();
            json.put("actuatorid", actuatorid);

            SensorBase sensor = Core.getSensorFromId(actuatorid);
            if (sensor != null)
                json.put("shieldid", sensor.getShieldId());
            json.put("command", HeaterActuatorCommand.Command_KeepTemperature);
            json.put("duration", duration);
            json.put("target", targetTemperature);
            json.put("actionid", id);
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            json.put("date", df.format(Core.getDate()));
            json.put("zone", zoneId);
            json.put("temperature", temperature);

            HeaterActuatorCommand cmd = new HeaterActuatorCommand(json);
            result = cmd.send();

        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    private class HeaterListenerClass implements SensorBase.SensorListener {

        @Override
        public void changeOnlineStatus(boolean online) {

        }

        @Override
        public void changeOnlineStatus(int sensorId, boolean online) {

        }

        @Override
        public void onChangeStatus(String newStatus, String oldStatus) {
            if (oldStatus.equals(HeaterActuator.STATUS_MANUAL)) {
                requestZoneStatusUpdate();
            }
        }

        @Override
        public void changeDoorStatus(int sensorId, boolean open, boolean oldOpen) {

        }
    }
}