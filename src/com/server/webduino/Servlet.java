package com.server.webduino;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

// Loading required libraries


/**
 * Created by Giacomo Span� on 07/11/2015.
 */
public class Servlet extends javax.servlet.http.HttpServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    }

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    //static final String DB_URL = "jdbc:mysql://192.168.1.100/foo";
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/foo";
    //static final String DB_URL = "jdbc:mysql://localhost/foo";

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "giacomo";

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        /*PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<body>");
        out.println("<h1>Hello Servlet Get</h1>");
        out.println("</body>");

        out.println("</html>");*/




        // Set response content type
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String title = "Database httpClientResult";
        String docType =
                "<!doctype html public \"-//w3c//dtd html 4.0 " +
                        "transitional//en\">\n";
        out.println(docType +
                "<html>\n" +
                "<head><title>" + title + "</title></head>\n" +
                "<body bgcolor=\"#f0f0f0\">\n" +
                "<h1 align=\"center\">" + title + "</h1>\n");






    }
}

