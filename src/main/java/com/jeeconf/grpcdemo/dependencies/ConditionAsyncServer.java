package com.jeeconf.grpcdemo.dependencies;

import com.jeeconf.grpcdemo.dependencies.PressureServiceGrpc.PressureServiceFutureStub;
import com.jeeconf.grpcdemo.dependencies.TemperatureServiceGrpc.TemperatureServiceFutureStub;

import io.grpc.Server;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.NettyServerBuilder;

import java.io.IOException;

/**
 * Starts condition, temperature, pressure servers.
 */
public class ConditionAsyncServer {

    public static void main(String[] args) throws IOException, InterruptedException {

        String host = "localhost";
        int temperaturePort = 8081;
        int pressurePort = 8082;

        Server temperatureServer = NettyServerBuilder.forPort(temperaturePort)
                .addService(new TemperatureService()).build().start();
        Server pressureServer = NettyServerBuilder.forPort(pressurePort)
                .addService(new PressureService()).build().start();

        TemperatureServiceFutureStub temperatureStub =
                TemperatureServiceGrpc.newFutureStub(NettyChannelBuilder.forAddress(host, temperaturePort).usePlaintext(true).build());
        PressureServiceFutureStub pressureStub =
                PressureServiceGrpc.newFutureStub(NettyChannelBuilder.forAddress(host, pressurePort).usePlaintext(true).build());


        ConditionAsyncService conditionService = new ConditionAsyncService(temperatureStub, pressureStub);
        Server conditionServer = NettyServerBuilder.forPort(8090).addService(conditionService).build().start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            conditionServer.shutdownNow();
            temperatureServer.shutdownNow();
            pressureServer.shutdownNow();
        }));

        conditionServer.awaitTermination();
    }
}
