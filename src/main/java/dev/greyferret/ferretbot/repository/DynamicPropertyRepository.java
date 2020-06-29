package dev.greyferret.ferretbot.repository;

import dev.greyferret.ferretbot.entity.DynamicProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DynamicPropertyRepository extends JpaRepository<DynamicProperty, String> {
}
