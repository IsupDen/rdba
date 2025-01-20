package ru.isupden.vpnbot.db.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.isupden.vpnbot.db.entity.MetadataEntity;

public interface MetadataRepository extends JpaRepository<MetadataEntity, UUID>  {
}
