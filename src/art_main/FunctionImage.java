package art_main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

/**
 * FunctionImages are objects that can produce images using mathematical functions. The images 
 * can mate with each other and produce new images.
 * 
 * @author Mikko Hilpinen
 * @since 25.9.2014
 */
public class FunctionImage
{
	// ATTRIBUTES	----------------------------------------------
	
	private Function[] rgbFunctions;
	private FunctionImage mother, father;
	private int childrenKilled, childrenSpawned, fitnessBoost;
	
	
	// CONSTRUCTOR	----------------------------------------------
	
	/**
	 * Creates a new FunctionImage based on three given functions
	 * 
	 * @param redFunction The function that will produce the red colour
	 * @param greenFunction The function that will produce the green colour
	 * @param blueFunction The function that will produce the blue colour
	 */
	public FunctionImage(Function redFunction, Function greenFunction, Function blueFunction)
	{
		// Initializes attributes
		this.mother = null;
		this.father = null;
		this.childrenKilled = 0;
		this.childrenSpawned = 0;
		this.fitnessBoost = 0;
		
		this.rgbFunctions = new Function[3];
		this.rgbFunctions[0] = redFunction;
		this.rgbFunctions[1] = greenFunction;
		this.rgbFunctions[2] = blueFunction;
	}

	/**
	 * Creates a new functionImage using randomly generated, rather simple functions
	 * 
	 * @param maxParameterAmount How many parameters are given to the functions when they are 
	 * in use
	 */
	public FunctionImage(int maxParameterAmount)
	{
		// Initializes attributes
		this.mother = null;
		this.father = null;
		this.childrenKilled = 0;
		this.childrenSpawned = 0;
		
		this.rgbFunctions = new Function[3];
		this.rgbFunctions[0] = 
				SimpleFunctionGenerator.createSimpleFunction(maxParameterAmount, null);
		this.rgbFunctions[1] = 
				SimpleFunctionGenerator.createSimpleFunction(maxParameterAmount, null);
		this.rgbFunctions[2] = 
				SimpleFunctionGenerator.createSimpleFunction(maxParameterAmount, null);
	}
	
	
	// OTHER METHODS	------------------------------------------------------
	
	/**
	 * Simplifies the image to it's most simple form (some information is lost)
	 */
	public void simplify()
	{
		for (int i = 0; i < this.rgbFunctions.length; i++)
		{
			this.rgbFunctions[i].simplify();
		}
	}
	
	/**
	 * This makes the image more fit when compared to others
	 */
	public void boost()
	{
		this.fitnessBoost = 25;
	}
	
	/**
	 * @return Should the image be able to die due to low fitness
	 */
	public boolean canDie()
	{
		// Images need to create a certain amount of children before they may die
		return (this.childrenSpawned >= 5);
	}
	
	/**
	 * @return How fit the image is, fitness depends on how many direct children have survived 
	 * the selection process
	 */
	public int getFitness()
	{
		// If no children have been born yet, has full fitness
		if (this.childrenSpawned == 0)
			return 100 + this.fitnessBoost;
		
		// Otherwise returns a percentage of survived children
		return 100 - (100 * this.childrenKilled / this.childrenSpawned) + this.fitnessBoost;
	}
	
	/**
	 * This method should be called when the image doesn't survive the selection process
	 */
	public void kill()
	{
		// Informs the parents that their child was killed
		if (this.mother != null)
			this.mother.childrenKilled ++;
		if (this.father != null)
			this.father.childrenKilled ++;
		
		this.mother = null;
		this.father = null;
	}
	
	/**
	 * @return how complex the image is (as in how many terms / functions are used in it)
	 */
	public int getComplexity()
	{
		int complexity = 3;
		for (int i = 0; i < 3; i++)
		{
			complexity += this.rgbFunctions[i].getSubFunctionAmount();
		}
		
		return complexity;
	}
	
	/**
	 * @return A copy of this functionImage
	 */
	public FunctionImage createCopy()
	{
		return new FunctionImage(this.rgbFunctions[0].createCopy(), 
				this.rgbFunctions[1].createCopy(), this.rgbFunctions[2].createCopy());
	}
	
	/**
	 * Calculates an rgb value for corresponding to the given parameters
	 * 
	 * @param args The parameters used for calculating the rgb values
	 * @return An rgb value calculated for the given parameters.
	 */
	public int getRGB(double[] args)
	{
		int[] rgb = new int[3];
		for (int i = 0; i < 3; i++)
		{
			rgb[i] = (int) this.rgbFunctions[i].getValue(args) % 255;
			
			if (rgb[i] < 0)
				rgb[i] *= -1;
		}
		
		return new Color(rgb[0], rgb[1], rgb[2]).getRGB();
	}
	
	/**
	 * Creates a new child with the father image
	 * @param father The father image that will affect the child
	 * @return The child created by this image and the father image
	 */
	public FunctionImage createChild(FunctionImage father)
	{
		Random random = new Random();
		FunctionImage child = createCopy();
		
		// Any colour function may mate with any colour function, most likely with 
		// the same colour. Father's functions act as the father most of the time
		for (int i = 0; i < 3; i++)
		{
			// Sometimes the functions won't mate at all
			if (random.nextDouble() < 0.2)
				continue;
			
			// Usually the father is of the same colour as the mother
			int fatherColour = i;
			if (random.nextDouble() < 0.4)
				fatherColour = random.nextInt(3);
			
			// Usually the function mother is from the image mother and father from image father
			Function functionMother = null;
			Function functionFather = null;
			if (random.nextDouble() < 0.8)
			{
				functionMother = this.rgbFunctions[i];
				functionFather = father.rgbFunctions[fatherColour];
			}
			else
			{
				functionMother = father.rgbFunctions[i];
				functionFather = this.rgbFunctions[fatherColour];
			}
			
			// The child image will use the produced child function to calculate the colour
			child.rgbFunctions[i] = functionMother.createChild(functionFather);
		}
		
		// Informs the child about their parents
		child.father = father;
		child.mother = this;
		// Counts the amount of created children
		this.childrenSpawned ++;
		father.childrenSpawned ++;
		
		return child;
	}
	
	/**
	 * Mutates the functions randomly
	 */
	public void mutate()
	{
		// Mutates all of the functions
		for (int i = 0; i < 3; i++)
		{
			this.rgbFunctions[i].mutate();
			// If the function grew upwards, goes to the top
			this.rgbFunctions[i] = this.rgbFunctions[i].getTopFunction();
		}
	}
	
	/**
	 * Creates a new function image as a child for the two function images
	 * 
	 * @param image1 The first image parent
	 * @param image2 The second image parent
	 * @return A child created by the two parents
	 */
	/*
	private static FunctionImage createChild(FunctionImage image1, FunctionImage image2)
	{
		// Randomly decides, which image will be ther mother
		Random random = new Random();
		FunctionImage mother = image1;
		FunctionImage father = image2;
		
		if (random.nextDouble() < 0.5)
		{
			mother = image2;
			father = image1;
		}
		
		// Produces a child using the parents
		return mother.createChild(father);
	}
	*/
	
	/**
	 * Creates a set of children from a set of parents
	 * 
	 * @param parents The parents that will produce the children
	 * @param childAmount How many children will be created
	 * @return The children created by the parents
	 */
	public static ArrayList<FunctionImage> createChildren(ArrayList<FunctionImage> parents, 
			int childAmount)
	{
		// Creates new children from random parents until enough children have been produced
		Random random = new Random();
		ArrayList<FunctionImage> children = new ArrayList<FunctionImage>();
		int childrenCreated = 0;
		
		// A singular image can't create children (because I say so)
		if (parents.size() < 2)
		{
			System.err.println("Can't create children if there aren't two or more parents");
			return null;
		}
		
		while (childrenCreated < childAmount)
		{
			FunctionImage mother = parents.get(random.nextInt(parents.size()));
			FunctionImage father = parents.get(random.nextInt(parents.size()));
			
			if (mother == null || father == null)
				System.err.println("Mother or father is NULL!");
			
			// An image can't mate with itself
			if (mother.equals(father))
				continue;
			
			children.add(mother.createChild(father));
			childrenCreated ++;
		}
		
		return children;
	}
}
