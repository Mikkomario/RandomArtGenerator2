package art_main;

import omega_world.Area;
import omega_world.GameObject;
import timers.ContinuousTimer;
import timers.TimerEventListener;

/**
 * AutoChildCreator creates new functionImage children at certain intervals and also 
 * visualizes them.
 * 
 * @author Mikko Hilpinen
 * @since 25.9.2014
 * @deprecated ArtUpdater does everything better
 */
public class AutoChildCreator extends GameObject implements TimerEventListener
{
	// ATTRIBUTES	---------------------------------------------
	
	private FunctionImage[] images;
	private FunctionDrawer[] drawers;
	private boolean active;
	private ContinuousTimer timer;
	
	
	// CONSTRUCTOR	---------------------------------------------
	
	/**
	 * Creates a new autoChildCreator that will start creating new generations right away
	 * 
	 * @param area The area where the images will be drawn
	 */
	public AutoChildCreator(Area area)
	{
		super(area);
		
		// Initializes attributes
		this.images = new FunctionImage[8];
		this.drawers = new FunctionDrawer[8];
		int w = 1360 / 4;
		int h = 768 / 2;
		this.active = true;
		
		for (int i = 0; i < this.images.length; i++)
		{
			int x = w / 2 + (i % (this.images.length / 2)) * w;
			int y = h / 2;
			
			if (i >= this.images.length / 2)
			{
				y += h;
			}
			
			System.out.println("Creates a drawer to (" + x + ", " + y + ")");
			
			this.images[i] = new FunctionImage(2);
			this.drawers[i] = new FunctionDrawer(x, y, w, h, this.images[i], null, area);
		}
		
		// Adds a timer
		this.timer = new ContinuousTimer(this, 300, 0, area.getActorHandler());
	}
	
	
	// IMPLEMENTED METHODS	----------------------------------------------

	@Override
	public void activate()
	{
		this.active = true;
	}

	@Override
	public void inactivate()
	{
		this.active = false;
	}

	@Override
	public boolean isActive()
	{
		return this.active;
	}

	@Override
	public void onTimerEvent(int timerid)
	{
		// Updates the functions (creates a new generation)
		/*
		FunctionImage[] nextGen = FunctionImage.createChildren(this.images, this.images.length);
		this.images = nextGen;
		
		for (int i = 0; i < this.images.length; i++)
		{
			this.drawers[i].setImage(this.images[i]);
			System.out.println("Child complexity: " + this.images[i].getComplexity());
		}
		*/
	}
	
	@Override
	public void kill()
	{
		// Also kills the timer
		this.timer.kill();
		super.kill();
	}
}
