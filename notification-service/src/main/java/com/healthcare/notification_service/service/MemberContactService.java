package com.healthcare.notification_service.service;

import com.healthcare.grpc.MemberRequest;
import com.healthcare.grpc.MemberContactResponse;
import com.healthcare.grpc.MemberServiceGrpc;
import com.healthcare.notification_service.model.MemberContact;
import com.healthcare.notification_service.model.event.Event;
import com.healthcare.notification_service.model.event.MemberCreated;
import com.healthcare.notification_service.repository.MemberContactRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class MemberContactService {

    private static final Logger logger = LoggerFactory.getLogger(MemberContactService.class);

    private final MemberContactRepository memberContactRepository;
    private final String memberServiceAddress;
    private ManagedChannel channel;
    private MemberServiceGrpc.MemberServiceBlockingStub memberStub;

    public MemberContactService(
            MemberContactRepository memberContactRepository,
            @Value("${grpc.client.memberService.address}")
            String memberServiceAddress
    ) {
        this.memberContactRepository = memberContactRepository;
        this.memberServiceAddress = memberServiceAddress;
    }

    @PostConstruct
    public void init() {
        try {
            String target = memberServiceAddress.replace("static://", "");
            this.channel = ManagedChannelBuilder
                    .forTarget(target)
                    .usePlaintext()
                    .build();
            this.memberStub = MemberServiceGrpc.newBlockingStub(channel);
            logger.info("gRPC channel initialized for {}", target);
        } catch (Exception e) {
            logger.error("Error initializing gRPC channel", e);
            throw new RuntimeException("Failed to initialize gRPC channel", e);
        }
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            try {
                channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
                logger.info("gRPC channel shut down");
            } catch (InterruptedException e) {
                logger.warn("gRPC channel shutdown interrupted", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    public MemberContact save(Event<MemberCreated> event) {
        MemberContact memberContact = MemberContact.builder()
                .id(event.getData().getId())
                .email(event.getData().getEmail())
                .phone(event.getData().getPhone())
                .build();
        return memberContactRepository.save(memberContact);
    }

    public MemberContact getById(String id) {
        // Primero intenta obtener del repositorio local
        Optional<MemberContact> memberContact = memberContactRepository.findById(id);
        if (memberContact.isPresent()) {
            return memberContact.get();
        }

        // Si no existe localmente, lo obtiene del servicio remoto
        MemberContactResponse memberContactResponse = getMemberContactById(id);
        MemberContact memberContactNew = MemberContact.builder()
                .id(memberContactResponse.getId())
                .email(memberContactResponse.getEmail())
                .phone(memberContactResponse.getPhone())
                .build();

        // Guarda en otro hilo para no bloquear (opcional - descomenta si quieres)
        // saveAsyncIfNeeded(memberContactNew);

        return memberContactRepository.save(memberContactNew);
    }

    /**
     * Llamada a gRPC con CircuitBreaker.
     * El CircuitBreaker capturará StatusRuntimeException y excepciones generales.
     */
    @CircuitBreaker(
            name = "memberService",
            fallbackMethod = "fallbackGetMemberContactById"
    )
    public MemberContactResponse getMemberContactById(String id) {
        try {
            MemberRequest request = MemberRequest.newBuilder().setId(id).build();
            return memberStub.getMemberContactById(request);
        } catch (StatusRuntimeException e) {
            logger.error("gRPC call failed with status: {}", e.getStatus(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during gRPC call", e);
            throw new RuntimeException("gRPC call failed", e);
        }
    }

    /**
     * Fallback que se ejecuta cuando el CircuitBreaker está abierto o hay error.
     */
    public MemberContactResponse fallbackGetMemberContactById(String id, Exception exception) {
        logger.warn("CircuitBreaker FALLBACK activated for getMemberContactById({}). Reason: {}",
                id, exception.getMessage());

        // Intenta obtener del repositorio local como último recurso
        Optional<MemberContact> cached = memberContactRepository.findById(id);
        if (cached.isPresent()) {
            MemberContact mc = cached.get();
            return MemberContactResponse.newBuilder()
                    .setId(mc.getId())
                    .setEmail(mc.getEmail())
                    .setPhone(mc.getPhone())
                    .build();
        }

        // Si no está en caché, devuelve valores por defecto
        return MemberContactResponse.newBuilder()
                .setId(id)
                .setEmail("unknown@example.com")
                .setPhone("N/A")
                .build();
    }
}