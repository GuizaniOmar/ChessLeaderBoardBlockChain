package com.mcgatletico.chessleaderboardblockchain;

import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class PingIP implements Runnable {

    private String ip;
    private List<String> ipList;

    public PingIP(String ip, List<String> ipList) {
        this.ip = ip;
        this.ipList = ipList;
    }

    @Override
    public void run() {
        try {
            InetAddress address = InetAddress.getByName(ip);
            if (address.isReachable(5000)) {
                try (Socket socket = new Socket(ip, CONS.PORT)) {
                    if (socket.isConnected()) {
                        System.out.println(ip + " : " + "port 52000 is open");
                        synchronized (ipList) {
                            ipList.add(ip);
                            MultiClientSocket multiClientSocket = MultiClientSocket.getInstance();
                            multiClientSocket.add(ip, CONS.PORT);
                        }
                    }
                } catch (IOException e) {
                    // port 52000 is not open
                }
            }
        } catch (IOException e) {
            // IP is not reachable
        }
    }


    public static void main() throws InterruptedException {
        String network = ThreadClient.IP + ".";
        List<String> ipList = new ArrayList<String>();

        for (int i = 1; i <= 255; i++) {
            String ip = network + i;
            if (ip.equals(ThreadClient.monIP)) {
                continue;
            }
            PingIP pingIP = new PingIP(ip, ipList);
            Thread thread = new Thread(pingIP);
            thread.start();
        }

        Thread.sleep(5000); // Wait for all threads to complete

        System.out.println("IPs with port " + CONS.PORT + " open:");
        for (String ip : ipList) {
            System.out.println(ip);
        }
    }
}

