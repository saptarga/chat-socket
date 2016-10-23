/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tugas2v2;

import java.util.StringTokenizer;
import javax.swing.JOptionPane;

/**
 *
 * @author saptarga
 */
class readFromServer extends Thread {

    client c;

    readFromServer(client cc) {
        c = cc;
    }

    @Override
    public void run() {
        String s;
        String l;
        String r;
        while (true) {
            if (client.logout) {
                return;
            }
            s = client.read();
            if (s.startsWith("List")) {
                c.setTitle("Simple Java Chat - " + client.nick + " -Connected to " + client.server);
                c.LblUser.setText("User : "+client.nick);
                c.jMHelp.setEnabled(true);
                client.connected = true;
                client.list.clear();
                String nextNick = "";
                StringTokenizer st = new StringTokenizer(s.substring(5, s.length()), ", ");
                String temp = null;
                while (st.hasMoreTokens()) {
                    temp = st.nextToken();
                    l = replace(temp, "<", " ");
                    client.list.addElement(replace(l, ";", ""));
                }
                System.out.print("List updated: New names: ");
                for (int i = 0; i < client.list.size(); i++) {
                    System.out.print(client.list.get(i) + " ");
                }
                System.out.println();
            } else if (s.startsWith("Recieve")) {
                r = replace(s,"<"," ");
                c.mainText.setText(c.mainText.getText()
                        + "\n" + r.substring(8, r.length()));
                c.mainText.setCaretPosition(c.mainText.getText().length());
            } else if (s.startsWith("PrivateRecieve")) {
                 r = replace(s,"<"," ");
                c.mainText.setText(c.mainText.getText()
                        + "\n" + "Privat Messages: " + r.substring(14, r.length()));
                c.mainText.setCaretPosition(c.mainText.getText().length());
            } else if (s.startsWith("NewNick")) {
                c.mainText.setText("");
                String newnick = JOptionPane.showInputDialog(null, "New nick:");
                client.connected = false;
                c.jMenuItem1.setEnabled(true);
                c.jMenuItem2.setEnabled(false);
                if (newnick != null) {
                    client.nick = newnick;
                    c.jMenuItem1.setEnabled(false);
                    c.jMenuItem2.setEnabled(true);
                    c.send("Login: " + newnick);
                }
            }else if(s.startsWith("Request")) {
                String tampil = s.substring(8, s.length());
                r = replace(tampil,"<>","\n");
                c.mainText.setText(c.mainText.getText()
                        + "\n" + r);
                c.mainText.setCaretPosition(c.mainText.getText().length());
            }
            System.out.println(s);
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
}
