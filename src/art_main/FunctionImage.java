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
	
	private Function[] functions;
	private FunctionImage mother, father;
	private int childrenKilled, childrenSpawned, fitnessBoost;
	
	
	// CONSTRUCTOR	----------------------------------------------
	
	private FunctionImage(Function[] functions, FunctionImage mother, FunctionImage father)
	{
		// Initializes attributes
		this.mother = mother;
		this.father = father;
		this.childrenKilled = 0;
		this.childrenSpawned = 0;
		this.fitnessBoost = 0;
		
		this.functions = functions;
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
		
		this.functions = new Function[4];
		this.functions[0] = SimpleFunctionGenerator.createSimpleFunction(
				maxParameterAmount, null);
		for (int i = 1; i < this.functions.length; i++)
		{
			// RGB have reference function value as the last parameter
			this.functions[i] = SimpleFunctionGenerator.createSimpleFunction(
					maxParameterAmount + 1, null);
		}
	}
	
	
	// OTHER METHODS	------------------------------------------------------
	
	/**
	 * Simplifies the image to it's most simple form (some information is lost)
	 */
	public void simplify()
	{
		for (int i = 0; i < this.functions.length; i++)
		{
			this.functions[i].simplify();
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
			complexity += this.functions[i].getSubFunctionAmount();
		}
		
		return complexity;
	}
	
	/**
	 * @return A copy of this functionImage
	 */
	public FunctionImage createCopy()
	{
		Function[] copies = new Function[this.functions.length];
		for (int i = 0; i < copies.length; i++)
		{
			copies[i] = this.functions[i].createCopy();
		}
		
		return new FunctionImage(copies, this.mother, this.father);
	}
	
	/**
	 * Calculates an rgb value for corresponding to the given parameters
	 * 
	 * @param args The parameters used for calculating the rgb values
	 * @return An rgb value calculated for the given parameters.
	 */
	public int getRGB(double[] args)
	{
		double[] modifierArgs = new double[args.length + 1];
		for (int i = 0; i < args.length; i++)
		{
			modifierArgs[i] = args[i];
		}
		double referenceValue = this.functions[0].getValue(args) % 255;
		if (referenceValue < 0)
			referenceValue += 255;
		modifierArgs[args.length] = referenceValue;
		
		int[] rgb = new int[3];
		for (int i = 0; i < 3; i++)
		{
			rgb[i] = (int) this.functions[i + 1].getValue(modifierArgs) % 255;
			
			if (rgb[i] < 0)
				rgb[i] += 255;
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
		
		Function[] generatedFunctions = new Function[this.functions.length];
		// The reference function will always be created the same way
		generatedFunctions[0] = this.functions[0].createChild(father.functions[0]);
		
		// Any colour function may mate with any colour function, most likely with 
		// the same colour. Father's functions act as the father most of the time
		for (int i = 1; i < 4; i++)
		{
			// Sometimes the functions won't mate at all (in which case uses the mother function)
			if (random.nextDouble() < 0.2)
				generatedFunctions[i] = this.functions[i].createCopy();
			
			// Usually the father is of the same colour as the mother
			int fatherColour = i;
			if (random.nextDouble() < 0.4)
				fatherColour = 1 + random.nextInt(3);
			
			// Usually the function mother is from the image mother and father from image father
			Function functionMother = null;
			Function functionFather = null;
			if (random.nextDouble() < 0.8)
			{
				functionMother = this.functions[i];
				functionFather = father.functions[fatherColour];
			}
			else
			{
				functionMother = father.functions[i];
				functionFather = this.functions[fatherColour];
			}
			
			// The child image will use the produced child function to calculate the colour
			generatedFunctions[i] = functionMother.createChild(functionFather);
		}
		
		// Counts the amount of created children
		this.childrenSpawned ++;
		father.childrenSpawned ++;
		
		return new FunctionImage(generatedFunctions, this, father);
	}
	
	/**
	 * Mutates the functions randomly
	 */
	public void mutate()
	{
		// Mutates all of the functions
		for (int i = 0; i < 3; i++)
		{
			this.functions[i].mutate();
			// If the function grew upwards, goes to the top
			this.functions[i] = this.functions[i].getTopFunction();
		}
	}
	
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
