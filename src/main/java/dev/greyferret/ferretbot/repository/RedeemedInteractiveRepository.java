package dev.greyferret.ferretbot.repository;

import dev.greyferret.ferretbot.entity.RedeemedInteractive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RedeemedInteractiveRepository extends JpaRepository<RedeemedInteractive, Long> {
	List<RedeemedInteractive> findByShownFalse();
}
