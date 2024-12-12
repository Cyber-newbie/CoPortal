package co.portal.question_service.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class QuizQuestionId implements Serializable {

    private int quiz_id;
    private int questions_id;

    // Parameterized constructor
    public QuizQuestionId(int quiz_id, int questions_id) {
        this.quiz_id = quiz_id;
        this.questions_id = questions_id;
    }

    // Override equals and hashCode for composite key comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuizQuestionId that = (QuizQuestionId) o;
        return quiz_id == that.quiz_id && questions_id == that.questions_id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(quiz_id, questions_id);
    }
}
