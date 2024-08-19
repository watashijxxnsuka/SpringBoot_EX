package com.bit.springboard.mapper;

import com.bit.springboard.dto.BoardDto;
import com.bit.springboard.dto.BoardFileDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FreeMapper {
    void post(BoardDto boardDto);

    BoardDto findById(int id);

    void postFiles(List<BoardFileDto> boardFileDtoList);
}
