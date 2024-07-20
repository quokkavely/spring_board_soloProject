package com.springboot.member.entity;

import com.springboot.audit.Auditable;
import com.springboot.likes.LikePost;
import com.springboot.questionPost.entity.QuestionPost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Setter
public class Member extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long memberId;

    @OneToMany(mappedBy = "member")
    private List<QuestionPost> questionPosts=new ArrayList<>();


    @Column(length=30, nullable = false,updatable = false, unique = true)
    private String email;

    @Setter
    @Column(length=30,nullable = false)
    private String name;

    @Setter
    @Column(length=13, nullable = false, unique = true)
    private String phone;

    @Enumerated(value=EnumType.STRING)
    @Column(length=20, nullable = false)
    @Setter
    private MemberStatus memberStatus=MemberStatus.MEMBER_ACTIVE;

    @Enumerated(value=EnumType.STRING)
    @Column
    private UserRole userRole =UserRole.USER_ROLE_USER;


    public enum MemberStatus {
        MEMBER_ACTIVE,
        MEMBER_SLEEP,
        MEMBER_QUIT;

        @Getter
        @Setter
        private String status;
    }
    public enum UserRole{
        USER_ROLE_USER,
        USER_ROLE_ADMIN;

        @Getter
        @Setter
        private String userRole;
    }

    public Member(String email, String name, String phone) {
        this.email = email;
        this.name = name;
        this.phone = phone;
    }

    public void setQuestionPosts(QuestionPost questionPost){
        questionPosts.add(questionPost);
        if(questionPost.getMember()!=this){
            questionPost.setMember(this);
        }
    }
}
