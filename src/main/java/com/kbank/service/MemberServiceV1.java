package com.kbank.service;

import com.kbank.domain.Member;
import com.kbank.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;

@RequiredArgsConstructor
public class MemberServiceV1 {

    private final MemberRepositoryV1 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
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
