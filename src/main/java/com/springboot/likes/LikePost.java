package com.springboot.likes;

import com.springboot.audit.Auditable;
import com.springboot.member.entity.Member;
import com.springboot.questionPost.entity.QuestionPost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class LikePost extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long likeId;

    @ManyToOne
    @JoinColumn(name="MEMBER_ID")
    private Member member;

    @ManyToOne
    @JoinColumn(name="POST_ID")
    private QuestionPost questionPost;

    public LikePost(Member member,QuestionPost questionPost) {
        this.questionPost = questionPost;
        this.member = member;
    }
}
