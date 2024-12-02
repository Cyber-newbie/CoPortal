package paybank.astro.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "submission")
@Getter
@Setter
@NoArgsConstructor

public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    @JsonBackReference
    private Quiz quiz;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private int score;
    private LocalDateTime submissionTime;

    public Submission( int score, LocalDateTime submissionTime) {
        this.score = score;
        this.submissionTime = submissionTime;
    }

    // Getters and Setters
}

