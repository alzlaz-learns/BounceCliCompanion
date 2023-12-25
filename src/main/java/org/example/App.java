package org.example;

import io.vertx.core.Vertx;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new BounceTCPVerticle(), res -> {
            if (res.succeeded()) {
                System.out.println("BounceTCPServer deployed successfully!");
            } else {
                System.err.println("Failed to deploy BounceTCPServer: " + res.cause());
            }
        });

        vertx.deployVerticle(new BouncePushVerticle(), res -> {
            if (res.succeeded()) {
                System.out.println("BouncePushVerticle deployed successfully!");
            } else {
                System.err.println("Failed to deploy BouncePushVerticle: " + res.cause());
            }
        });


        ConsoleInputReader cir = new ConsoleInputReader();

        final Thread[] cliThread = new Thread[1];

        cliThread[0] = new Thread(() -> {
            CLIVersion cli = new CLIVersion(cir, vertx, cliThread[0]);
            cli.readCommands();
        });
        cliThread[0].start();
    }
}
