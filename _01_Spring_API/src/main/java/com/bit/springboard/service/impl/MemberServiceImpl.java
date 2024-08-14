package com.bit.springboard.service.impl;

import com.bit.springboard.dto.MemberDto;
import com.bit.springboard.mapper.MemberMapper;
import com.bit.springboard.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberMapper memberMapper;

    @Override
    public MemberDto save(MemberDto memberDto) {
        memberMapper.save(memberDto);
        return memberMapper.findLastMember();
    }

    @Override
    public List<MemberDto> findAll() {
        return memberMapper.findAll();
    }

    @Override
    public MemberDto findById(int id) {
        return memberMapper.findById(id);
    }

    @Override
    public void deleteById(int id) {
        memberMapper.deleteById(id);
    }

    @Override
    public MemberDto update(MemberDto memberDto) {
        memberMapper.update(memberDto);
        return memberMapper.findById(memberDto.getId());
    }


}
