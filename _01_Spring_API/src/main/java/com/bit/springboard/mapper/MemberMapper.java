package com.bit.springboard.mapper;

import com.bit.springboard.dto.MemberDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {
    void save(MemberDto memberDto);

    MemberDto findLastMember();
}
