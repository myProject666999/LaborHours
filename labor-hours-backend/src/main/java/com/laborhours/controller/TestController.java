package com.laborhours.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.laborhours.common.Result;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.success("Hello, LaborHours!");
    }

    @PostMapping("/dateTest")
    public Result<DateTestVO> dateTest(@RequestBody DateTestDTO dto) {
        DateTestVO vo = new DateTestVO();
        vo.setStartTime(dto.getStartTime());
        vo.setEndTime(dto.getEndTime());
        vo.setDate(dto.getDate());
        vo.setMessage("日期解析成功！");
        return Result.success(vo);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DateTestDTO {
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private LocalDateTime startTime;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private LocalDateTime endTime;

        @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
        private LocalDate date;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DateTestVO {
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private LocalDateTime startTime;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private LocalDateTime endTime;

        @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
        private LocalDate date;

        private String message;
    }
}
