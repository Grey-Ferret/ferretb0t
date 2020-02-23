package dev.greyferret.ferretbot.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "wasd_messages")
public class WasdMessages implements Serializable {
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue
    @Id
    private Long id;

    @Column(name = "wasd_id")
    private String wasdId;

    @Column(name = "messageType")
    private Integer message_type;

    @Column(name = "author")
    private String author;

    @Column(name = "content", columnDefinition = "text")
    private String content;

    @Column(name = "sended_at")
    private LocalDateTime sendedAt;

    @Column(name = "inserted_at", nullable = false)
    private LocalDateTime insertedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
