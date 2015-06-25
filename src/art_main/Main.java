package art_main;

import genesis_event.DrawableHandler;
import genesis_event.HandlerRelay;
import genesis_event.KeyListenerHandler;
import genesis_event.MouseListenerHandler;
import genesis_util.Vector3D;
import genesis_video.GamePanel;
import genesis_video.GameWindow;

/**
 * Main is used for starting the program
 * 
 * @author Mikko Hilpinen
 * @since 25.9.2014
 */
public class Main
{
	// TODO: Sievennys & Fitness boost
	
	// CONSTRUCTOR	------------------------------------------------------------------
	
	private Main()
	{
		// Constructor is hidden since the interface is static
	}

	
	// MAIN METHOD	------------------------------------------
	
	/**
	 * Starts the game
	 * 
	 * @param args Not used
	 */
	public static void main(String[] args)
	{
		// Starts the game
		start();
	}
	
	
	// OTHER METHODS	--------------------------------------
	
	private static void start()
	{	
		// Creates new GameWindow & Panels
		Vector3D resolution = new Vector3D(1360, 768);
		GameWindow window = new GameWindow(resolution, "Random Art Generator", true, 30, 10);
		GamePanel panel = window.getMainPanel().addGamePanel();
		
		// Creates the handlers
		HandlerRelay handlers = new HandlerRelay();
		handlers.addHandler(new MouseListenerHandler(false, window.getHandlerRelay()));
		handlers.addHandler(new KeyListenerHandler(false, window.getHandlerRelay()));
		handlers.addHandler(new DrawableHandler(false, panel.getDrawer()));
		
		new ArtUpdater(resolution, 2, 4, handlers);
	}
}
