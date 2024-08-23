package com.bit.springboard.service.impl;

import com.bit.springboard.common.FileUtils;
import com.bit.springboard.dto.BoardDto;
import com.bit.springboard.dto.BoardFileDto;
import com.bit.springboard.dto.MemberDto;
import com.bit.springboard.entity.FreeBoardFile;
import com.bit.springboard.entity.Freeboard;
import com.bit.springboard.entity.Member;
import com.bit.springboard.repository.FreeBoardRepository;
import com.bit.springboard.repository.MemberRepository;
import com.bit.springboard.service.ApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiServiceImpl implements ApiService {
    private final MemberRepository memberRepository;
    private final FileUtils fileUtils;
    private final FreeBoardRepository freeBoardRepository;

    @Override
    public Member save(MemberDto memberDto) {
        Member member = new Member();
        member.setUsername(memberDto.getUsername());
        member.setPassword(memberDto.getPassword());
        member.setEmail(memberDto.getEmail());
        member.setNickname(memberDto.getNickname());
        member.setTel(memberDto.getTel());
        return memberRepository.save(member);
    }

    @Override
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    @Override
    public Member findById(Long id) {
        return memberRepository.findById(id).orElseThrow();
    }

    @Override
    public void deleteById(Long id) {
        memberRepository.deleteById(id);
    }

    @Override
    public Member modify(Long id, MemberDto memberDto) {
        Member member = memberRepository.findById(id).orElseThrow();

        member.setPassword(memberDto.getPassword());
        member.setEmail(memberDto.getEmail());
        member.setNickname(memberDto.getNickname());
        member.setTel(memberDto.getTel());

        return memberRepository.save(member);
    }

    @Override
    public Freeboard post(BoardDto boardDto, MultipartFile[] uploadFiles) {
        List<FreeBoardFile> freeBoardFileList = new ArrayList<>();
        if(uploadFiles != null && uploadFiles.length > 0) {
            Arrays.stream(uploadFiles).forEach(file -> {
                if(file.getOriginalFilename() != null && !file.getOriginalFilename().equals("")) {
                    BoardFileDto boardFileDto = fileUtils.parserFileInfo(file, "free/");

                    FreeBoardFile freeBoardFile = new FreeBoardFile();

                    freeBoardFile.setFileName(boardFileDto.getFilename());
                    freeBoardFile.setFileoriginname(boardFileDto.getFileoriginname());
                    freeBoardFile.setFilePath(boardFileDto.getFilepath());
                    freeBoardFile.setFiletype(boardFileDto.getFiletype());

                    freeBoardFileList.add(freeBoardFile);
                }
            });
        }

        Freeboard freeboard = new Freeboard();

        freeboard.setTitle(boardDto.getTitle());
        freeboard.setContent(boardDto.getContent());
        freeboard.setMember(memberRepository.findById((long)boardDto.getWriter_id()).orElseThrow());
        freeboard.setBoardFileList(freeBoardFileList);

        freeboard.setCnt(0);
        freeboard.setRegdate(LocalDateTime.now());
        freeboard.setModdate(LocalDateTime.now());

        freeboard.getBoardFileList().forEach(freeBoardFile -> freeBoardFile.setFreeBoard(freeboard));

        return freeBoardRepository.save(freeboard);
    }

    @Override
    public Freeboard findFreeBoardById(Long id) {
        return freeBoardRepository.findById(id).orElseThrow();
    }
}
