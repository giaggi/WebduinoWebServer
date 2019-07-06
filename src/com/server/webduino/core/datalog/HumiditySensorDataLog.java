package com.server.webduino.core.datalog;

import com.server.webduino.core.Core;
import com.server.webduino.core.sensors.HumiditySensor;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HumiditySensorDataLog extends DataLog {

    public int humidity = 0;
    public String tableName = "currentdatalog";

    private int sensorid;

    public HumiditySensorDataLog(int sensorid) {
        super();
        this.sensorid = sensorid;
    }

    @Override
    public String getSQLInsert(String event, Object object) {

        HumiditySensor humiditySensor = (HumiditySensor) object;
        String sql;
        sql = "INSERT INTO " + tableName + " (id, subaddress, date, current) VALUES ("
                + humiditySensor.getId() + ",'" + humiditySensor.getSubaddress() + "',"  + getStrDate() + "," + humiditySensor.getHumidity() + ");";
        return sql;
    }

    /*@Override
    public DataLog.DataLogValues getDataLogValue(int id, Date startDate, Date endDate) {

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
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.");
                date = df.parse(String.valueOf(rs.getTimestamp("date")));
                humidity = rs.getInt("humidity");
                list.add(this);
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
    }*/
}
