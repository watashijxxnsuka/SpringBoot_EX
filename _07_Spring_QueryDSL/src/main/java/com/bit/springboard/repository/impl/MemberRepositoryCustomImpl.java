package com.bit.springboard.repository.impl;

import com.bit.springboard.entity.Member;
import com.bit.springboard.repository.MemberRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.bit.springboard.entity.QFreeBoard.freeBoard;
import static com.bit.springboard.entity.QMember.member;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {
    // QueryDSL의 메소드를 이용해서 쿼리를 생성하는 클래스
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Member searchOne(Long id) {
        return jpaQueryFactory.selectFrom(member)
                .where(member.id.eq(id))
                .fetchOne();
    }

    @Override
    public String searchUsername(Long id) {
        return jpaQueryFactory.select(member.username)
                .from(member)
                .where(member.id.eq(id))
                .fetchOne();
    }





}
