package com.hwans.screenshareserver.entity.sharing;

import com.hwans.screenshareserver.common.security.jwt.JwtTokenProvider;
import com.hwans.screenshareserver.entity.BaseEntity;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tb_sharing_channel")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SharingChannel extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;
    @Column(length = 64)
    private String password;
    @OneToOne
    @JoinColumn(name = "host_user_id")
    @Setter
    private SharingUser host;
}
