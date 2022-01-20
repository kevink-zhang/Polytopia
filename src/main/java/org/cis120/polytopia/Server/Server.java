package org.cis120.polytopia.Server;

import java.io.*;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.net.*;

import static org.cis120.polytopia.Server.Server.playerinfo;

// Server class
public class Server extends Thread {

    // Vector to store active clients
    static List<ClientHandler> ar = new LinkedList<>();
    public boolean acceptnew = true;
    public static List<List<String>> playerinfo = new LinkedList<>();

    // counter for clients
    int i = 0;

    // port to listen on
    int portnumber;
    ServerSocketChannel ssc;

    public Server() {
        portnumber = 6666;
    }

    public Server(int pnum) {
        portnumber = pnum;

        try {
            ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ssc.socket().bind(new InetSocketAddress(portnumber));
            System.out.println("Server listening on: " + getIP() + ", port: " + portnumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            for(ClientHandler c: ar){
                c.open = false;
            }
            ssc.socket().close();
            ssc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tick() {
        if (!acceptnew) {
            return; // no new players allowed
        }

        // server is listening on port pnum
        try {
            SocketChannel ss;
            Socket s;

            // running infinite loop for getting
            // client request
            Set<InetAddress> iplist = new HashSet<>();

            ss = ssc.accept();
            if (ss != null) {
                s = ss.socket();
                if (true) {// !iplist.contains(s.getInetAddress())) { TO CHANGE
                    iplist.add(s.getInetAddress());

                    System.out.println("New client request received : " + s);

                    // obtain input and output streams
                    DataInputStream dis = new DataInputStream(s.getInputStream());
                    DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                    System.out.println("Creating a new handler for this client...");

                    // Create a new handler object for handling this request.
                    ClientHandler mtch = new ClientHandler(s, dis, dos);

                    // Create a new Thread with this object.
                    Thread t = new Thread(mtch);

                    System.out.println("Adding this client to active client list");

                    // add this client to active clients list
                    ar.add(mtch);

                    // start the thread.
                    t.start();

                    // increment i for new client.
                    // i is used for naming only, and can be replaced
                    // by any naming scheme
                    i++;
                } else {
                    s.close();
                    ss.close();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getIP() {
        String ip = "localhost";
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // filters out 127.0.0.1 and inactive interfaces
                if (iface.isLoopback() || !iface.isUp())
                    continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    ip = addr.getHostAddress();
                    // System.out.println(iface.getDisplayName() + " " + ip);
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        return ip;
    }

}

// ClientHandler class
class ClientHandler implements Runnable {
    final DataInputStream dis;
    final DataOutputStream dos;
    List<String> info = null;
    Socket s;
    boolean open = true;

    // constructor
    public ClientHandler(
            Socket s,
            DataInputStream dis, DataOutputStream dos
    ) {
        this.dis = dis;
        this.dos = dos;
        this.s = s;

        try {
            ServerPacket sp = new ServerPacket("confirm", null);
            this.dos.writeUTF(sp.toJSON());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        String received;
        while (open) {
            try {
                // receive the string
                received = dis.readUTF();
                System.out.println("Received: " + received);
                ServerPacket sp = new ServerPacket().fromJSON(received);

                if (this.info == null && sp.type.equals("user info")) {
                    playerinfo.add((List<String>) sp.information);
                    this.info = (List<String>) sp.information;
                }
                if (sp.type.equals("end game")) {
                    // sends a message to all other
                    playerinfo = new LinkedList<>();
                }
                if (sp.type.equals("log out")) {
                    // sends a message to all other
                    for (ClientHandler mc : Server.ar) {
                        if (mc != this) {
                            mc.dos.writeUTF(received);
                        }
                    }
                    for (List<String> l : playerinfo) {
                        if (l.contains(info.get(0))) {
                            playerinfo.remove(l);
                            break;
                        }
                    }
                    this.s.close();

                    break;
                }

                // break the string into message and recipient part
                String st = received;

                // search for the recipient in the connected devices list.
                // ar is the vector storing client of active users
                for (ClientHandler mc : Server.ar) {
                    // sends a message to all others
                    if (mc != this) {
                        mc.dos.writeUTF(received);
                    }
                }
            } catch (IOException e) {
                try {
                    this.s.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            }

        }
        try {
            // closing resources
            this.dis.close();
            this.dos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
