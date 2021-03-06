package com.server.webduino;

import com.server.webduino.core.Core;
import org.json.JSONException;
import org.json.JSONObject;
import java.sql.*;

public abstract class DBObject {

    public void save() throws Exception {
            Connection conn = DriverManager.getConnection(Core.getDbUrl(), Core.getUser(), Core.getPassword());
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            delete(stmt);
            write(conn);
            stmt.close();
            conn.commit();
    }

    public void remove() throws SQLException, Exception {
            Connection conn = DriverManager.getConnection(Core.getDbUrl(), Core.getUser(), Core.getPassword());
            conn.setAutoCommit(false);
            //Statement stmt = conn.createStatement();
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            delete(stmt);
            stmt.close();
            conn.commit();
    }

    abstract public JSONObject toJson() throws JSONException;
    abstract public void fromJson(JSONObject json) throws Exception;
    abstract protected void delete(Statement stmt) throws SQLException;
    abstract protected void write(Connection conn) throws SQLException, Exception;
}
