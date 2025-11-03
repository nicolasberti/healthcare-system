package com.healthcare.member_service.application.member;

import com.healthcare.member_service.common.command.CreateMember;
import com.healthcare.member_service.domain.aggregate.member.MemberAggregate;
import com.healthcare.member_service.domain.model.member.Member;
import com.healthcare.member_service.domain.model.member.exception.MemberNotFoundException;
import com.healthcare.member_service.domain.model.member.valueobject.Age;
import com.healthcare.member_service.domain.model.member.valueobject.Email;
import com.healthcare.member_service.domain.model.member.valueobject.MemberId;
import com.healthcare.member_service.domain.model.member.valueobject.Phone;
import com.healthcare.member_service.domain.port.in.MemberUseCase;
import com.healthcare.member_service.domain.port.out.DomainEventPublisher;
import com.healthcare.member_service.domain.port.out.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class MemberService implements MemberUseCase {
    private final MemberRepository memberRepository;
    private final DomainEventPublisher domainEventPublisher;

    @Override
    public Member createMember(CreateMember createMember) {
        MemberAggregate memberAggregate = MemberAggregate.createMember(
                new MemberId(), createMember.getName(),
                new Email(createMember.getEmail()),
                new Age(createMember.getAge()),
                new Phone(createMember.getPhone())
        );

        // TODO: Si existe (email unico o telefono unico) lanzar excepcion MemberAlreadyExistsException
        Member member = memberRepository.save(memberAggregate.getMember());

        // TODO: Agregar outbox pattern
        memberAggregate.getDomainEvents().forEach(event ->
                domainEventPublisher.publish("Member", memberAggregate.getMember().getId(), event)
        );

        return member;
    }

    @Override
    public Member getMemberById(MemberId id) {
        log.info("Service -> memberId: {}", id.toString());
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + id.getValue().toString()));
    }

    @Override
    public List<Member> getAllMembers() {
       return memberRepository.findAll();
    }

    @Override
    public Member updateMember(MemberId id, Member member) {
        return memberRepository.update(id, member);
    }

    @Override
    public void deleteMember(MemberId id) {
        memberRepository.delete(id);
    }
}
