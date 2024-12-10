package co.portal.quiz_service.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @ManyToOne(fetch = FetchType.EAGER)
    private Category category;

    private long userId;

    public Quiz(String title, String description, String maxMarks, String numberOfQuestions, Boolean active) {
        this.title = title;
        this.description = description;
        this.maxMarks = maxMarks;
        this.numberOfQuestions = numberOfQuestions;
        this.active = active;
    }

}
