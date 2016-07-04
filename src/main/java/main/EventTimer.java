package main;

public class EventTimer implements Runnable {
	
	GameEvents event;
	long duration;
	long startTimer;
	Thread thread;
	boolean hasStarted = false;
	boolean isDone  = false;
	int code;
	
	
	public EventTimer(long duration, int eventCode, GameEvents event) {
		this.event = event;
		this.duration = duration;
		code = eventCode;
	}
	
	public void start(){
		if(hasStarted)return;
		thread = new Thread(this);
		hasStarted = true;
		thread.start();
	}
	
	public void reset(){
		hasStarted = false;
	}

	@Override
	public void run() {
		startTimer = System.nanoTime();
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		switch(code){
		case GameEvents.END_GAME:
			event.endGame();
			break;
		case GameEvents.WAVE_START:
			event.startWave();
			break;
		case GameEvents.SLOW:
			event.slowGame();
			break;
		case GameEvents.HIDE_PLAYER:
			event.hidePlayer();
			break;
		case GameEvents.PLAYER_RECOVERY:
			event.finishRecovery();
			break;
		}
		
		
		//event.startWave();
	}

	public boolean hasStarted() {
		
		return hasStarted;
	}

	public boolean isDone() {
		
		return isDone;
	}

	public long getDuration() {
		return duration;
	}

	public double getTimePassed() {
		
		return (System.nanoTime() - startTimer) / 1000000;
	}
}
