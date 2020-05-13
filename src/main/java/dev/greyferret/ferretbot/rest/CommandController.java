package dev.greyferret.ferretbot.rest;

import dev.greyferret.ferretbot.entity.RedeemedInteractive;
import dev.greyferret.ferretbot.repository.RedeemedInteractiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/interactive")
public class CommandController {
	@Autowired
	private RedeemedInteractiveRepository redeemedInteractiveRepository;

	@GetMapping("/redeem")
	protected ResponseEntity<List<RedeemedInteractive>> getRedeemed() {
		List<RedeemedInteractive> redeemedInteractive = redeemedInteractiveRepository.findByShownFalse();
		List<RedeemedInteractive> res = new ArrayList<>();
		for (RedeemedInteractive ri : redeemedInteractive) {
			ri.setShown(true);
			redeemedInteractiveRepository.save(ri);
			res.add(ri);
		}
		return ResponseEntity.ok(res);
	}

	@GetMapping("/last")
	protected ResponseEntity<Page<RedeemedInteractive>> last(Pageable pageable) {
		Page<RedeemedInteractive> pageRes = redeemedInteractiveRepository.findAll(pageable);
		return ResponseEntity.ok(pageRes);
	}
}
