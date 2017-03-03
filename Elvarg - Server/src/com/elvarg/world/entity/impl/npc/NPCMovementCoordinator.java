package com.elvarg.world.entity.impl.npc;

import com.elvarg.util.Misc;
import com.elvarg.world.collision.region.RegionClipping;
import com.elvarg.world.entity.combat.CombatFactory;
import com.elvarg.world.model.Position;
import com.elvarg.world.model.movement.RS317PathFinder;

/**
 * Will make all {@link NPC}s set to coordinate, pseudo-randomly move within a
 * specified radius of their original position.
 * 
 * @author lare96
 */
public class NPCMovementCoordinator {

	/** The npc we are coordinating movement for. */
	private NPC npc;

	/** The coordinate state this npc is in. */
	private CoordinateState coordinateState;

	public enum CoordinateState {
		HOME,
		AWAY,
		RETREATING;
	}

	public NPCMovementCoordinator(NPC npc) {
		this.npc = npc;
		this.coordinateState = CoordinateState.HOME;
	}

	public void onTick() {

		//If walk radius is 0, that means the npc shouldn't walk around.
		//HOWEVER: Only if npc is home. Because the npc might be retreating 
		//from a fight.
		if(npc.getDefinition().getWalkRadius() == 0) {
			if (coordinateState == CoordinateState.HOME) {
				return;
			}
		}

		updateCoordinator();

		switch (coordinateState) {
		case HOME:

			if(CombatFactory.inCombat(npc)) {
				return;
			}

			if (npc.getMovementQueue().isMovementDone()) {
				if (Misc.getRandom(10) <= 1) {
					Position pos = generateLocalPosition();
					if(pos != null) {
						npc.getMovementQueue().walkStep(pos.getX(), pos.getY());
					}
				}
			}

			break;
		case RETREATING:
		case AWAY:
			RS317PathFinder.findPath(npc, npc.getSpawnPosition().getX(), npc.getSpawnPosition().getY(), true, 1, 1);
			break;
		}
	}

	public void updateCoordinator() {

		/**
		 * Handle retreating from combat.
		 */

		if(CombatFactory.inCombat(npc)) {
			if(coordinateState == CoordinateState.AWAY) {
				coordinateState = CoordinateState.RETREATING;
			}
			if(coordinateState == CoordinateState.RETREATING) {
				if(npc.getPosition().equals(npc.getSpawnPosition())) {
					coordinateState = CoordinateState.HOME;
				}
				npc.getCombat().reset();
			}
			return;
		}

		int deltaX;
		int deltaY;

		if(npc.getSpawnPosition().getX() > npc.getPosition().getX()) {
			deltaX = npc.getSpawnPosition().getX() - npc.getPosition().getX();
		} else {
			deltaX = npc.getPosition().getX() - npc.getSpawnPosition().getX();
		}

		if(npc.getSpawnPosition().getY() > npc.getPosition().getY()) {
			deltaY = npc.getSpawnPosition().getY() - npc.getPosition().getY();
		} else {
			deltaY = npc.getPosition().getY() - npc.getSpawnPosition().getY();
		}

		int radius = npc.getDefinition().getWalkRadius();	

		if((deltaX > radius) || (deltaY > radius)) {
			coordinateState = CoordinateState.AWAY;
		} else { 
			coordinateState = CoordinateState.HOME;
		}
	}

	private Position generateLocalPosition() {
		int dir = -1;
		int x = 0, y = 0;
		if (!RegionClipping.blockedNorth(npc.getPosition()))
		{
			dir = 0;
		}
		else if (!RegionClipping.blockedEast(npc.getPosition()))
		{
			dir = 4;
		}
		else if (!RegionClipping.blockedSouth(npc.getPosition()))
		{
			dir = 8;
		}
		else if (!RegionClipping.blockedWest(npc.getPosition()))
		{
			dir = 12;
		}
		int random = Misc.getRandom(3);

		boolean found = false;

		if (random == 0)
		{
			if (!RegionClipping.blockedNorth(npc.getPosition()))
			{
				y = 1;
				found = true;
			}
		}
		else if (random == 1)
		{
			if (!RegionClipping.blockedEast(npc.getPosition()))
			{
				x = 1;
				found = true;
			}
		}
		else if (random == 2)
		{
			if (!RegionClipping.blockedSouth(npc.getPosition()))
			{
				y = -1;
				found = true;
			}
		}
		else if (random == 3)
		{
			if (!RegionClipping.blockedWest(npc.getPosition()))
			{
				x = -1;
				found = true;
			}
		}
		if (!found)
		{
			if (dir == 0)
			{
				y = 1;
			}
			else if (dir == 4)
			{
				x = 1;
			}
			else if (dir == 8)
			{
				y = -1;
			}
			else if (dir == 12)
			{
				x = -1;
			}
		}
		if(x == 0 && y == 0)
			return null;
		int spawnX = npc.getSpawnPosition().getX();
		int spawnY = npc.getSpawnPosition().getY();
		if (x == 1) {
			if (npc.getPosition().getX() + x > spawnX + 1)
				return null;
		}
		if (x == -1) {
			if (npc.getPosition().getX() - x < spawnX - 1)
				return null;
		}
		if (y == 1) {
			if (npc.getPosition().getY() + y > spawnY + 1)
				return null;
		}
		if (y == -1) {
			if (npc.getPosition().getY() - y < spawnY - 1)
				return null;
		}
		return new Position(x, y);
	}

	public void setCoordinateState(CoordinateState coordinateState) {
		this.coordinateState = coordinateState;
	}

	public CoordinateState getCoordinateState() {
		return coordinateState;
	}
}