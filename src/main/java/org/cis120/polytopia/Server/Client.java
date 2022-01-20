package org.cis120.polytopia.Server;// Java implementation for multithreaded chat client

// Save file as Client.java

import java.io.*;
import java.net.*;

import static org.cis120.polytopia.GameBody.JOINCODE;
import static org.cis120.polytopia.GameBody.incorrectjoin;
import static org.cis120.polytopia.GameBody.PARSE_SERVER;

public class Client extends Thread {
    int ServerPort = 6666;
    InetAddress ip = null;
    private String tosend = null;

    Socket s;
    DataInputStream dis;
    DataOutputStream dos;

    public Client(String inputip) throws UnknownHostException {
        // getting localhost ip
        ip = InetAddress.getByName(inputip);

    }

    @Override
    public void run() {
        // establish the connection

        try {
            s = new Socket();
            s.connect(new InetSocketAddress(ip, ServerPort));
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            System.out.println("Client connected to: " + ip);

            // // sendMessage thread
            // Thread sendMessage = new Thread(new Runnable()
            // {
            // @Override
            // public void run() {
            // while (true) {
            // // read the message to deliver.
            // if(tosend!=null) {
            // try {
            // // write on the output stream
            // System.out.println(tosend);
            //
            // } catch (IOException e) {
            // e.printStackTrace();
            // }
            // //tosend = null;
            // }
            // }
            // }
            // });

            // readMessage thread
            Thread readMessage = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            String msg = dis.readUTF();
                            PARSE_SERVER(new ServerPacket().fromJSON(msg));

                        } catch (IOException e) {
                            // e.printStackTrace();
                            // break;
                        }
                    }
                    // close();
                }

            });

            // sendMessage.start();
            readMessage.start();

        } catch (IOException e) {
            incorrectjoin = JOINCODE;
            close();
        }
    }

    public void close() {
        try {
            s.close();
            dis.close();
            dos.close();
            this.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(ServerPacket sp) {
        this.tosend = sp.toJSON();
        try {
            dos.writeUTF(tosend);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // System.out.println(tosend);
    }
}
