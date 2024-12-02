package paybank.astro.entity;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "role")
@Setter
@NoArgsConstructor
//@ToString
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(nullable = false)
    private String role;

    @ManyToMany( cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "userRole",
            joinColumns = @JoinColumn(name = "roleId"),
            inverseJoinColumns = @JoinColumn(name = "userId")
    )
    @JsonIgnore
    private List<User> users;

    public Roles(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Roles{" +
                "role='" + role + '\'' +
                ", id=" + id +
                '}';
    }
}
