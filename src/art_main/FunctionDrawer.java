package art_main;

import genesis_graphic.DepthConstants;
import genesis_logic.AdvancedMouseListener;

import java.awt.Graphics2D;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;

import omega_gameplay.CollisionType;
import omega_graphic.DimensionalDrawnObject;
import omega_world.Area;

/**
 * FunctionDrawer draws a function visualization on screen
 * 
 * @author Mikko Hilpinen
 * @since 25.9.2014
 */
public class FunctionDrawer extends DimensionalDrawnObject implements AdvancedMouseListener
{
	// ATTRIBUTES	-------------------------------------------------------
	
	private int width, height;
	private BufferedImage image;
	private FunctionImage functionImage;
	private boolean active;
	private ArtUpdater updater;
	
	
	// CONSTRUCTOR	-------------------------------------------------------
	
	/**
	 * Creates a new FunctionDrawer
	 * 
	 * @param x The x-coordinate of the visualization's center
	 * @param y The y-coordinate of the visualization's center
	 * @param width The width of the visualization (in pixels)
	 * @param height The height of the visualization (in pixels)
	 * @param image The functionImage that will be visualized
	 * @param updater The artUpdater that is interested about clicked drawers
	 * @param area The area where the visualization will be drawn
	 */
	public FunctionDrawer(int x, int y, int width, int height, FunctionImage image, 
			ArtUpdater updater, Area area)
	{
		super(x, y, DepthConstants.NORMAL, false, CollisionType.BOX, area);
		
		// Initializes attributes
		this.width = width;
		this.height = height;
		this.image = new BufferedImage(this.width, this.height, 
				BufferedImage.TYPE_INT_RGB);
		this.functionImage = image;
		this.active = true;
		this.updater = updater;
		
		updatePixels();
		
		// Adds the drawer to the handler(s)
		area.getMouseHandler().addMouseListener(this);
	}
	
	
	// IMPLEMENTED METHODS	------------------------------------------------

	@Override
	public void drawSelfBasic(Graphics2D g2d)
	{
		g2d.drawImage(this.image, 0, 0, null);
		g2d.drawRect(0, 0, this.width, this.height);
	}

	@Override
	public int getOriginX()
	{
		return this.width / 2;
	}

	@Override
	public int getOriginY()
	{
		return this.height / 2;
	}
	
	@Override
	public Class<?>[] getSupportedListenerClasses()
	{
		return null;
	}

	@Override
	public int getHeight()
	{
		return this.height;
	}

	@Override
	public int getWidth()
	{
		return this.width;
	}
	
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
	public MouseButtonEventScale getCurrentButtonScaleOfInterest()
	{
		return MouseButtonEventScale.LOCAL;
	}
	
	@Override
	public boolean listensMouseEnterExit()
	{
		// TODO: Add some mouse hover effect
		return false;
	}

	@Override
	public boolean listensPosition(Double testedPosition)
	{
		return pointCollides(testedPosition);
	}

	@Override
	public void onMouseButtonEvent(MouseButton button,
			MouseButtonEventType eventType, Double mousePosition,
			double eventStepTime)
	{
		// Informs the artUpdater about the event
		if (eventType == MouseButtonEventType.PRESSED && this.updater != null)
		{
			if (button == MouseButton.LEFT)
				this.updater.killDrawer(this);
			else
				this.functionImage.boost();
			// TODO: Add a visual effect to boost
		}
	}

	@Override
	public void onMouseMove(Double newMousePosition)
	{
		// Not interested
	}

	@Override
	public void onMousePositionEvent(MousePositionEventType eventType,
			Double mousePosition, double eventStepTime)
	{
		// Not interested (yet)
	}
	
	
	// GETTERS & SETTERS	----------------------------------------------
	
	/**
	 * Changes the functionImage visualized by the drawer
	 * @param image The functionImage that will be visualized by the drawer
	 */
	public void setImage(FunctionImage image)
	{
		this.functionImage = image;
		updatePixels();
	}
	
	/**
	 * @return The function visualized by this drawer
	 */
	public FunctionImage getImage()
	{
		return this.functionImage;
	}

	
	// OTHER METHODS	--------------------------------------------------
	
	private void updatePixels()
	{
		//System.out.println("Updating drawer at x=" + getX() + ", y=" + getY());
		
		for (int x = 0; x < this.width; x++)
		{
			for (int y = 0; y < this.height; y++)
			{
				// TODO: Add other parameters when necessary
				double[] args = {x, y};
				this.image.setRGB(x, y, this.functionImage.getRGB(args));
			}
		}
	}
}
