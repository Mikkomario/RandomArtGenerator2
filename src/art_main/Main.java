package art_main;

import java.awt.BorderLayout;

import genesis_graphic.GamePanel;
import genesis_graphic.GameWindow;
import omega_world.AreaRelay;
import arc_bank.MultiMediaHolder;
import arc_bank.OpenGamePhaseBank;
import arc_bank.OpenGamePhaseBankHolder;
import arc_resource.MetaResource;

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
		// Initializes resources
		MultiMediaHolder.initializeResourceDatabase(new OpenGamePhaseBankHolder("gamephaseload.txt"));
		MultiMediaHolder.activateBank(MetaResource.GAMEPHASE, "default", true);
		
		// Creates new GameWindow & Panels
		GameWindow window = new GameWindow(1360, 768, "Random Art Generator", true, 30, 10, 
				false);
		GamePanel panel = new GamePanel(1360, 768);
		//panel.disableClear();
		window.addGamePanel(panel, BorderLayout.CENTER);
		
		// Creates areas
		AreaRelay areaRelay = new AreaRelay(window, panel);
		areaRelay.addArea("art", OpenGamePhaseBank.getGamePhaseBank("default").getPhase("generate"));
		
		// TODO: Starts the game
		//new AutoChildCreator(areaRelay.getArea("art"));
		new ArtUpdater(2, 4, areaRelay.getArea("art"));
		areaRelay.getArea("art").start();
	}
}
