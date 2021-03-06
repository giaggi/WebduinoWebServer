package com.server.webduino.core.datalog;

import com.server.webduino.core.Core;
import com.server.webduino.core.webduinosystem.scenario.actions.Action;

import java.text.SimpleDateFormat;

public class ActionDataLog extends DataLog {

    public int id;
    private String table = "actiondatalog";

    @Override
    public String getSQLInsert(String event, Object object) {

        Action action = (Action) object;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String sql = "";

        sql = "INSERT INTO " + table + " (date,event,actionid)" +
                " VALUES ("
                + "'" + df.format(Core.getDate()) + "',"
                + "'" + event + "',"
                + action.id + ");";

        return sql;
    }

    @Override
    public int writelog(String event, Object object) {
        // salva id perchè è riutilizzato nella chiamata successiva
        id = super.writelog(event, object);
        return id;
    }
}
