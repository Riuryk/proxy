/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proxy;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rodrigo
 */

class Servidor implements Runnable {

    int contador = 0;
    Socket s = null;
    boolean HTTP = true;
    boolean GUARDAR = false;
    boolean POST = false;
    Scanner sc = null;
    String totlinea = "";
    String[] pget = null;
    String[] host = null;
    String[] total = null;
    String[] ext = null;
    String linea = null;
    String contenido = "";
    ServerSocket ss = null;
    String cmd = "";
    String Blist = "";
    String Type = "_____";
    String[] MimeType;
    String ruta = null;
    String archivo = "";
    File f;
    RandomAccessFile fw = null;
    int PUERTO = 5555;
public Servidor(int puerto) {
        this.PUERTO = PUERTO;
    }
    public void run() {

        //Se inicia la conexion
        try {
            ss = new ServerSocket(PUERTO);
        } catch (IOException ex) {
        }

        DataInputStream in;
        DataOutputStream out;
        while (true) {
            adm(cmd);

            try {

                s = ss.accept();
                sc = new Scanner(s.getInputStream());
                linea = sc.nextLine().replace("HTTP/1.1", "HTTP/1.0");
            } catch (Exception e) {

                HTTP = true;
            }
            //se reciben los datos del navegador
            while (!linea.equals("")) {

                if (linea.contains("GET")) {
                    pget = linea.split(" ");
                }
                if (linea.contains("POST")) {
                    pget = linea.split(" ");
                    POST = true;                   
                }
                if (linea.contains("Host")) {
                    host = linea.split(" ");
                }
                totlinea += linea + "\n";
                linea = sc.nextLine();
            }
            //se excluyen las peticiones HTTPS
            try {
                for (int i = 0; i < pget.length; i++) {
                    if (pget[i].contains("443")) {
                        HTTP = true;
                    }
                }
                archivo = pget[1].replace("\\", "").replace(".", "").replace("=", "").replace("?", "");
            } catch (NullPointerException ex) {

            }
            //se separa el pedido HTTP por lineas
            total = totlinea.split("\n");

            if (HTTP == false && !Blist.contains(host[1])) {

                URL url = null;
                try {
                    url = new URL(pget[1]);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }

                HttpURLConnection cl = null;
             
                try {
                    cl = (HttpURLConnection) url.openConnection();
                } catch (IOException ex) {
                    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
   if (POST) {
                    try {
                        cl.setRequestMethod("POST");
                    } catch (Exception ex) {
                    }
                }
                String headers = cl.getHeaderFields().toString().replace(",", "\n");
                try {
                    for (int i = 0; i < MimeType.length; i++) {
                        if (headers.contains(MimeType[i])) {
                            ext = cl.getContentType().split("/");
                            GUARDAR = true;
                            if (MimeType[i].contains("image/svg+xml")) {
                                ext[1] = "svg";
                            }

                        }
                    }
                    for (int i = 0; i < MimeType.length; i++) {
                        if (MimeType[i].contains("image/svg+xml")) {
                            ext[1] = "svg";
                        }
                    }
                } catch (NullPointerException e) {

                }
                //Se reciben los datos en bytes y se escriben en el navegador

                try {
                    out = new DataOutputStream(s.getOutputStream());
                    in = new DataInputStream(cl.getInputStream());
                    int count;
                    byte[] buffer = new byte[8192];

                    String cont = String.valueOf(contador);

                    if (GUARDAR) {

                        f = new File(ruta, host[1] + cont + "." + ext[1]);
                        fw = new RandomAccessFile(f, "rw");
                    }
                    while ((count = in.read(buffer)) > 0) {
                        if (GUARDAR) {
                            fw.write(buffer, 0, count);
                            fw.skipBytes(count);
                        }
                        out.write(buffer, 0, count);
                    }
                    if (GUARDAR) {
                        fw.close();
                    }
                    out.close();
                    in.close();
                } catch (IOException ex) {
                }

            }
            //Si una pagina se bloquea se agrega a la blacklist
            if (Blist.contains(host[1])) {
                PrintWriter pw = null;
                try {
                    pw = new PrintWriter(s.getOutputStream());
                } catch (IOException ex) {
                    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
                pw.write("HTTP/1.0 200 OK");
                pw.write("\n");
                pw.write("\n");
                pw.write("<html>sitio bloqueado<html>");
                pw.flush();
            }
            GUARDAR = false;
            totlinea = "";
            total = null;
            try {
                s.close();
            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                //se cierra la conexion
                s.close();
            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
            POST = false;
            HTTP = false;
            contador++;
        }

    }

    public void adm(String comando) {
        String[] aux;
        String des = "";
        if (!cliente.getStr().equals(comando)) {
            comando = cliente.getStr();
            if (comando.contains("bloq_")) {
                aux = comando.split("_");
                if (!Blist.contains(aux[1])) {
                    Blist += aux[1];
                }
            }
            if (comando.contains("desbl_")) {
                aux = comando.split("_");
                des = Blist.replace(aux[1], "");
                Blist = des;
            }
            if (comando.contains("type_")) {
                aux = comando.split("_");
                Type += aux[1] + "\n";
                MimeType = Type.split("\n");

            }
            if (comando.contains("rut_")) {
                aux = comando.split("_");
                ruta = aux[1];
            }
            if (comando.contains("reset")) {
                ruta = null;
            }
            if (comando.contains("restyp")) {
                Type = null;
                MimeType = null;
            }
        }
    }
}
