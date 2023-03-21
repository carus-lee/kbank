package com.kbank.service;

import com.kbank.domain.Member;
import com.kbank.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 매니저
 */
@Slf4j
public class MemberServiceV3_2 {

//    private final PlatformTransactionManager transactionManager;
    private final TransactionTemplate txTemplate;
    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepository) {
        this.txTemplate = new TransactionTemplate(transactionManager);
        this.memberRepository = memberRepository;
    }

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        // 트랜잭션 템플릿을 사용해서 "커밋 or 롤백"하는 코드가 제거된다. (아래 주석처리된 부분)
        txTemplate.executeWithoutResult((status) -> {
            try {
                bizLogic(fromId, toId, money);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
//        //트랜잭션 시작
//        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition()); //트랜잭션의 상태정보를 받아서 커밋 or 롤백할 때 넘겨준다.
//
//        try {
//            //비지니스 로직
//            bizLogic(fromId, toId, money);
//            transactionManager.commit(status); //성공시 커밋
//        } catch(Exception e) {
//            transactionManager.rollback(status); //실패시 롤백
//            throw new IllegalStateException(e);
//        }
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
