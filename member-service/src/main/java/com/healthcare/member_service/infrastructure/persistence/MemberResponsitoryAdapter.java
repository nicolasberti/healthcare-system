package com.healthcare.member_service.infrastructure.persistence;

import com.healthcare.member_service.domain.model.member.Member;
import com.healthcare.member_service.domain.model.member.exception.MemberAlreadyExistsException;
import com.healthcare.member_service.domain.model.member.valueobject.MemberId;
import com.healthcare.member_service.domain.port.out.MemberRepository;
import com.healthcare.member_service.infrastructure.persistence.mapper.MemberMapper;
import com.healthcare.member_service.infrastructure.persistence.model.MemberEntity;
import com.healthcare.member_service.infrastructure.persistence.repository.MemberRepositoryJPA;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j
public class MemberResponsitoryAdapter implements MemberRepository {
    private final MemberRepositoryJPA memberRepositoryJPA;

    @Override
    public Member save(Member member) {
        try {
            MemberEntity memberEntity = MemberMapper.memberToMemberEntity(member);
            memberRepositoryJPA.save(memberEntity);
            return member;
        } catch(DataIntegrityViolationException exception) {
            throw new MemberAlreadyExistsException("Member already exists");
        }
    }

    @Override
    public Optional<Member> findById(MemberId id) {
        log.info("Repository -> memberId: {}", id.toString());
        Optional<MemberEntity> memberEntity = memberRepositoryJPA.findById(id.getValue().toString());
        log.info("Repository -> memberEntity: {}", memberEntity.toString());
        return memberEntity.map(MemberMapper::memberEntityToMember);
    }

    @Override
    public List<Member> findAll() {
        List<MemberEntity> memberEntities = memberRepositoryJPA.findAll();
        return memberEntities.stream().map(MemberMapper::memberEntityToMember).toList();
    }

    // TODO: Está mal, arreglar.
    @Override
    public Member update(MemberId id, Member member) {
        try {
            MemberEntity memberEntity = MemberMapper.memberToMemberEntity(member);
            memberRepositoryJPA.save(memberEntity);
            return member;
        } catch(DataIntegrityViolationException exception) {
            throw new MemberAlreadyExistsException("Member already exists");
        }
    }

    @Override
    public void delete(MemberId id) {
        memberRepositoryJPA.deleteById(id.getValue().toString());
    }
}
