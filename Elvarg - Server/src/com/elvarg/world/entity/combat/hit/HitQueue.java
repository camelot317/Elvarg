package com.elvarg.world.entity.combat.hit;

import java.util.ArrayList;
import java.util.List;

import com.elvarg.world.entity.combat.CombatFactory;

public class HitQueue {

	// Our hits list. Containing all our hits to be processed.
	private List<QueueableHit> hits = new ArrayList<>();

	// A list containing hits which should be removed from our hits list
	private List<QueueableHit> toRemove = new ArrayList<>();

	// A list containing hits which should be added to our hits list
	private List<QueueableHit> toAdd = new ArrayList<>();

	public void process() {

		// Remove queued hits
		for (QueueableHit hit : toRemove) {
			hits.remove(hit);
		}

		// Add queued hits
		for (QueueableHit hit : toAdd) {
			hits.add(hit);
		}

		// Clear other lists
		toRemove.clear();
		toAdd.clear();

		// Process the actual hits list now.
		for (QueueableHit hit : hits) {

			if (hit == null) {
				toRemove.add(hit);
				continue;
			}

			if (hit.decrementAndGetDelay() <= 0) {
				CombatFactory.handleQueuedHit(hit);
				toRemove.add(hit);
			}
		}
	}

	public void append(QueueableHit c_h) {
		toAdd.add(c_h);
	}
}
