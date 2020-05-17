package dev.greyferret.ferretbot.repository;

import dev.greyferret.ferretbot.entity.ChatGiftFluffDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatGiftFluffRepository extends JpaRepository<ChatGiftFluffDto, Long> {
	long count();
	Page<ChatGiftFluffDto> findAll(Pageable pageable);
}
