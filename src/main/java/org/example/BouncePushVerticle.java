package org.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class BouncePushVerticle extends AbstractVerticle {
    private ServerWebSocket currentClient;
    @Override
    public void start(Promise<Void> p){
        HttpServer server = vertx.createHttpServer();
        server.webSocketHandler(webSocket -> {
            currentClient = webSocket;
            System.out.println("Client connected: " + webSocket.remoteAddress());
            webSocket.closeHandler(v ->
                    {
                        System.out.println("Client disconnected: " + webSocket.remoteAddress());
                        currentClient = null;
                    }
                );
        });

        Router router = Router.router(vertx);
        router.post("/sendNotification").handler(this::handleNotification);
        router.post("/closeWebSocket").handler(this::handleCloseWebSocket);
        router.route().handler(context -> {
            System.out.println("Received HTTP Request: " + context.request().path());
            context.next();
        });

        // Attach the router as a request handler
        server.requestHandler(router).listen(9091, "127.0.0.1", res -> {
            if (res.succeeded()) {
                System.out.println("HTTP Server is listening on 127.0.0.1:9091");
            } else {
                System.out.println("Failed to start HTTP Server!");
            }
        });
    }

    public void pushMessage(String message){
        if (currentClient != null) {
            currentClient.writeTextMessage(message);
        }
    }
    private void handleCloseWebSocket(RoutingContext context) {
        if (currentClient != null) {
            currentClient.close();
            currentClient = null;
            context.response().setStatusCode(200).end("WebSocket closed");
        } else {
            context.response().setStatusCode(400).end("No WebSocket connection to close");
        }
    }
    private void handleNotification(RoutingContext context) {
        try {
                // Alert the client to open TCP connection
                pushMessage("OpenTcpConnection");

                context.response()
                        .setStatusCode(200)
                        .end("Notification queued for TCP");

        } catch (Exception e) {
            e.printStackTrace();
            context.response()
                    .setStatusCode(500) // Internal Server Error
                    .end("Internal Server Error");
        }
    }
}
