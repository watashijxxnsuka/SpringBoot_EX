package com.bit.springboard.service.impl;

import com.bit.springboard.dto.BoardDto;
import com.bit.springboard.dto.BoardFileDto;
import com.bit.springboard.dto.Criteria;
import com.bit.springboard.service.BoardService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class NoticeServiceImpl implements BoardService {
    @Override
    public BoardDto post(BoardDto boardDto, MultipartFile[] uploadFiles) {
        return null;
    }

    @Override
    public List<BoardDto> findAll(Map<String, String> searchMap, Criteria cri) {
        return List.of();
    }

    @Override
    public BoardDto findById(int id) {
        return null;
    }

    @Override
    public List<BoardFileDto> findFilesById(int id) {
        return List.of();
    }

    @Override
    public BoardDto modify(BoardDto boardDto, MultipartFile[] uploadFiles, MultipartFile[] changeFiles, String originFiles) {
        return null;
    }

    @Override
    public void updateBoardCnt(int id) {

    }

    @Override
    public void remove(int id) {

    }

    @Override
    public int findTotalCnt(Map<String, String> searchMap) {
        return 0;
    }
}
