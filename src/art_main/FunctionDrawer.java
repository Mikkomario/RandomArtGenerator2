package art_main;

import genesis_event.Drawable;
import genesis_event.EventSelector;
import genesis_event.HandlerRelay;
import genesis_event.MouseEvent;
import genesis_event.MouseEvent.MouseButton;
import genesis_event.MouseEvent.MouseButtonEventScale;
import genesis_event.MouseEvent.MouseButtonEventType;
import genesis_event.MouseListener;
import genesis_event.StrictEventSelector;
import genesis_util.HelpMath;
import genesis_util.StateOperator;
import genesis_util.Vector3D;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import omega_util.SimpleGameObject;
import omega_util.Transformable;
import omega_util.Transformation;

/**
 * FunctionDrawer draws a function visualization on screen
 * 
 * @author Mikko Hilpinen
 * @since 25.9.2014
 */
public class FunctionDrawer extends SimpleGameObject implements MouseListener, Transformable, 
		Drawable
{
	// ATTRIBUTES	-------------------------------------------------------
	
	private Vector3D dimensions;
	private BufferedImage image;
	private FunctionImage functionImage;
	private ArtUpdater updater;
	private Transformation transformation;
	private EventSelector<MouseEvent> selector;
	private StateOperator visibleOperator;
	
	
	// CONSTRUCTOR	-------------------------------------------------------
	
	/**
	 * Creates a new FunctionDrawer
	 * @param position The absolute location of the visualization's center
	 * @param dimensions The size of the visualization (pixels)
	 * @param image The functionImage that will be visualized
	 * @param updater The artUpdater that is interested about clicked drawers
	 * @param handlers The handlers that will handle this object
	 */
	public FunctionDrawer(Vector3D position, Vector3D dimensions, FunctionImage image, 
			ArtUpdater updater, HandlerRelay handlers)
	{
		super(handlers);
		
		// Initializes attributes
		this.transformation = new Transformation(position);
		this.dimensions = dimensions;
		this.image = new BufferedImage(getDimensions().getFirstInt(), 
				getDimensions().getSecondInt(), BufferedImage.TYPE_INT_RGB);
		this.functionImage = image;
		this.updater = updater;
		this.visibleOperator = new StateOperator(true, true);
		
		StrictEventSelector<MouseEvent, MouseEvent.Feature> selector = 
				MouseEvent.createButtonEventSelector();
		selector.addRequiredFeature(MouseButtonEventScale.LOCAL);
		selector.addRequiredFeature(MouseButtonEventType.PRESSED);
		
		this.selector = selector;
		
		updatePixels();
	}
	
	
	// IMPLEMENTED METHODS	------------------------------------------------

	@Override
	public StateOperator getListensToMouseEventsOperator()
	{
		return getIsActiveStateOperator();
	}

	@Override
	public EventSelector<MouseEvent> getMouseEventSelector()
	{
		return this.selector;
	}

	@Override
	public boolean isInAreaOfInterest(Vector3D position)
	{
		// TODO: Test
		Vector3D relativePosition = 
				getTransformation().inverseTransform(position).plus(getOrigin());
		return HelpMath.pointIsInRange(relativePosition, Vector3D.zeroVector(), getDimensions());
	}


	@Override
	public void onMouseEvent(MouseEvent event)
	{
		// Informs the artUpdater about the event
		if (event.getButton() == MouseButton.LEFT)
			this.updater.killDrawer(this);
		else if (event.getButton() == MouseButton.RIGHT)
			this.functionImage.boost();
	}
	
	@Override
	public void drawSelf(Graphics2D g2d)
	{
		AffineTransform lastTransform = g2d.getTransform();
		getTransformation().transform(g2d);
		
		int x = -getOrigin().getFirstInt();
		int y = -getOrigin().getSecondInt();
		
		g2d.drawImage(this.image, x, y, null);
		g2d.drawRect(x, y, getDimensions().getFirstInt(), getDimensions().getSecondInt());
		
		g2d.setTransform(lastTransform);
	}

	@Override
	public int getDepth()
	{
		return 0;
	}

	@Override
	public StateOperator getIsVisibleStateOperator()
	{
		return this.visibleOperator;
	}

	@Override
	public Transformation getTransformation()
	{
		return this.transformation;
	}


	@Override
	public void setTrasformation(Transformation t)
	{
		this.transformation = t;
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
	
	/**
	 * @return The size of the visualized area
	 */
	public Vector3D getDimensions()
	{
		return this.dimensions;
	}
	
	/**
	 * @return The visualization's origin's relative position
	 */
	public Vector3D getOrigin()
	{
		return getDimensions().dividedBy(2);
	}

	
	// OTHER METHODS	--------------------------------------------------
	
	private void updatePixels()
	{
		for (int x = 0; x < getDimensions().getFirst(); x++)
		{
			for (int y = 0; y < getDimensions().getSecond(); y++)
			{
				// TODO: Add other parameters when necessary
				double[] args = {x, y};
				this.image.setRGB(x, y, this.functionImage.getRGB(args));
			}
		}
	}
}
