package com.example.chat_application.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "chat_requests",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"sender_id", "receiver_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(optional = false)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    public enum RequestStatus {
        PENDING,
        ACCEPTED,
        REJECTED
    }
}