package dev.greyferret.ferretbot.service;

import dev.greyferret.ferretbot.entity.GameVoteBonusVote;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface GameVoteBonusVoteService extends CrudRepository<GameVoteBonusVote, Long> {
	List<GameVoteBonusVote> findByTextChannelId(Long addChannelId);
}
