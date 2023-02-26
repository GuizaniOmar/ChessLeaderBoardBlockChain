package com.example.chessleaderboardblockchain;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DiscordMessage {

    public static String EnvoieMessage(String message) {
       final String message_provisoire = message;
         new Thread(new Runnable() {
            String message_finale = message_provisoire;
            @Override

            public void run() {
                try {
                    URL url = new URL("https://discord.com/api/webhooks/1078864077507534878/h0jTevT0U9TwcqsQkjYfSYvda0F3MI9hiXzU8YbL-VMWlupNWugZD_6qk2AXvQin57gW");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    //String payload = "{\"content\":\"RONALDO\"}";
                    String payload = "{\"content\":\""+message_finale+"\"}";
                    byte[] outputInBytes = payload.getBytes("UTF-8");
                    OutputStream os = conn.getOutputStream();
                    os.write(outputInBytes);
                    os.close();
                    int i = conn.getResponseCode();
                    String stringi = Integer.toString(i);
                    System.out.println("Response code: " + conn.getResponseCode());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return "Ronaldo";
    }
    public static void main(String[] args) {

    }
}
