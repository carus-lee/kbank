package com.kbank.domain;

import lombok.Data;

@Data
public class Member {

    private String memberId;
    private int money;

    public Member() {

    }

    public Member(String memeberId, int money) {
        this.memberId = memeberId;
        this.money = money;
    }
}
