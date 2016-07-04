package main;

public interface GameEvents {
	
	
	public int WAVE_START =0;
	public int END_GAME = 1;
	public int SLOW = 2;
	public int HIDE_PLAYER = 3;
	public int PLAYER_RECOVERY = 4;
	public void startWave();
	public void endGame();
	public void slowGame();
	public void hidePlayer();
	public void finishRecovery();	
	
}
