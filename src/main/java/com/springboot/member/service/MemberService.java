package com.springboot.member.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final String USER_ROLE_ADMIN = "admin@gmail.com";

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member createMember(Member member) {
        isExistPhone(member.getPhone());
        boolean isAdmin = verifyExistEmail(member.getEmail());

        if (isAdmin) {
            member.setUserRole(Member.UserRole.USER_ROLE_ADMIN);
        }
        return memberRepository.save(member);
    }

    public Member updateMember(Member member) {
        Member findMember = findVerifiedMember(member.getMemberId());
        Optional.ofNullable(member.getName()).ifPresent(findMember::setName);
        Optional.ofNullable(member.getPhone()).ifPresent(findMember::setPhone);
        Optional.ofNullable(member.getMemberStatus()).ifPresent(findMember::setMemberStatus);

        findMember.setModifiedAt(LocalDateTime.now());
        return memberRepository.save(findMember);
    }

    public Member findMember(long memberId) {
        return findVerifiedMember(memberId);
    }

    public Page<Member> findMembers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("memberId").descending());
        return memberRepository.findAll(pageable);
    }

    public void deleteMember(long memberId) {
        Member findMember = findVerifiedMember(memberId);

        memberRepository.delete(findMember);
    }

    private boolean verifyExistEmail(String email) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);


        if (optionalMember.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_ALREADY_EXIST);
        } else {
            if (email.equals(USER_ROLE_ADMIN)) {
                return true;
            }
        }
        return false;
    }

    public Member findVerifiedMember(long memberId) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        return optionalMember.orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    private void isExistPhone(String phone) {
        Optional<Member> optionalMember = memberRepository.findByPhone(phone);
        if (optionalMember.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_ALREADY_EXIST);
        }
    }

    public boolean isAdmin(long memberId){
        boolean isAdmin =false;
        Member member = findVerifiedMember(memberId);
        if(member.getEmail().equals(USER_ROLE_ADMIN)){
            return true;
        }else{
            throw new BusinessLogicException(ExceptionCode.ONLY_ADMIN_CAN_WRITE);
        }

    }
}