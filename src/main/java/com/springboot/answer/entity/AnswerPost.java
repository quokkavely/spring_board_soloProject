package com.springboot.answer.entity;

import com.springboot.audit.Auditable;
import com.springboot.questionPost.entity.QuestionPost;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class AnswerPost extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long answerId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String replyContent;

    @OneToOne//(cascade = CascadeType.MERGE)
    @JoinColumn(name = "POST_ID")
    private QuestionPost questionPost;

    @Enumerated(value = EnumType.STRING)
    @Column
    private QuestionPost.OpenStatus openStatus;



    public AnswerPost(String title, String replyContent) {
        this.title = title;
        this.replyContent = replyContent;
    }

    public void setQuestionPost(QuestionPost questionPost){
        this.questionPost=questionPost;
        if(questionPost.getAnswerPost()!=this){
            questionPost.setAnswerPost(this);
        }
    }


//    public enum OpenStatus{
//        PUBLIC,
//        PRIVATE;
//
//        @Getter
//        @Setter
//        private String status;
//    }

}
