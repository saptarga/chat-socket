/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Config;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author saptarga
 */
public class Koneksi {

    private static Connection koneksiDb;

    public static Connection getKoneksi() {
        if (koneksiDb == null) {
            try {
                // String url = "jdbc:mysql://72.29.127.21:3306/poenyath_chattingan";
                String url = "jdbc:mysql://127.0.0.1:3306/chat";
                String username = "root";
                String password = "";

                DriverManager.registerDriver(new com.mysql.jdbc.Driver());
                koneksiDb = DriverManager.getConnection(url, username, password);
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
        return koneksiDb;
        
    }
}
