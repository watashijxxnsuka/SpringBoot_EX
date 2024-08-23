package com.bit.springboard.service.impl;

import com.bit.springboard.common.FileUtils;
import com.bit.springboard.dto.BoardDto;
import com.bit.springboard.dto.BoardFileDto;
import com.bit.springboard.dto.Criteria;
import com.bit.springboard.mapper.FreeMapper;
import com.bit.springboard.service.BoardService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FreeServiceImpl implements BoardService {
    private final FreeMapper freeMapper;
    private final FileUtils fileUtils;

    @Override
    public BoardDto post(BoardDto boardDto, MultipartFile[] uploadFiles) {
        List<BoardFileDto> boardFileDtoList = new ArrayList<>();

        if(uploadFiles != null && uploadFiles.length > 0) {
            Arrays.stream(uploadFiles).forEach(file -> {
                if(file.getOriginalFilename() != null && !file.getOriginalFilename().equals("")) {
                    BoardFileDto boardFileDto = fileUtils.parserFileInfo(file, "free/");
                    boardFileDtoList.add(boardFileDto);
                }
            });
        }

        freeMapper.post(boardDto);

        if(boardFileDtoList.size() > 0) {
            boardFileDtoList.forEach(boardFileDto -> boardFileDto.setBoard_id(boardDto.getId()));
            freeMapper.postFiles(boardFileDtoList);
        }

        return freeMapper.findById(boardDto.getId());
    }

    @Override
    public List<BoardDto> findAll(Map<String, String> searchMap, Criteria cri) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("search", searchMap);

        cri.setStartNum((cri.getPageNum() - 1) * cri.getAmount());
        paramMap.put("cri", cri);

        return freeMapper.findAll(paramMap);
    }

    @Override
    public BoardDto findById(int id) {
        return freeMapper.findById(id);
    }

    @Override
    public List<BoardFileDto> findFilesById(int id) {
        return freeMapper.findFilesById(id);
    }

    @Override
    public BoardDto modify(BoardDto boardDto, MultipartFile[] uploadFiles, MultipartFile[] changeFiles, String originFiles) {
        List<BoardFileDto> originFileList = new ArrayList<>();

        try {
            originFileList = new ObjectMapper().readValue(
                    originFiles,
                    new TypeReference<List<BoardFileDto>>() {}
            );
        } catch (IOException ie) {
            System.out.println(ie.getMessage());
        }

        List<BoardFileDto> uFileList = new ArrayList<>();

        if(originFileList.size() > 0) {
            originFileList.forEach(boardFileDto -> {
                if(boardFileDto.getFilestatus().equals("U") && changeFiles != null) {
                    Arrays.stream(changeFiles).forEach(file -> {
                        if(boardFileDto.getNewfilename().equals(file.getOriginalFilename())) {
                            BoardFileDto updatedBoardFileDto = fileUtils.parserFileInfo(file, "free/");

                            updatedBoardFileDto.setId(boardFileDto.getId());
                            updatedBoardFileDto.setBoard_id(boardDto.getId());
                            updatedBoardFileDto.setFilestatus("U");

                            uFileList.add(updatedBoardFileDto);
                        }
                    });
                } else if(boardFileDto.getFilestatus().equals("D")) {
                    BoardFileDto deleteBoardFileDto = new BoardFileDto();

                    deleteBoardFileDto.setBoard_id(boardDto.getId());
                    deleteBoardFileDto.setId(boardFileDto.getId());
                    deleteBoardFileDto.setFilestatus("D");

                    fileUtils.deleteFile("free/", boardFileDto.getFilename());

                    uFileList.add(deleteBoardFileDto);
                }
            });
        }

        if(uploadFiles != null && uploadFiles.length > 0) {
            Arrays.stream(uploadFiles).forEach(file -> {
                if(!file.getOriginalFilename().equals("") && file.getOriginalFilename() != null) {
                    BoardFileDto postBoardFileDto = fileUtils.parserFileInfo(file, "free/");

                    postBoardFileDto.setBoard_id(boardDto.getId());
                    postBoardFileDto.setFilestatus("I");

                    uFileList.add(postBoardFileDto);
                }
            });
        }

        boardDto.setModdate(LocalDateTime.now());
        freeMapper.modify(boardDto);

        uFileList.forEach(boardFileDto -> {
            if(boardFileDto.getFilestatus().equals("U")) {
                freeMapper.modifyFile(boardFileDto);
            } else if(boardFileDto.getFilestatus().equals("D")) {
                freeMapper.removeFile(boardFileDto);
            } else if(boardFileDto.getFilestatus().equals("I")) {
                freeMapper.postFile(boardFileDto);
            }
        });

        return freeMapper.findById(boardDto.getId());
    }

    @Override
    public void updateBoardCnt(int id) {
        freeMapper.updateBoardCnt(id);
    }

    @Override
    public void remove(int id) {
        freeMapper.removeFiles(id);
        freeMapper.remove(id);
    }

    @Override
    public int findTotalCnt(Map<String, String> searchMap) {
        return freeMapper.findTotalCnt(searchMap);
    }
}
