package com.healthcare.notification_service.service;

import com.healthcare.grpc.MemberRequest;
import com.healthcare.grpc.MemberContactResponse;
import com.healthcare.grpc.MemberServiceGrpc;
import com.healthcare.notification_service.model.MemberContact;
import com.healthcare.notification_service.model.event.Event;
import com.healthcare.notification_service.model.event.MemberCreated;
import com.healthcare.notification_service.repository.MemberContactRepository;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberContactService {

    private final MemberContactRepository memberContactRepository;
    private final MemberServiceGrpc.MemberServiceBlockingStub memberStub;

    public MemberContactService(MemberContactRepository memberContactRepository,
                                @Value("${grpc.client.memberService.address}") String memberServiceAddress) {
        this.memberContactRepository = memberContactRepository;

        // Crear canal gRPC usando la dirección configurada
        Channel channel = ManagedChannelBuilder
                .forTarget(memberServiceAddress.replace("static://", "")) // elimina prefijo si lo tenés
                .usePlaintext()
                .build();

        this.memberStub = MemberServiceGrpc.newBlockingStub(channel);
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
        Optional<MemberContact> memberContact = memberContactRepository.findById(id);
        if(memberContact.isPresent())
            return memberContact.get();
        else {
            MemberContactResponse memberContactResponse = getMemberContactById(id);
            MemberContact memberContactNew = MemberContact.builder()
                                                .id(memberContactResponse.getId())
                                                .email(memberContactResponse.getEmail())
                                                .phone(memberContactResponse.getPhone())
                                                .build();
            // TODO: se podria guardar en otro hilo y devolver el member contact para optimizar -> activando virtual threads
            return memberContactRepository.save(memberContactNew);                                  
        }
        
    }

    public MemberContactResponse getMemberContactById(String id) {
        MemberRequest request = MemberRequest.newBuilder().setId(id).build();
        return memberStub.getMemberContactById(request);
    }
}
