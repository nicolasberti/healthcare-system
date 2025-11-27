package com.healthcare.notification_service.config;

import com.healthcare.grpc.MemberServiceGrpc;
import io.grpc.Channel;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {

    @Bean
    @GrpcClient("memberService")
    public MemberServiceGrpc.MemberServiceBlockingStub memberServiceStub(Channel channel) {
        return MemberServiceGrpc.newBlockingStub(channel);
    }

}
