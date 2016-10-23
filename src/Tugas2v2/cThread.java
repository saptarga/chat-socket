/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tugas2v2;

import Config.Koneksi;
import Config.KoneksiAkademik;
import com.mysql.jdbc.PreparedStatement;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

/**
 *
 * @author saptarga
 */
public class cThread extends Thread {

    String nick;
    Boolean connected;
    Socket socket;
    PrintWriter out;
    BufferedReader in;
    Socket clientSocket;
    static Connection connAkademik = KoneksiAkademik.getKoneksi();
    static Connection conn = Koneksi.getKoneksi();
    String hasil;
    public SimpleDateFormat dateFormatter;
    public Date date1;

    cThread(Socket s) {
        super("cThread");
        connected = false;
        nick = "";
        clientSocket = s;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            date1 = new Date();
            dateFormatter = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        } catch (Exception e) {
            System.out.println("salah 1" + e);
        }
    }

    public boolean equals(cThread c) {
        return (c.nick.equals(this.nick));
    }

    synchronized void send(String msg) {
        out.println(msg);
    }

    void listen() {
        try {
            while (true) {
                String msg = in.readLine();
                System.out.println("Activity : " + msg);
                if (msg.startsWith("Login")) {
                    login(msg);
                    System.out.println("Complete Login User");
                } else if (msg.equals("Logout")) {
                    if (connected) {
                        int k = server.clients.indexOf(this);
                        server.clients.remove(this);
                        sendList();
                        try {
                            PreparedStatement stat = (PreparedStatement) conn.prepareStatement("Insert into history(user,log,time) values( ?,  ?, ?)");
                            try {
                                stat.setString(1, nick);
                                stat.setString(2, "Logout");
                                stat.setString(3, dateFormatter.format(date1));
                                stat.executeUpdate();
                            } catch (SQLException se) {
                                JOptionPane.showMessageDialog(null, "Data tidak disimpan...Entry Ulang \nPesan Error : \n " + se);
                            }
                            stat.close();
                        } catch (Exception er) {
                            System.out.println("Pesan Error-E = " + er);
                        }
                        connected = false;
                        out.println("OK");
                        out.close();
                        in.close();
                        clientSocket.close();
                        return;
                    } else {
                        send("Not Logged in !!");
                    }
                } else if (msg.startsWith("Post ")) {
                    for (int i = 0; i < server.clients.size(); i++) {
                        cThread t = (cThread) server.clients.get(i);
                        if (t.connected) {
                            t.send("Recieve " + nick + ": " + msg.substring(5, msg.length()));
                        }
                    }
                    try {
                        PreparedStatement stat = (PreparedStatement) conn.prepareStatement("Insert into history(user,log,time) values( ?,  ?, ?)");
                        try {
                            stat.setString(1, nick);
                            stat.setString(2, "Post " + msg.substring(5, msg.length()));
                            stat.setString(3, dateFormatter.format(date1));
                            stat.executeUpdate();
                        } catch (SQLException se) {
                            JOptionPane.showMessageDialog(null, "Data tidak disimpan...Entry Ulang \nPesan Error : \n " + se);
                        }
                        stat.close();
                    } catch (Exception er) {
                        System.out.println("Pesan Error-E = " + er);
                    }
                } else if (msg.startsWith("PrivatePost ")) {
                    StringTokenizer st = new StringTokenizer(msg.substring(12, msg.length()), ", ");
                    String message = st.nextToken();
                    String to = st.nextToken();
                    boolean success = false;
                    for (int i = 0; i < server.clients.size(); i++) {
                        cThread t = (cThread) server.clients.get(i);
                        if (t.nick.equals(to)) {
                            t.send("PrivateRecieve " + t.nick + ": " + message);
                            success = true;
                            try {
                                PreparedStatement stat = (PreparedStatement) conn.prepareStatement("Insert into history(user,log,time) values( ?,  ?, ?)");
                                try {
                                    stat.setString(1, nick);
                                    stat.setString(2, "PrivatePost " + message);
                                    stat.setString(3, dateFormatter.format(date1));
                                    stat.executeUpdate();
                                } catch (SQLException se) {
                                    JOptionPane.showMessageDialog(null, "Data tidak disimpan...Entry Ulang \nPesan Error : \n " + se);
                                }
                                stat.close();
                            } catch (Exception er) {
                                System.out.println("Pesan Error-E = " + er);
                            }
                            break;
                        }
                    }
                    if (!success) {
                        send("Error!");
                    }
                } else if (msg.startsWith("Info")) {
                    String info = msg.substring(11);
                    int p = info.indexOf(",");
                    int plus = info.indexOf("+");
                    String request = info.substring(0, p);
                    String user = info.substring(p+1,info.length());
                    String nim = info.substring(plus+1, p);
                    System.out.println("Request "+request);
                    System.out.println("User Request : "+user);
                    System.out.println("NIM  : "+nim);
                    if(request.equals("mhs")){
                        tampilDataMahasiswa();
                        boolean success = false;
                        for (int i = 0; i < server.clients.size(); i++) {
                            cThread t = (cThread) server.clients.get(i);
                            if (t.nick.equals(user)) {
                                t.send("Request Daftar Mahasiswa : <>" + hasil);
                                success = true;
                                break;
                            }
                        }
                        if (!success) {
                            send("Error!");
                        }
                        System.out.println(hasil);
                    }else if(request.startsWith("mhs+")){
                        tampilDetailMahasiswa(nim);
                        boolean success = false;
                        for (int i = 0; i < server.clients.size(); i++) {
                            cThread t = (cThread) server.clients.get(i);
                            if (t.nick.equals(user)) {
                                t.send("Request Data Mahasiswa : <>" + hasil);
                                success = true;
                                break;
                            }
                        }
                        if (!success) {
                            send("Error!");
                        }
                    }else{
                        send("Request Hasil Tidak Ditemukan <>"
                                + "-------------------------------------------------------- <>"
                                + "Request : <> "
                                + "@Info mhs   Menampilkan Daftar Nahasiswa <> "
                                + "@Info mhs+nim Menampilkan Data Mahasiswa Berdasarkan NIM <>"
                                + "--------------------------------------------------------");
                    }     
                } else {
                    send(msg);
                }
            }
        } catch (SocketException e) {
            if (connected) {
                try {
                    connected = false;
                    int k = server.clients.indexOf(this);
                    server.clients.remove(this);
                    sendList();
                    out.close();
                    in.close();
                    clientSocket.close();
                    return;
                } catch (Exception d) {
                    return;
                }
            }
        } catch (Exception e) {
            System.out.println("salah2 " + e);
        }
    }

    public void run() {
        listen();
    }

    boolean login(String msg) {
        if (connected) {
            out.println("Already Connected!");
            return true;
        }
        boolean exists = false;
        for (int i = 0; i < server.clients.size(); i++) {
            if (server.clients.get(i) != null) {
                cThread temp = (cThread) server.clients.get(i);
                if ((temp.nick).equals(msg.substring(7, msg.length()))) {
                    exists = true;
                    break;
                }
            }
        }
        if (exists) {
            out.println("New Nick");
        } else {
            connected = true;
            nick = msg.substring(7, msg.length());
            System.out.println("Activity : sendList");
             try {
                PreparedStatement stat = (PreparedStatement) conn.prepareStatement("Insert into history(user,log,time) values( ?,  ?, ?)");
                try {
                    stat.setString(1, nick);
                    stat.setString(2, "Login");
                    stat.setString(3, dateFormatter.format(date1));
                    stat.executeUpdate();
                } catch (SQLException se) {
                    JOptionPane.showMessageDialog(null, "Data tidak disimpan...Entry Ulang \nPesan Error : \n " + se);
                }
                stat.close();
            } catch (Exception er) {
                System.out.println("Pesan Error-E = " + er);
            }
            sendList();
        }
        
        return true;
    }

    void sendList() {
        String list = "";
        System.out.println("Server Client Size => " + server.clients.size());
        if (server.clients.size() == 0) {
            return;
        }
        for (int i = 0; i < server.clients.size(); i++) {
            cThread temp = (cThread) server.clients.get(i);
            if (server.clients.get(i) != null) {
                if (connected) {
                    list = temp.nick + "," + list;
                }
            }
        }
        list = "List " + list.substring(0, list.length() - 1) + ";";
        for (int i = 0; i < server.clients.size(); i++) {
            cThread t = (cThread) server.clients.get(i);
            if (t.connected) {
                t.send(list);
            }
        }
    }

    static String replace(String str, String pattern, String replace) {
        int s = 0;
        int e = 0;
        StringBuffer result = new StringBuffer();
        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(replace);
            s = e + pattern.length();
        }
        result.append(str.substring(s));
        return result.toString();
    }

    void tampilDataMahasiswa() {
        hasil = "";
        try {
            Statement stat = connAkademik.createStatement();
            ResultSet rSet = stat.executeQuery("Select * from mahasiswa ");
            String line = "---------------------------------------------";
            while (rSet.next()) {
                String nim = rSet.getString("nim");
                String nama = rSet.getString("nama");
                String jk = rSet.getString("jk");
                String alamat = rSet.getString("alamat");
                String email = rSet.getString("email");
                String jurusan = rSet.getString("jurusan");
                hasil = hasil+"NIM : "+nim+" <>"
                        + "Nama : "+nama+" <>"
                        + "Jenis Kelamin : "+jk+" <>"
                        + "Alamat : "+alamat+" <>"
                        + "Jurusan : "+jurusan+" <>"+line+"<>";
            }
            stat.close();
        } catch (SQLException se) {
            System.out.println("SQL salah" + se);
        } catch (Exception e) {
            System.out.println("Pesan error " + e);
        }
    }
    
    void tampilDetailMahasiswa(String n){
        hasil = "";
        boolean data = false;
        try {
            Statement stat = connAkademik.createStatement();
            ResultSet rSet = stat.executeQuery("Select * from mahasiswa where nim="+n);
            String line = "---------------------------------------------";
            while (rSet.next()) {
                String nim = rSet.getString("nim");
                String nama = rSet.getString("nama");
                String jk = rSet.getString("jk");
                String alamat = rSet.getString("alamat");
                String email = rSet.getString("email");
                String jurusan = rSet.getString("jurusan");
                hasil = hasil + "NIM : " + nim + " <>"
                        + "Nama : " + nama + " <>"
                        + "Jenis Kelamin : " + jk + " <>"
                        + "Alamat : " + alamat + " <>"
                        + "Jurusan : " + jurusan + " <>" + line;
                data = true;
            }
            if (!data){
                hasil = "Data Tidak Ditemukan";
            }
            stat.close();
        } catch (SQLException se) {
            System.out.println("SQL salah" + se);
        } catch (Exception e) {
            System.out.println("Pesan error " + e);
        }
    }
}
