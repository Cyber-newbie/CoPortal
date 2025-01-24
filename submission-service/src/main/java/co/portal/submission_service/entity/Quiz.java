package co.portal.submission_service.entity;

import co.portal.submission_service.dto.question.QuestionDTO;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quiz")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String maxMarks;

    @Column
    private LocalDateTime deadline;

    @Column
    private LocalTime timeLimit;

    @Column
    private String numberOfQuestions;

    @Column
    private Boolean active = false;

    @Column
    private int totalAttempts = 1;

    @ManyToOne(fetch = FetchType.EAGER)
    private Category category;

    private long userId;


    @Transient
    private List<QuestionDTO> questions = new ArrayList<>();

    public Quiz(String title, String description, String maxMarks, String numberOfQuestions, Boolean active) {
        this.title = title;
        this.description = description;
        this.maxMarks = maxMarks;
        this.numberOfQuestions = numberOfQuestions;
        this.active = active;
    }

}
