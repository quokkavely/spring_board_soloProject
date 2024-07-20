package com.springboot.member.dto;

import com.springboot.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

public class MemberDto {

    @Getter
    @AllArgsConstructor
    public static class Post{

        @NotBlank
        @Email
        private String email;

        @Pattern(regexp = "^010-\\d{3,4}-\\d{4}$",
                message = "휴대폰 번호는 010으로 시작하는 11자리 숫자와 '-'로 구성되어야 합니다.")
        private String phone;

        @NotBlank(message = "이름은 공백이 아니어야 합니다.")
        private String name;

        @NotBlank
        private String password;
    }

    @Getter
    @AllArgsConstructor
    public static class Patch{
        @Setter
        private long memberId;
        private String phone;
        private String name;
        private Member.MemberStatus memberStatus;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response{
        @Setter
        private long memberId;
        private String name;
        private String email;
        private String phone;
        private Member.MemberStatus memberStatus;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
    }
}
