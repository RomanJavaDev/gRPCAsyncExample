package com.jeeconf.grpcdemo.dependencies;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.jeeconf.grpcdemo.*;
import com.jeeconf.grpcdemo.ConditionServiceGrpc.ConditionServiceImplBase;
import com.jeeconf.grpcdemo.dependencies.PressureServiceGrpc.PressureServiceFutureStub;
import com.jeeconf.grpcdemo.dependencies.TemperatureServiceGrpc.TemperatureServiceFutureStub;

import io.grpc.stub.StreamObserver;

import java.util.List;

/**
 * Sends async requests to temperature, pressure services,
 * combines their responses and sends it back to a client.
 */
public class ConditionAsyncService extends ConditionServiceImplBase {

    private final TemperatureServiceFutureStub tempService;
    private final PressureServiceFutureStub pressureService;

    ConditionAsyncService(TemperatureServiceFutureStub tempService,
                          PressureServiceFutureStub pressureService) {
        this.tempService = tempService;
        this.pressureService = pressureService;

    }

    @Override
    public void getCurrent(ConditionRequest request, StreamObserver<ConditionResponse> responseObserver) {

        Altitude altitude = request.getAltitude();

        ListenableFuture<List<ConditionResponse>> responsesFuture = Futures.allAsList(
                Futures.transform(pressureService.getCurrent(altitude),
                        (Pressure pressure) -> ConditionResponse.newBuilder().setPressure(pressure).build()),
                Futures.transform(tempService.getCurrent(altitude),
                        (Temperature temp) -> ConditionResponse.newBuilder().setTemperature(temp).build()));

        Futures.addCallback(responsesFuture, new FutureCallback<List<ConditionResponse>>() {
            @Override
            public void onSuccess(List<ConditionResponse> results) {
                ConditionResponse.Builder response = ConditionResponse.newBuilder();
                results.forEach(response::mergeFrom);
                responseObserver.onNext(response.build());
                responseObserver.onCompleted();
            }
            @Override
            public void onFailure(Throwable t) { responseObserver.onError(t); }
        });

    }
}
