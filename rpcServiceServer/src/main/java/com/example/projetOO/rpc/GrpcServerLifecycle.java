package com.example.projetOO.rpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GrpcServerLifecycle implements SmartLifecycle {
    private final SeriesTrackerGrpcService service;
    private Server server;
    private Thread awaitThread;
    private volatile boolean running;

    public GrpcServerLifecycle(SeriesTrackerGrpcService service) {
        this.service = service;
    }

    @Override
    public synchronized void start() {
        if (running) return;
        try {
            server = ServerBuilder.forPort(9090).addService(service).build().start();
            awaitThread = new Thread(() -> {
                try {
                    server.awaitTermination();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                }
            }, "grpc-server-await");
            awaitThread.setDaemon(false);
            awaitThread.start();
            running = true;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to start gRPC server on port 9090", e);
        }
    }

    @Override
    public synchronized void stop() {
        if (server != null) {
            server.shutdown();
            running = false;
        }
    }

    @Override public boolean isRunning() { return running; }
    @Override public boolean isAutoStartup() { return true; }
    @Override public int getPhase() { return Integer.MAX_VALUE; }
}
