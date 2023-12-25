package org.example;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class CLIVersion {


    private InputReader ip;
    private Vertx vertx;
    private Thread cliThread;

    public CLIVersion(InputReader ip, Vertx vertx, Thread cliThread) {
        this.ip = ip;
        this.vertx = vertx;
        this.cliThread = cliThread;

    }

    public void readCommands(){
        while(true){
            System.out.println("> < send | quit >");
            String command = ip.readInput();
            String[] split = command.split(" ");

            if(split[0].compareTo("quit") == 0){
                System.out.println("shutting down");
                closeResources();
                ip.close();
                break;
            }

            if ("send".equals(split[0]) && split.length >= 3) {
                String phoneNumber = split[1];
                String message = split[2];
                MessageQueue.queueMessage(phoneNumber, message);
                sendMessageToServer();
            } else {
                System.out.println("Invalid command or insufficient arguments");
            }
        }
    }

    private static void sendMessageToServer() {
        try {

            URL url = new URL("http://127.0.0.1:9091/sendNotification");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            int responseCode = conn.getResponseCode();
//            System.out.println("Response Code: " + responseCode);
            String responseMessage = conn.getResponseMessage();
//            System.out.println("Response Message: " + responseMessage);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void closeResources() {
        closeWebSocketConnection();
        ip.close();

        // Close Vert.x
        vertx.close(handler -> {
            if (handler.succeeded()) {
                System.out.println("Vert.x closed successfully.");
            } else {
                System.err.println("Failed to close Vert.x: " + handler.cause());
            }
        });

        // Interrupt the CLI thread
        cliThread.interrupt();
    }
    private static void closeWebSocketConnection(){
            try{
                URL url = new URL("http://127.0.0.1:9091/closeWebSocket");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                int responseCode = conn.getResponseCode();
                System.out.println("Response Code: " + responseCode + " " );

                String responseMessage = conn.getResponseMessage();
                System.out.println("Response Message: " + responseMessage);
            }catch (Exception e) {
                e.printStackTrace();
            }
    }
}
