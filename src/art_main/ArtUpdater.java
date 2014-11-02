package art_main;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;

import genesis_logic.AdvancedKeyListener;
import omega_world.Area;
import omega_world.GameObject;

/**
 * ArtUpdater lets the user pick some of the created images and generates a new generation 
 * based on those.
 * 
 * @author Mikko Hilpinen
 * @since 26.9.2014
 */
public class ArtUpdater extends GameObject implements AdvancedKeyListener
{
	// ATTRIBUTES	--------------------------------------------------------
	
	private ArrayList<FunctionImage> parents;
	private ArrayList<FunctionImage> children;
	private FunctionDrawer[] drawers;
	private int rows, columns;
	private boolean active;
	
	
	// CONSTRUCTOR	---------------------------------------------------------
	
	/**
	 * Creates a new ArtUpdater that will automatically generate the first generation of 
	 * function images
	 * 
	 * @param rows How many rows the of images there will be
	 * @param columns How many columns of images there will be
	 * @param area The area where the images will be drawn
	 */
	public ArtUpdater(int rows, int columns, Area area)
	{
		super(area);
		
		// Initializes attributes
		this.rows = rows;
		this.columns = columns;
		
		if (this.rows < 1)
			this.rows = 1;
		if (this.columns < 1)
			this.columns = 1;
		
		this.parents = new ArrayList<FunctionImage>();
		this.children = new ArrayList<FunctionImage>();
		this.drawers = new FunctionDrawer[this.rows * this.columns];
		int w = 1360 / this.columns;
		int h = 768 / this.rows;
		this.active = true;
		
		for (int i = 0; i < this.drawers.length; i++)
		{
			int x = w / 2 + (i % (this.columns)) * w;
			int y = h / 2 + i / this.columns * h;
			
			System.out.println("Creates a drawer to (" + x + ", " + y + ")");
			
			this.children.add(new FunctionImage(2));
			this.drawers[i] = new FunctionDrawer(x, y, w, h, this.children.get(i), this, area);
		}
		
		// Adds the object to the handler(s)
		area.getKeyHandler().addKeyListener(this);
	}
	
	
	// IMPLEMENTED METHODS	-----------------------------------------------

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
	public void onKeyDown(char key, int keyCode, boolean coded, double steps)
	{
		// Does nothing
	}

	@Override
	public void onKeyPressed(char key, int keyCode, boolean coded)
	{
		// When a key is pressed, creates a new generation of images
		createNextGeneration();
	}

	@Override
	public void onKeyReleased(char key, int keyCode, boolean coded)
	{
		// Does nothing
	}

	
	// OTHER METHODS	----------------------------------------------------
	
	/**
	 * The updater reacts to situations when the user clicks (kills) a drawer / an image by 
	 * removing that image from play.
	 * 
	 * @param drawer The drawer that was clicked.
	 */
	public void killDrawer(FunctionDrawer drawer)
	{
		// Removes the image (child) and temporarily disables the drawer
		this.children.remove(drawer.getImage());
		drawer.getImage().kill();
		drawer.setInvisible();
		drawer.inactivate();
	}
	
	private void createNextGeneration()
	{
		// The previous children reach maturity
		this.parents.addAll(this.children);
		
		// Creates the new children
		this.children = FunctionImage.createChildren(this.parents, this.drawers.length);
		
		System.out.println("-----------------------");
		
		// Updates the drawers
		for (int drawerIndex = 0; drawerIndex < this.drawers.length; drawerIndex++)
		{
			// Also mutates and simplifies the functionImage before use
			FunctionImage image = this.children.get(drawerIndex);
			image.mutate();
			image.simplify();
			
			FunctionDrawer drawer = this.drawers[drawerIndex];
			drawer.setImage(image);
			drawer.setVisible();
			drawer.activate();
			
			System.out.println("Child complexity: " + image.getComplexity());
		}
		
		// Removes some of the parents if there are too many
		removeOverPopulation(30);
	}
	
	private void removeOverPopulation(int maximumPopulation)
	{
		int killAmount = this.parents.size() - maximumPopulation;
		
		// If there is not too many parents, no one has to die
		if (killAmount <= 0)
			return;
		
		// Finds the least fit set of parents
		int killableFit = 1000;
		int imagesFound = 0;
		LinkedList<FunctionImage> toBeKilled = new LinkedList<FunctionImage>();
		
		for (FunctionImage image : this.parents)
		{
			// Some images can't be killed yet
			if (!image.canDie())
				continue;
			
			// First, adds any function that can be killed
			if (imagesFound < killAmount)
			{	
				toBeKilled.add(image);
				imagesFound ++;
			}
			// Otherwise checks if the image is unfit enough to be killed
			else
			{
				// The first set of images needs to be sorted into fitness order
				if (imagesFound == killAmount)
				{
					toBeKilled.sort(new FitnessComparator());
					// Also calculates the fitness limit
					killableFit = toBeKilled.getLast().getFitness();
					
					// Doesn't count the images anymore
					imagesFound ++;
				}
				
				if (image.getFitness() < killableFit)
				{
					// Removes the most fit
					toBeKilled.removeLast();
					// Adds the new image to the list of killed images
					toBeKilled.add(image);
					// Calculates the new standard
					toBeKilled.sort(new FitnessComparator());
					killableFit = toBeKilled.getLast().getFitness();
				}
			}
		}
		
		// Removes the chosen parents from the population
		for (FunctionImage image : toBeKilled)
		{
			this.parents.remove(image);
		}
	}
	
	
	// SUBCLASSES	-------------------------------------------------------
	
	private static class FitnessComparator implements Comparator<FunctionImage>
	{
		@Override
		public int compare(FunctionImage first, FunctionImage second)
		{
			return first.getFitness() - second.getFitness();
		}	
	}
}
