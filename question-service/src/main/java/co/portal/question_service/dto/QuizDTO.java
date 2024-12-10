package co.portal.question_service.dto;

import co.portal.question_service.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

        private boolean active;

//        private List<Question> qusetions;

}
