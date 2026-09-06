package dev.opencloud.domain.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "client_support")
@Entity
@NoArgsConstructor
public class ClientSupport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Long clientId;

    private SupportType type = SupportType.DEPLOYMENT;

    private String title;

    private String description;

    private String status;

    private Date createdAt = new Date();

    private Date solvedAt = new Date();

    enum SupportType {
        DEPLOYMENT,
        GITHUB,
        BITBUCKET,
        SERVER,
        BUILD,
        SECURITY,
        OTHER
    }
}
