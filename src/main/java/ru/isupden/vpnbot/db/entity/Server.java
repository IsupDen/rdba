package ru.isupden.vpnbot.db.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Data
@Entity
@Table(name = "servers")
@NoArgsConstructor
public class Server {
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotNull
    @Column(nullable = false)
    private String ip;

    @NotNull
    @Column(nullable = false)
    private Integer usersNumber;
}
