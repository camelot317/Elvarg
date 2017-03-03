package com.elvarg.engine;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;

import com.elvarg.engine.task.TaskManager;
import com.elvarg.world.World;
import com.elvarg.world.content.clan.ClanChatManager;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * The engine which processes the game.
 * 
 * @author lare96
 * @author Swiffy
 */
public final class GameEngine implements Runnable {

	private final ScheduledExecutorService logicService = GameEngine.createLogicService();   
	private boolean PACKETS_PROCESS;
	
	@Override
	public void run() {
		try {
			
			switch(next()) {
			case PACKET_PROCESSING:
				World.getPlayers().forEach($it -> $it.getSession().handleQueuedPackets(true));
				break;
			case GAME_PROCESSING:
				TaskManager.sequence();
				World.sequence();
				break;
			}
			
		} catch (Throwable e) {
			e.printStackTrace();
			World.savePlayers();
			ClanChatManager.save();
		}
	}

	private EngineState next() {
		
		if(PACKETS_PROCESS) {
			PACKETS_PROCESS = false;
			return EngineState.PACKET_PROCESSING;
		}
		
		PACKETS_PROCESS = true;
		return EngineState.GAME_PROCESSING;
	}

	private enum EngineState {
		PACKET_PROCESSING,
		GAME_PROCESSING;
	}

	public void submit(Runnable t) {
		try {
			logicService.execute(t);
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}

	/** STATIC **/

	public static ScheduledExecutorService createLogicService() {
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
		executor.setRejectedExecutionHandler(new CallerRunsPolicy());
		executor.setThreadFactory(new ThreadFactoryBuilder().setNameFormat("LogicServiceThread").build());
		executor.setKeepAliveTime(45, TimeUnit.SECONDS);
		executor.allowCoreThreadTimeOut(true);
		return Executors.unconfigurableScheduledExecutorService(executor);
	}
}
