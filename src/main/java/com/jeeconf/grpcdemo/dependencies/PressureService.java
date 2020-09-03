package com.jeeconf.grpcdemo.dependencies;

import com.jeeconf.grpcdemo.Altitude;
import com.jeeconf.grpcdemo.Pressure;
import com.jeeconf.grpcdemo.dependencies.PressureServiceGrpc.PressureServiceImplBase;
import com.jeeconf.grpcdemo.providers.RandomPressureProvider;
import io.grpc.stub.StreamObserver;

import java.util.function.Supplier;

/**
 * Replies with randomly generated {@link Pressure}.
 */
public class PressureService extends PressureServiceImplBase {

    private final Supplier<Pressure> pressureProvider = new RandomPressureProvider();

    @Override
    public void getCurrent(Altitude request, StreamObserver<Pressure> responseObserver) {
        responseObserver.onNext(pressureProvider.get());
        responseObserver.onCompleted();
    }

}
