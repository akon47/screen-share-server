package com.hwans.screenshareserver.entity.sharing;

import com.hwans.screenshareserver.entity.BaseEntity;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "tb_sharing_message")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SharingMessage extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;
    @Column(length = 2000, nullable = false)
    private String message;
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    @Setter
    private SharingUser author;
    @Column(nullable = false)
    private boolean deleted;

    public UUID getAuthorId() {
        return this.author.getId();
    }
}
