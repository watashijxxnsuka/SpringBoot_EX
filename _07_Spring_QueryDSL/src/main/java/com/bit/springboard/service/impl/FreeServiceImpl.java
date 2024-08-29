package com.bit.springboard.service.impl;

import com.bit.springboard.common.FileUtils;
import com.bit.springboard.dto.BoardDto;
import com.bit.springboard.dto.BoardFileDto;
import com.bit.springboard.dto.Criteria;
import com.bit.springboard.entity.FreeBoard;
import com.bit.springboard.entity.FreeBoardFile;
import com.bit.springboard.entity.Member;
import com.bit.springboard.mapper.FreeMapper;
import com.bit.springboard.repository.FreeBoardFileRepository;
import com.bit.springboard.repository.FreeBoardRepository;
import com.bit.springboard.repository.MemberRepository;
import com.bit.springboard.service.BoardService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final FreeBoardRepository freeBoardRepository;
    private final MemberRepository memberRepository;
    private final FreeBoardFileRepository freeBoardFileRepository;

    @Override
    public BoardDto post(BoardDto boardDto, MultipartFile[] uploadFiles) {

        Member member = memberRepository.findByNickname(boardDto.getNickname())
                .orElseThrow(() -> new RuntimeException("member not exist"));

        boardDto.setRegdate(LocalDateTime.now());
        boardDto.setModdate(LocalDateTime.now());

        FreeBoard freeBoard = boardDto.toFreeBoardEntity(member);

        if(uploadFiles != null && uploadFiles.length > 0) {
            Arrays.stream(uploadFiles).forEach(file -> {
                if(file.getOriginalFilename() != null && !file.getOriginalFilename().equals("")) {
                    BoardFileDto boardFileDto = fileUtils.parserFileInfo(file, "free/");
                    FreeBoardFile freeBoardFile = boardFileDto.toFreeBoardFileEntity(freeBoard);
                    freeBoard.getBoardFileList().add(freeBoardFile);
                }
            });
        }

//        if(boardFileDtoList.size() > 0) {
//            List<FreeBoardFile> freeBoardFileList =
//                    boardFileDtoList.stream()
//                            .map(boardFileDto -> boardFileDto.toFreeBoardFileEntity(freeBoard))
//                            .toList();//List<FreeBoardFile>
//
//            freeBoardFileList.forEach(freeBoardFile -> freeBoard.getBoardFileList().add(freeBoardFile));
//        }

        return freeBoardRepository.save(freeBoard).toDto();
    }

    @Override
    public Page<BoardDto> findAll(Map<String, String> searchMap, Pageable pageable) {
        Page<FreeBoard> freeBoardPage = freeBoardRepository.findAllFreeBoard(pageable);

        if(searchMap.get("searchKeyword") != null) {
            if(searchMap.get("searchCondition").toLowerCase().equals("all")) {
                freeBoardPage = freeBoardRepository.findBySearchKeyword(
                        pageable, searchMap.get("searchKeyword")
                );
            } else if(searchMap.get("searchCondition").toLowerCase().equals("title")) {
                freeBoardPage = freeBoardRepository.findByTitleContaining(pageable, searchMap.get("searchKeyword"));
            } else if(searchMap.get("searchCondition").toLowerCase().equals("content")) {
                freeBoardPage = freeBoardRepository.findByContentContaining(pageable, searchMap.get("searchKeyword"));
            }  else if(searchMap.get("searchCondition").toLowerCase().equals("writer")) {
                freeBoardPage = freeBoardRepository.findByMemberNicknameContaining(pageable, searchMap.get("searchKeyword"));
            }
        }

        return freeBoardPage.map(FreeBoard::toDto);
    }

    @Override
    public BoardDto findById(Long id) {
        return freeBoardRepository.findById(id).orElseThrow(
                () -> new RuntimeException("board not exist")).toDto();
    }

    @Override
    public List<BoardFileDto> findFilesById(Long id) {
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

//        freeMapper.modify(boardDto);
//
//        uFileList.forEach(boardFileDto -> {
//            if(boardFileDto.getFilestatus().equals("U")) {
//                freeMapper.modifyFile(boardFileDto);
//            } else if(boardFileDto.getFilestatus().equals("D")) {
//                freeMapper.removeFile(boardFileDto);
//            } else if(boardFileDto.getFilestatus().equals("I")) {
//                freeMapper.postFile(boardFileDto);
//            }
//        });

//        FreeBoard board = boardDto.toFreeBoardEntity(
//                memberRepository.findById(boardDto.getWriter_id()).orElseThrow());

        FreeBoard freeBoard = freeBoardRepository.findById(boardDto.getId()).orElseThrow(
                () -> new RuntimeException("board not exist")
        );

        freeBoard.setTitle(boardDto.getTitle());
        freeBoard.setContent(boardDto.getContent());
        freeBoard.setModdate(boardDto.getModdate());

        uFileList.forEach(
                boardFileDto -> {
                    if(boardFileDto.getFilestatus().equals("U")
                            || boardFileDto.getFilestatus().equals("I")) {
                        freeBoard.getBoardFileList().add(boardFileDto.toFreeBoardFileEntity(freeBoard));
                    } else if(boardFileDto.getFilestatus().equals("D")) {
                        fileUtils.deleteFile("free/", boardFileDto.getFilename());
                        freeBoardFileRepository.delete(boardFileDto.toFreeBoardFileEntity(freeBoard));
                    }
                }
        );

        return freeBoardRepository.save(freeBoard).toDto();
    }

    @Override
    public void updateBoardCnt(Long id) {
        FreeBoard freeBoard = freeBoardRepository.findById(id).orElseThrow(
                () -> new RuntimeException("board not exist"));

        freeBoard.setCnt(freeBoard.getCnt() + 1);

        freeBoardRepository.save(freeBoard);
    }

    @Override
    public void remove(Long id) {
        List<FreeBoardFile> freeBoardFileList =
                freeBoardRepository.findById(id).orElseThrow(
                        () -> new RuntimeException("board not exist")
                ).getBoardFileList();

        freeBoardFileList.forEach(freeBoardFile ->
                fileUtils.deleteFile("free/", freeBoardFile.getFilename()));

        freeBoardRepository.deleteById(id);
    }

    @Override
    public int findTotalCnt(Map<String, String> searchMap) {
        return freeMapper.findTotalCnt(searchMap);
    }
}
