package org.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import org.apache.commons.lang3.tuple.Pair;


public class BounceTCPVerticle extends AbstractVerticle {

    private NetSocket tcpSocket = null;

    @Override
    public void start(Promise<Void> promise){
        NetServer server = vertx.createNetServer();

        server.connectHandler(socket -> {
            this.tcpSocket = socket;
            sendPendingMessages();
            socket.handler(buffer -> {
                System.out.println("Recieved tcp data: " + buffer.toString());
            });

//            socket.close(); // Close the TCP connection
        });

        server.listen(8081, "127.0.0.1",res -> {
            if (res.succeeded()) {
                System.out.println("TCP Server is listening on 127.0.0.1:8081");
            } else {
                System.out.println("Failed to start TCP Server!");
            }
        });

    }

    private void sendPendingMessages() {
//        System.out.println("in send pending message");
//        System.out.println("messageQueue status:  " + MessageQueue.toPush());
//        System.out.println("socket status: " + tcpSocket != null);
        while (MessageQueue.toPush() && tcpSocket != null) {
            Pair<String, String> message = MessageQueue.pushMessage();

            String dataToSend = message.getKey() + " " + message.getValue();
//            System.out.println("dataTosend: " + dataToSend);
            tcpSocket.write(dataToSend + "\n");

        }
    }
}
