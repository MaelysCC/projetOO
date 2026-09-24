package com.example.projetOO.rpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.example.projetOO.rpc.SeriesTrackerServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.DisposableBean;

@Configuration
public class GrpcClientConfiguration {
    @Bean
    ManagedChannel trackerChannel() {
        return ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
    }

    @Bean
    SeriesTrackerServiceGrpc.SeriesTrackerServiceBlockingStub trackerStub(ManagedChannel trackerChannel) {
        return SeriesTrackerServiceGrpc.newBlockingStub(trackerChannel);
    }

    @Bean
    DisposableBean trackerChannelShutdown(ManagedChannel trackerChannel) {
        return trackerChannel::shutdown;
    }
}
