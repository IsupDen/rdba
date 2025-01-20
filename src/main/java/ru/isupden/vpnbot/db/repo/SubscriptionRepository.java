package ru.isupden.vpnbot.db.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.isupden.vpnbot.db.entity.SubscriptionPlan;

@Repository
public interface SubscriptionRepository extends JpaRepository<SubscriptionPlan, UUID> {
}
