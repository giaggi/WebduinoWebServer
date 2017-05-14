package com.server.webduino.core.sensors.commands;

import com.server.webduino.core.Core;
import com.server.webduino.core.DataLog;
import com.server.webduino.core.sensors.CurrentSensor;
import com.server.webduino.core.sensors.DoorSensor;
import com.server.webduino.core.sensors.SensorBase;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DoorSensorDataLog extends DataLog {

    private boolean open = false;
    private String tableName = "doordatalog";

    @Override
    public String getSQLInsert(String event, SensorBase sensor) {

        DoorSensor doorSensor = (DoorSensor) sensor;
        String sql;
        sql = "INSERT INTO " + tableName + " (id, date, open) VALUES ("
                + doorSensor.getId() + "',"  + getStrDate() + "," + doorSensor.getStatus() + ");";
        return sql;
    }

    @Override
    public ArrayList<DataLog> getDataLog(int id, Date startDate, Date endDate) {

        ArrayList<DataLog> list = new ArrayList<DataLog>();
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
            sql = "SELECT * FROM " + tableName + " WHERE id = " + id + " AND date BETWEEN '" + start + "' AND '" + end + "'" + "ORDER BY date ASC";

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                DoorSensorDataLog data = new DoorSensorDataLog();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.");
                data.date = df.parse(String.valueOf(rs.getTimestamp("date")));
                data.open = rs.getBoolean("open");
                list.add(data);
            }
            // Clean-up environment
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        }
        return list;
    }
}
