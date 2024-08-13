package com.bit.springboard.controller;

import com.bit.springboard.dto.MemberDto;
import com.bit.springboard.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// @RestController  =  @Controller +  @ResponseBody
@RestController
@RequestMapping("/apis")
@RequiredArgsConstructor
public class ApiController {
    private final MemberService memberService;

    @PostMapping("/members")
    public ResponseEntity<?> save(MemberDto memberDto) {
        MemberDto savedMemberDto = memberService.save(memberDto);
        return ResponseEntity.ok(savedMemberDto);
    }
}
