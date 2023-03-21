package com.kbank.service;

import com.kbank.domain.Member;
import com.kbank.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

/**
 * 트랜잭션 - @Transactional AOP
 */
@Slf4j
public class MemberServiceV3_3 {

    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_3(MemberRepositoryV3 memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        //비지니스 로직
        bizLogic(fromId, toId, money);
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        //비지니스 로직 수행
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        //memberA -> memberB로 money만큼 계좌이체
        memberRepository.update(fromId, fromMember.getMoney() - money); //memberA: money만큼 감소
        validatation(toMember); // toMember의 ID가 "ex"인 경우 예외발생 (예외시 검증)
        memberRepository.update(toId, toMember.getMoney() + money); //memberB:  money만큼 증가
    }

    private void validatation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
