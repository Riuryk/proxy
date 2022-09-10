/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import static proxy.NewJFrame.enviar;

/**
 *
 * @author Rodrigo
 */
public class Proxy {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int puerto =5555;
        String ip="localhost";
        boolean admin = false;
        for (int i = 0; i < args.length; i++) {
            if (args[i].contains("-adm")) {
                admin = true;
            }
            if (args[i].contains("-p")) {
                puerto = Integer.parseInt(args[i+1]);
            }
            if (args[i].contains("-ip")) {
                ip =(args[i+1]);
            }
        }
        if (admin) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new NewJFrame().setVisible(true);
                    try {
                   enviar("");
                } catch (Exception e) {
                }
                }
            });
            
            NewJFrame jf =new NewJFrame(ip);
        } else {
            Servidor serv = new Servidor(puerto);
            System.out.println("Servidor proxy iniciado en el puerto "+puerto);
            Thread h1 = new Thread(serv);
            h1.start();
              
            ServerSocket ss = null;
                Socket s = null;
                try {
                    ss = new ServerSocket(6666);
                } catch (IOException ex) {
                    System.out.println("Eror iniciando cliente");
                }
            while (true) {
              

                try {
                    s = ss.accept();
                    System.out.println("Conexion desde: " + s.getRemoteSocketAddress());
                } catch (Exception e) {
                }

                
                cliente c = new cliente(s);

                
                Thread h2 = new Thread(c);
                
                h2.start();
            }
        }

    }

}
