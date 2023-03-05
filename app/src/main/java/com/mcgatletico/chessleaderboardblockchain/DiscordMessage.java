package com.mcgatletico.chessleaderboardblockchain;

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
                 // Pour les personnes qui scannent les github à la recherche d'un Token disc.or.d
                    URL url = new URL(CryptageClef.decrypt("M1a8q44V6i+RmaPvcQgcAWDXTFuHl5D6M2YwS5ly6TxgqADD2lrqNRBt9gkAS98vITM098B+ToDq7Z4Ve+boKn70Z7pVo5qjE/Y3rSl37h4qQ/9WOLGhcRCe2LDLeUFIDaXNuvO1EBE+cjKqiTFK+cMGqu9+3WshE3pfAw0T+Lg=","123"));
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
