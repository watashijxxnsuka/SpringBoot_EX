package com.bit.springboard.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/board")
public class BoardController {
    @GetMapping("/free-list")
    public ModelAndView freeListView() {
        ModelAndView mav = new ModelAndView();

        mav.setViewName("board/free-list");
        return mav;
    }

    @GetMapping("/notice-list")
    public ModelAndView noticeListView() {
        ModelAndView mav = new ModelAndView();

        mav.setViewName("board/notice-list");
        return mav;
    }

    @GetMapping("/free-detail")
    public ModelAndView freeDetailView() {
        ModelAndView mav = new ModelAndView();

        mav.setViewName("board/free-detail");
        return mav;
    }

    @GetMapping("/notice-detail")
    public ModelAndView noticeDetailView() {
        ModelAndView mav = new ModelAndView();

        mav.setViewName("board/notice-detail");
        return mav;
    }

    @GetMapping("/post")
    public ModelAndView postView() {
        ModelAndView mav = new ModelAndView();

        mav.setViewName("board/post");
        return mav;
    }










}
