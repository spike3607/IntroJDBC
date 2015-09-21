/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package booksample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mschoenauer1
 */
public class MySqlDb {
    private Connection conn;
    
    public void openConnection(String driverClass, String url, String userName, String password) throws Exception {
        
        Class.forName (driverClass);
	conn = DriverManager.getConnection(url, userName, password);
        
    }
    
    public void closeConnection() throws SQLException {
        conn.close();
    }
    
    public List<Map<String,Object>> findAllRecords(String tableName) throws SQLException {
        List<Map<String,Object>> records = new ArrayList<>();
        
        String sql = "SELECT * FROM " + tableName;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        
        while(rs.next()) {
            Map<String,Object> record = new HashMap<>();
            for(int i=1; i <= columnCount; i++) {
                record.put(metaData.getColumnName(i), rs.getObject(i));
            }
            records.add(record);
        }
        return records;
    }
    
    public int deleteRecordByPrimaryKey(Object key, String keyIdentifier, String tableName) throws SQLException {
        
        String sql = "";
        
        if (key instanceof String) {
            sql = "DELETE FROM " + tableName + " WHERE " + keyIdentifier + " = '" + key + "'";
        }
        else {
            sql = "DELETE FROM " + tableName + " WHERE " + keyIdentifier + " = " + key.toString();
        }
        
        int updateCount = 0;
        
        Statement stmt = conn.createStatement();
        updateCount = stmt.executeUpdate(sql);
        
        return updateCount;
    }
    
    public int deleteRecordByPrimaryKeyPreparedStatement(Object key, String keyIdentifier, String tableName) throws SQLException {
        PreparedStatement pstmt = null;
        String sql = "DELETE FROM " + tableName + " WHERE " + keyIdentifier + " = ?";
        
        pstmt = conn.prepareStatement(sql);
        
        if (key instanceof String) {
            pstmt.setString(1, key.toString());
        }
        else {
            pstmt.setInt(1, (int)key);
        }
        
        int updateCount = pstmt.executeUpdate();
        
        return updateCount;
    }
    
    public static void main(String[] args) throws Exception {
        MySqlDb db = new MySqlDb();
        db.openConnection("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/book2", "root", "admin");
        
        List<Map<String,Object>> records = db.findAllRecords("author");
        for (Map record : records) {
            System.out.println(record);
        }
        
        int updateCount = db.deleteRecordByPrimaryKey(1, "author_id", "author");
        System.out.println("Deleted " + updateCount + " record(s)");
        
        records = db.findAllRecords("author");
        for (Map record : records) {
            System.out.println(record);
        }
        
        db.closeConnection();
    }
}
