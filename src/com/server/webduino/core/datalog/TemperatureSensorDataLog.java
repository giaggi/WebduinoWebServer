package com.server.webduino.core.datalog;

import com.server.webduino.core.Core;
import com.server.webduino.core.sensors.TemperatureSensor;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TemperatureSensorDataLog extends DataLog {

    public Double temperature = 0.0;
    public Double avTemperature = 0.0;
    public String tableName = "temperaturedatalog";

    private int sensorid;

    public TemperatureSensorDataLog(int sensorid) {
        super();
        this.sensorid = sensorid;
    }

    @Override
    public String getSQLInsert(String event, Object object) {

        TemperatureSensor temperatureSensor = (TemperatureSensor) object;
        String sql;
        sql = "INSERT INTO " + tableName + " (id, sensorid, date, temperature, avtemperature) VALUES ("
                + temperatureSensor.getId() + "," + temperatureSensor.getId() + ","  + getStrDate() + "," + temperatureSensor.getTemperature() + "," + temperatureSensor.getAvTemperature() + ");";
        return sql;
    }

    @Override
    public DataLogValues getDataLogValue(int id, Date startDate, Date endDate) {

        //ArrayList<DataLog> list = new ArrayList<DataLog>();
        try {
            // Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            // Open a connection
            Connection conn = DriverManager.getConnection(Core.getDbUrl(), Core.getUser(), Core.getPassword());
            // Execute SQL query
            Statement stmt = conn.createStatement();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String start = dateFormat.format(startDate);
            String end = dateFormat.format(endDate);

            String sql;
            sql = "SELECT * FROM temperaturedatalog WHERE id = " + id + " AND date BETWEEN '" + start + "' AND '" + end + "'" + "ORDER BY date ASC";

            ResultSet rs = stmt.executeQuery(sql);

            DataLogValues datalogvalues = new DataLogValues();
            datalogvalues.valueLabels.add("Temperatura");

            List<Double> temperatures = new ArrayList<>();
            while (rs.next()) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.");
                Date date = df.parse(String.valueOf(rs.getTimestamp("date")));
                datalogvalues.dates.add(date);
                temperature = rs.getDouble("temperature");
                temperatures.add(temperature);
            }
            datalogvalues.values.add(temperatures);
            // Clean-up environment
            rs.close();
            stmt.close();
            conn.close();
            return datalogvalues;

        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();

        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject toJson() throws JSONException {
        return null;
    }

    public class DataLogValue {
        String date;
        //List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();
    }
}
