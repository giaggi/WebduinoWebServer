package com.server.webduino.core;

import com.server.webduino.DBObject;
import com.server.webduino.core.Core;
import com.server.webduino.core.sensors.SensorBase;
import com.server.webduino.core.webduinosystem.zones.Zone;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by giaco on 17/05/2017.
 */
public class Trigger extends DBObject {

    public int id = 0;
    public String name = "";
    public String status = "";
    public java.util.Date date;

    public Trigger(int id, String name, String status, java.util.Date date) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.date = date;
    }

    public Trigger(JSONObject json) throws Exception {
        fromJson(json);
    }


    public String getStatus() {
        return "--";
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
            json.put("id", id);
            json.put("name", name);
            json.put("status", status);
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            if (date != null)
                json.put("nextjobdate", df.format(date));

        return json;
    }

    @Override
    public void fromJson(JSONObject json) throws Exception {

        if (json.has("id"))
            id = json.getInt("id");
        if (json.has("name"))
            name = json.getString("name");
        if (json.has("status"))
            status = json.getString("status");
    }

    @Override
    public void delete(Statement stmt) throws SQLException {
        String sql = "DELETE FROM triggers WHERE id=" + id;
        stmt.executeUpdate(sql);
    }



    public void saveStatus() throws Exception {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(Core.getDbUrl(), Core.getUser(), Core.getPassword());
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            write(conn);
            stmt.close();
            conn.commit();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new Exception(e.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception(e.toString());
        }
    }

    public void write(Connection conn) throws SQLException {

        String sql;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = Core.getDate();

        sql = "INSERT INTO triggers (id, name, status, lastupdate)" +
                " VALUES ("
                + id + ","
                + "\"" + name + "\","
                + "\"" + status + "\","
                + "'" + df.format(date) + "' "
                + ") ON DUPLICATE KEY UPDATE "
                + "name=\"" + name + "\","
                + "status=\"" + status + "\","
                + "lastupdate='" + df.format(date) + "' "
                + ";";
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        Integer affectedRows = stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            id = rs.getInt(1);
        }
    }

}