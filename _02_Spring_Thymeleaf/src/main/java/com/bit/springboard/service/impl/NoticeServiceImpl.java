package com.bit.springboard.service.impl;

import com.bit.springboard.dto.BoardDto;
import com.bit.springboard.service.BoardService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class NoticeServiceImpl implements BoardService {
    @Override
    public BoardDto post(BoardDto boardDto, MultipartFile[] uploadFiles) {
        return null;
    }
}
