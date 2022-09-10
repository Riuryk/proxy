/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proxy;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rodrigo
 */
public class cliente implements Runnable {

    static public String str = "";
    static public String types = "";
Socket s;

    public cliente(Socket s) {
        this.s = s;
    }

    private cliente() {
    }
    @Override
    public void run() {
      
        DataInputStream in = null;
        DataOutputStream out = null;

        
       
          
            try {
                in= new DataInputStream(s.getInputStream());
            out =new DataOutputStream(s.getOutputStream());
            } catch (Exception e) {

            }
            
            try {
                out.writeUTF(types);
            } catch (IOException ex) {
                Logger.getLogger(cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                this.str = in.readUTF();
            } catch (IOException ex) {
                System.out.println("No se pudo leer");
            }
            types += str + ",";
            if ("restyp".equals(str)) {
                types = "";
            }
            try {
                s.close();
            } catch (IOException ex) {
                Logger.getLogger(cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        
    }

    static public String getStr() {
        return str;
    }

}
