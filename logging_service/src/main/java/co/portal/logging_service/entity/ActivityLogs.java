package co.portal.logging_service.entity;


import io.hypersistence.utils.hibernate.type.json.JsonType;
import lombok.*;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDate;

@Table(name = "activity_logs")
@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(name = "json", typeClass = JsonType.class)
public class ActivityLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String username;

    @Column
    private String statusCode;

    @Column(columnDefinition = "json")
    private String requestBody;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private String responseBody;

    @Column(nullable = false)
    private String requestURI;

    @Column(nullable = false)
    private LocalDate timestamp;

    @Column(name = "ip_address")
    private String ipAddress;



}
