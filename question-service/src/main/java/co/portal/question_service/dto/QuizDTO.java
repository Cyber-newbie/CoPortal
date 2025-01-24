package co.portal.question_service.dto;

import co.portal.question_service.entity.Question;
import lombok.*;

import javax.validation.constraints.Null;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizDTO {

        private int id;

        private String title;

        private String Description;

        private String maxMarks;

        private String numberOfQuestions;

        private LocalDateTime deadline;

        private LocalTime timeLimit;

        private long userId;

        private boolean active;

        private int totalAttempts;

        @Null
        private Object Category;

        @Null
        private List<Question> questions;

}
