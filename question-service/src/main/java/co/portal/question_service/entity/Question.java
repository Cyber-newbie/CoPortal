package co.portal.question_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "Question")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String question;

    @Column
    private String answer;

    @Column
    private String image;

    @Column
    private String option1;

    @Column
    private String option2;

    @Column
    private String option3;

    @Column
    private String option4;


    public Question(String question, String answer, String image,
                    String option1, String option2, String option3, String option4) {

        this.question = question;
        this.answer = answer;
        this.image = image;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
    }


}
