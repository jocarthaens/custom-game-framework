package game;

// The main game class that runs the game loop

public class Game implements Runnable {
	
	// GAMELOOP
	protected static final long SECOND = 1_000_000_000;
	protected boolean running;
	protected int FPS = 60;
	
	protected long currentDelta = 0;
	
	
	// SYSTEM
	protected final Screen display;
	private Thread thread;
	
	
	
	
	public Game(Screen screen) {
		this.display = screen;
	}
	
	
	
	
	public void setup() {
		
	}
	
	
	
	
	
	public final void startGame() {
		thread = new Thread(this);
		thread.start();
	}

	
	
	@Override
	public void run() {
		//System.out.println("Starting the Game");
		running = true;
		long delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		long updateRate = SECOND/FPS;
		
		while(running == true){
			currentTime = System.nanoTime();
			delta += currentTime - lastTime;
			lastTime = currentTime;
			updateRate = SECOND/FPS;
			
			if (delta >= updateRate) {
				currentDelta = delta;
				update();
				render();
				delta -= updateRate;
			}
		}
		
	}
	
	
	public void update() {
		
	}
	
	public void render() {
		display.render(this);;
	}
	
	
	
	public long getCurrentDelta() {
		return currentDelta;
	}
	
	

}
