package com.kbank.service;

import com.kbank.domain.Member;
import com.kbank.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection con = getConnection();
        try {
            con.setAutoCommit(false); //트랜잭션 시작
            //비지니스 로직
            bizLogic(con, fromId, toId, money);
            con.commit(); //성공시 커밋 - 트랜잭션 종료
        } catch(Exception e) {
            con.rollback(); //실패시 롤백
            throw new IllegalStateException(e);
        } finally {
//            JdbcUtils.closeConnection(con);
            if (con != null) {
                try {
                    con.setAutoCommit(true); //커넥션 풀 고려
                    con.close();
                } catch(Exception e) {
                    log.info("error", e);
                }
            }
        }
    }

    private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
        //비지니스 로직 수행
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);

        //memberA -> memberB로 money만큼 계좌이체
        memberRepository.update(con, fromId, fromMember.getMoney() - money); //memberA: money만큼 감소
        validatation(toMember); // toMember의 ID가 "ex"인 경우 예외발생 (예외시 검증)
        memberRepository.update(con, toId, toMember.getMoney() + money); //memberB:  money만큼 증가
    }

    private void validatation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

    public Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }
}
