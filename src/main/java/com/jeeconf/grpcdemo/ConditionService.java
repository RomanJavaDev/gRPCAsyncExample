package com.jeeconf.grpcdemo;

import com.jeeconf.grpcdemo.ConditionServiceGrpc.ConditionServiceImplBase;
import io.grpc.stub.StreamObserver;

import static com.jeeconf.grpcdemo.Temperature.Units.CELSUIS;

/**
 * Returns hard-coded condition response.
 */
public class ConditionService extends ConditionServiceImplBase {

    @Override
    public void getCurrent(ConditionRequest request, StreamObserver<ConditionResponse> responseObserver) {
        ConditionResponse response = ConditionResponse.newBuilder()
                .setTemperature(Temperature.newBuilder().setUnits(CELSUIS).setDegrees(20.f))
                .setPressure(Pressure.newBuilder().setValue(.65f))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
