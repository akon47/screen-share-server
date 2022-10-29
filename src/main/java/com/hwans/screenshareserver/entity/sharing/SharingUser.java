package com.hwans.screenshareserver.entity.sharing;

import com.hwans.screenshareserver.entity.BaseEntity;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "tb_sharing_user")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SharingUser extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;
    @Column(length = 32)
    private String role;
    @Column(length = 255)
    @Setter
    private String token;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    @Setter
    private SharingChannel channel;
}
