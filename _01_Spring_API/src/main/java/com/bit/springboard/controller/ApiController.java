package com.bit.springboard.controller;

import com.bit.springboard.dto.MemberDto;
import com.bit.springboard.dto.ResponseDto;
import com.bit.springboard.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

// @RestController = @Controller + @ResponseBody
@RestController
@RequestMapping("/apis")
@RequiredArgsConstructor
public class ApiController {
    private final MemberService memberService;

    @PostMapping("/members")
    public ResponseEntity<?> save(MemberDto memberDto) {
        ResponseDto<MemberDto> responseDto = new ResponseDto<>();
        try {
            MemberDto savedMemberDto = memberService.save(memberDto);
            responseDto.setStatusCode(HttpStatus.CREATED.value());//메롱
            responseDto.setStatusMessage("CREATED");
            responseDto.setData(savedMemberDto);
            return ResponseEntity.ok(responseDto);
        } catch(Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());//롱ㅁㅔ
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }
    @GetMapping("/members")
    public ResponseEntity<?> findAll() {
        ResponseDto<MemberDto> responseDto = new ResponseDto<>();
        try {
            List<MemberDto> memberDtoList = memberService.findAll();
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("OK");
            responseDto.setDataList(memberDtoList);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    @GetMapping("/members/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") int id) {
        ResponseDto<MemberDto> responseDto = new ResponseDto<>();

        try {
            MemberDto memberDto = memberService.findById(id);
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("OK");
            responseDto.setData(memberDto);

            return ResponseEntity.ok(responseDto);

        } catch (Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());

            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    @DeleteMapping("/members/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") int id) {
        ResponseDto<MemberDto> responseDto = new ResponseDto<>();

        try {
            memberService.deleteById(id);

            responseDto.setStatusCode(HttpStatus.NO_CONTENT.value());
            responseDto.setStatusMessage("NO CONTENT");

            return ResponseEntity.ok(responseDto);

        } catch (Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }


    @PatchMapping("/members/{id}")
    // @RequestBody 로 받는 경우는 application/json 형태로 처리하는 경우 사용.
    // 기본적으로 urlencoded 의 경우 @modelAttribute 혹은 @RequestParam 이 사용.
    public ResponseEntity<?> update(@PathVariable("id") int id, @RequestBody MemberDto memberDto) {
        ResponseDto<MemberDto> responseDto = new ResponseDto<>();

        try {
            memberDto.setId(id);
            MemberDto modifiedMemberDto = memberService.update(memberDto);

            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("OK");
            responseDto.setData(modifiedMemberDto);

            return ResponseEntity.ok(responseDto);

        } catch (Exception e) {
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

}
