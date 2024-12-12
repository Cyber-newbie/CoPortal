package co.portal.question_service.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "quiz_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(QuizQuestionId.class)
public class QuizQuestion {

    @Id
    @Column
    private int quiz_id;

    @Id
    @Column
    private int questions_id;

}
