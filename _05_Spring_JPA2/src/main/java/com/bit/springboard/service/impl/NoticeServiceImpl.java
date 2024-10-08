package com.bit.springboard.service.impl;

import com.bit.springboard.common.FileUtils;
import com.bit.springboard.dto.BoardDto;
import com.bit.springboard.dto.BoardFileDto;
import com.bit.springboard.dto.Criteria;
import com.bit.springboard.mapper.NoticeMapper;
import com.bit.springboard.service.BoardService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements BoardService {
    private final NoticeMapper noticeMapper;
    private final FileUtils fileUtils;

//    public NoticeServiceImpl(NoticeMapper noticeMapper, FileUtils fileUtils) {
//        this.noticeMapper = noticeMapper;
//        this.fileUtils = fileUtils;
//    }

    @Override
    public BoardDto post(BoardDto boardDto, MultipartFile[] uploadFiles) {
        List<BoardFileDto> boardFileDtoList = new ArrayList<>();

        if(uploadFiles != null && uploadFiles.length > 0) {
            Arrays.stream(uploadFiles).forEach(file -> {
                if(!file.getOriginalFilename().equals("") && file.getOriginalFilename() != null) {
                    BoardFileDto boardFileDto = fileUtils.parserFileInfo(file, "notice/");

                    boardFileDtoList.add(boardFileDto);
                }
            });
        }

        noticeMapper.post(boardDto);

        if(boardFileDtoList.size() > 0) {
            boardFileDtoList.forEach(boardFileDto -> boardFileDto.setBoard_id(boardDto.getId()));
            noticeMapper.postFiles(boardFileDtoList);
        }

        return noticeMapper.findById(boardDto.getId());
    }

    @Override
    public Page<BoardDto> findAll(Map<String, String> searchMap, Pageable pageable) {
        return null;
    }

    @Override
    public BoardDto findById(Long id) {
        return noticeMapper.findById(id);
    }

    @Override
    public List<BoardFileDto> findFilesById(Long id) {
        return noticeMapper.findFilesById(id);
    }

    @Override
    public BoardDto modify(BoardDto boardDto, MultipartFile[] uploadFiles, MultipartFile[] changeFiles, String originFiles) {
        List<BoardFileDto> originFileList = new ArrayList<>();

        try {
            originFileList = new ObjectMapper().readValue(
                    originFiles,
                    new TypeReference<List<BoardFileDto>>() {}
            );
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

        List<BoardFileDto> uFileList = new ArrayList<>();

        if(originFileList.size() > 0) {
            originFileList.forEach(boardFileDto -> {
                if(boardFileDto.getFilestatus().equals("U") && changeFiles != null) {
                    Arrays.stream(changeFiles).forEach(file -> {
                        if(boardFileDto.getNewfilename().equals(file.getOriginalFilename())) {
                            BoardFileDto updateBoardFileDto = fileUtils.parserFileInfo(file, "notice/");

                            updateBoardFileDto.setBoard_id(boardDto.getId());
                            updateBoardFileDto.setId(boardFileDto.getId());
                            updateBoardFileDto.setFilestatus("U");

                            uFileList.add(updateBoardFileDto);
                        }
                    });
                } else if(boardFileDto.getFilestatus().equals("D")) {
                    BoardFileDto deleteBoardFileDto = new BoardFileDto();

                    deleteBoardFileDto.setBoard_id(boardDto.getId());
                    deleteBoardFileDto.setId(boardFileDto.getId());
                    deleteBoardFileDto.setFilestatus("D");

                    fileUtils.deleteFile("notice/", boardFileDto.getFilename());

                    uFileList.add(deleteBoardFileDto);
                }
            });
        }

        if(uploadFiles != null && uploadFiles.length > 0) {
            Arrays.stream(uploadFiles).forEach(file -> {
                if(file.getOriginalFilename() != null && !file.getOriginalFilename().equals("")) {
                    BoardFileDto postBoardFileDto = fileUtils.parserFileInfo(file, "notice/");

                    postBoardFileDto.setBoard_id(boardDto.getId());
                    postBoardFileDto.setFilestatus("I");

                    uFileList.add(postBoardFileDto);
                }
            });
        }

        boardDto.setModdate(LocalDateTime.now());
        noticeMapper.modify(boardDto);

        uFileList.forEach(boardFileDto -> {
            if(boardFileDto.getFilestatus().equals("U")) {
                noticeMapper.modifyFile(boardFileDto);
            } else if(boardFileDto.getFilestatus().equals("D")) {
                noticeMapper.removeFile(boardFileDto);
            } else if(boardFileDto.getFilestatus().equals("I")) {
                noticeMapper.postFile(boardFileDto);
            }
        });

        return noticeMapper.findById(boardDto.getId());
    }

    @Override
    public void updateBoardCnt(Long id) {
        noticeMapper.updateBoardCnt(id);
    }

    @Override
    public void remove(Long id) {
        noticeMapper.removeFiles(id);
        noticeMapper.remove(id);
    }

    @Override
    public int findTotalCnt(Map<String, String> searchMap) {
        return noticeMapper.findTotalCnt(searchMap);
    }
}
