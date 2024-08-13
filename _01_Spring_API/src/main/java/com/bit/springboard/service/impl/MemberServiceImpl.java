package com.bit.springboard.service.impl;

import com.bit.springboard.dto.MemberDto;
import com.bit.springboard.mapper.MemberMapper;
import com.bit.springboard.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberMapper memberMapper;

    @Override
    public MemberDto save(MemberDto memberDto) {
        memberMapper.save(memberDto);
        return memberMapper.findLastMember();
    }
}
