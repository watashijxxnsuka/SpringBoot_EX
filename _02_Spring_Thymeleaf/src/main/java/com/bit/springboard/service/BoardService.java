package com.bit.springboard.service;

import com.bit.springboard.dto.BoardDto;
import org.springframework.web.multipart.MultipartFile;

public interface BoardService {
    BoardDto post(BoardDto boardDto, MultipartFile[] uploadFiles);
}
