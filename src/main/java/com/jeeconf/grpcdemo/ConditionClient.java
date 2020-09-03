package com.jeeconf.grpcdemo;

import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.Semaphore;

/**
 * Subscribes to condition service with blocking and async stubs and prints its responses to the "standard" output stream.
 */
public class ConditionClient {

    public static void main(String[] args) throws InterruptedException {

        ManagedChannel grpcChannel = NettyChannelBuilder.forAddress("localhost", 8090).usePlaintext(true).build();


        ConditionServiceGrpc.ConditionServiceStub asyncClient = ConditionServiceGrpc.newStub(grpcChannel);

        ConditionRequest request = ConditionRequest.newBuilder()
                .setAltitude(Altitude.newBuilder().setHeightAboveSea(UserAltitude.heightAboveSea))
                        .build();


        Semaphore exitSemaphore = new Semaphore(0);
        asyncClient.getCurrent(request, new StreamObserver<ConditionResponse>() {

            @Override
            public void onNext(ConditionResponse response) {
                System.out.printf("Async client. Current condition for %s: %s.%n", request, response);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                System.err.printf("Async client. Cannot get condition for %s.%n", request);
                exitSemaphore.release();
            }

            @Override
            public void onCompleted() {
                System.out.printf("Async client. Stream completed.%n");
                exitSemaphore.release();
            }
        });

        exitSemaphore.acquire();

    }
}
