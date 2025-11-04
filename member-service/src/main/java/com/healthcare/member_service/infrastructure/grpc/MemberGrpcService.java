package com.healthcare.member_service.infrastructure.grpc;

import com.healthcare.grpc.MemberContactResponse;
import com.healthcare.grpc.MemberRequest;
import com.healthcare.grpc.MemberServiceGrpc;
import com.healthcare.member_service.domain.model.member.Member;
import com.healthcare.member_service.domain.model.member.valueobject.MemberId;
import com.healthcare.member_service.domain.port.in.MemberUseCase;
import net.devh.boot.grpc.server.service.GrpcService;
import lombok.AllArgsConstructor;

@GrpcService
@AllArgsConstructor
public class MemberGrpcService extends MemberServiceGrpc.MemberServiceImplBase {
    private final MemberUseCase memberUseCase;

    @Override
    public void getMemberContactById(MemberRequest request, io.grpc.stub.StreamObserver<MemberContactResponse> responseObserver) {
        MemberId memberId = new MemberId(request.getId());
        Member member = memberUseCase.getMemberById(memberId);
        MemberContactResponse response = MemberContactResponse.newBuilder()
                .setId(request.getId())
                .setPhone(member.getPhone().value())
                .setEmail(member.getEmail().value())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

