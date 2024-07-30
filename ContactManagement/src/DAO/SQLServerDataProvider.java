/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

/**
 *
 * @author levan
 */
public class SQLServerDataProvider {
    private Connection connection;
    public void open(){
        String strServer="DESKTOP-273A8MJ\\SQLEXPRESS";
        String strDatabase="QL_DanhBa";
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String connectionURL="jdbc:sqlserver://"+strServer
                                +":1433;databaseName="+strDatabase
                                +";encrypt=true;trustServerCertificate=true;user=sa;password=123";
            connection=DriverManager.getConnection(connectionURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void close(){
        try {
            this.connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public ResultSet executeQuery(String sql,int parameter){
        ResultSet rs=null;
        try {
            Statement sm=connection.createStatement();
            rs=sm.executeQuery(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
    }
    
    public int executeUpdate(String sql){
        int n=-1;
        try {
            Statement sm=connection.createStatement();
            n=sm.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return n;
    }
    
    public Connection getConnection() {
        return this.connection;
    }
            
}
