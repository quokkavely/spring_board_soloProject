package com.springboot.questionPost.entity;
import com.springboot.answer.entity.AnswerPost;
import com.springboot.audit.Auditable;
import com.springboot.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@NoArgsConstructor
@Getter
@Setter
@Entity
public class QuestionPost extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long postId;

    @Column(nullable = false)
    private String title;

    @Column
    private String content;

    @Setter
    @Column(nullable = false)
    private boolean displayNew = true;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int viewCount;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int likeCount;

    @OneToOne(mappedBy = "questionPost",cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private AnswerPost answerPost;

    @ManyToOne
    @JoinColumn(name="MEMBER_ID")
    private Member member;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private QuestionStatus questionStatus= QuestionStatus.QUESTION_REGISTERED;

    @Enumerated(value = EnumType.STRING)
    @Column
    private OpenStatus openStatus = OpenStatus.PUBLIC;

    public enum QuestionStatus{
        QUESTION_REGISTERED,
        QUESTION_ANSWERED,
        QUESTION_DELETED,
        QUESTION_DEACTIVED;
    }

    public enum OpenStatus{
        PUBLIC,
        PRIVATE;

        @Getter
        @Setter
        private String status;
    }

    public void setMember(Member member){
        this.member = member;

        if( !member.getQuestionPosts().contains(this)) {
            member.getQuestionPosts().add(this);
        }
    }
    public void setAnswerPost(AnswerPost answerPost){
        this.answerPost = answerPost;

        if( answerPost.getQuestionPost()!=this) {
            answerPost.setQuestionPost(this);
        }
    }
}
