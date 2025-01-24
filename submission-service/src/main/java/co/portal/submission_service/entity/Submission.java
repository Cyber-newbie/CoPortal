package co.portal.submission_service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "submission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "quiz_id")
    private int quizId;

    @JoinColumn(name = "user_id")
    private long userId;

    @Column
    private int score;

    @Column
    private LocalDateTime submissionTime;

    @Column(name = "time_taken")
    private LocalTime timeTaken;

    public Submission(int score, LocalDateTime submissionTime) {
        this.score = score;
        this.submissionTime = submissionTime;
    }

    // Getters and Setters
}

