package com.healthcare.document_service.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;
import java.util.Optional;

@Configuration
@EnableConfigurationProperties(AwsS3Properties.class)
public class AwsS3Config {

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider(AwsS3Properties props) {

        if (props.accessKey() != null && props.secretKey() != null) {
            // 👉 LocalStack / dev
            return StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                            props.accessKey(),
                            props.secretKey()
                    )
            );
        }

        // 👉 Producción (IAM Role, EC2, ECS, EKS, etc.)
        return DefaultCredentialsProvider.create();
    }

    @Bean
    public S3Client s3Client(
            AwsS3Properties props,
            AwsCredentialsProvider credentialsProvider
    ) {

        S3ClientBuilder builder = S3Client.builder()
                .region(Region.of(props.region()))
                .credentialsProvider(credentialsProvider);

        Optional.ofNullable(props.endpoint()).ifPresent(endpoint ->
                builder.endpointOverride(URI.create(endpoint))
                        .serviceConfiguration(
                                S3Configuration.builder()
                                        .pathStyleAccessEnabled(true) // REQUIRED for LocalStack
                                        .build()
                        )
        );

        return builder.build();
    }

    @Bean
    public S3Presigner s3Presigner(AwsS3Properties props) {
        var builder = S3Presigner.builder()
                .region(Region.of(props.region()));

        if (props.accessKey() != null && props.secretKey() != null) {
            builder.credentialsProvider(
                    StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(
                                    props.accessKey(),
                                    props.secretKey()
                            )
                    )
            );
        } else {
            builder.credentialsProvider(DefaultCredentialsProvider.create());
        }

        Optional.ofNullable(props.endpoint()).ifPresent(endpoint ->
                builder.endpointOverride(URI.create(endpoint))
        );

        return builder.build();
    }
}
