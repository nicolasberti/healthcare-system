package com.healthcare.notification_service.service;

import com.healthcare.grpc.MemberRequest;
import com.healthcare.grpc.MemberContactResponse;
import com.healthcare.grpc.MemberServiceGrpc;
import com.healthcare.notification_service.model.MemberContact;
import com.healthcare.notification_service.model.event.Event;
import com.healthcare.notification_service.model.event.MemberCreated;
import com.healthcare.notification_service.repository.MemberContactRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class MemberContactService {
    private static final Logger log = LoggerFactory.getLogger(MemberContactService.class);

    private final MemberContactRepository memberContactRepository;
    private final MemberServiceGrpc.MemberServiceBlockingStub memberStub;

    public MemberContactService(
            MemberContactRepository memberContactRepository,
            @GrpcClient("memberService") MemberServiceGrpc.MemberServiceBlockingStub memberStub
    ) {
        this.memberContactRepository = memberContactRepository;
        this.memberStub = memberStub;
    }

    public MemberContact save(Event<MemberCreated> event) {
        MemberContact memberContact = MemberContact.builder()
                .id(event.getData().getId())
                .email(event.getData().getEmail())
                .phone(event.getData().getPhone())
                .build();
        return memberContactRepository.save(memberContact);
    }

    @CircuitBreaker(name = "memberService", fallbackMethod = "fallbackGetById")
    @Retry(name = "memberService")
    public MemberContact getById(String id) {
        log.info("Buscando member contact con ID: {}", id);
        Optional<MemberContact> memberContact = memberContactRepository.findById(id);
        if(memberContact.isPresent()) {
            log.info("Member contact encontrado en base de datos local: {}", id);
            return memberContact.get();
        } else {
            log.info("Member contact no encontrado en local, llamando a servicio gRPC: {}", id);
            MemberContactResponse memberContactResponse = getMemberContactById(id);
            MemberContact memberContactNew = MemberContact.builder()
                    .id(memberContactResponse.getId())
                    .email(memberContactResponse.getEmail())
                    .phone(memberContactResponse.getPhone())
                    .build();
            log.info("Guardando member contact en base de datos local: {}", id);
            return memberContactRepository.save(memberContactNew);
        }
    }


    public MemberContactResponse getMemberContactById(String id) {
        log.info("Iniciando llamada gRPC para member: {}", id);
        try {
            MemberRequest request = MemberRequest.newBuilder().setId(id).build();
            MemberContactResponse response = memberStub.getMemberContactById(request);
            log.info("Llamada gRPC exitosa para member: {}", id);
            return response;
        } catch (io.grpc.StatusRuntimeException e) {
            log.error("Error gRPC - Status: {}, Description: {}",
                    e.getStatus().getCode(), e.getStatus().getDescription());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado en llamada gRPC: {}", e.getMessage(), e);
            throw new RuntimeException("Error en servicio gRPC: " + e.getMessage(), e);
        }
    }

  /*  public MemberContactResponse fallbackGetMemberContactById(String id, Exception exception) {
        log.warn("CIRCUIT BREAKER - Fallback activado para member: {}. Error: {}", id, exception.getMessage());
        return MemberContactResponse.newBuilder()
                .setId(id)
                .setEmail("unknown@fallback.com")
                .setPhone("000-000-0000")
                .build();
    }*/

    public MemberContact fallbackGetById(String id, Exception exception){
        log.warn("CIRCUIT BREAKER - Fallback activado para member: {}. Error: {}", id, exception.getMessage());
        return MemberContact.builder()
                .id(id)
                .email("Unknown")
                .phone("Unknown")
                .build();
        // es importante que el fallback no guarde en el repository la informacion del member
    }
}