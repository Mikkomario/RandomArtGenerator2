package art_main;

import java.util.Random;

/**
 * Functions are mathematical entities that return a value when called. Functions support 
 * a certain number of parameters.
 * 
 * @author Mikko Hilpinen
 * @since 25.9.2014
 */
public abstract class Function
{
	// ATTRIBUTES	-------------------------------------------------
	
	private FunctionModifier modifier;
	private ComplexFunction parent;
	
	
	// CONSTRUCTOR	-------------------------------------------------
	
	/**
	 * Creates a new function. The given modifier affects the function
	 * 
	 * @param modifier The modifier used for adjusting the function's value
	 * @param parent The function that this function is part of. 
	 * Null if this function isn't part of any other function.
	 */
	public Function(FunctionModifier modifier, ComplexFunction parent)
	{
		// Initializes attributes
		this.modifier = modifier;
		this.parent = parent;
	}
	
	
	// ABSTRACT METHODS	------------------------------------------------
	
	/**
	 * A function should be able to return a value. The value may or may not be based on the 
	 * given parameters. This value shouldn't be affected by the function's modifier.
	 * @param args The arguments used as a part of the functions
	 * @return A value that may or may or be based on the given arguments
	 */
	protected abstract double getValueWithoutModification(double[] args);
	
	/**
	 * @return How many other functions this function holds
	 */
	protected abstract int getSubFunctionAmount();
	
	/**
	 * Creates an exact copy of this function. The copy can't retain its parents, however. 
	 * At least it shouldn't have the same parent as the called function.
	 * @return A copy of this function
	 */
	public abstract Function createCopy();
	
	/**
	 * Simplifies the function to its most simple form
	 */
	public abstract void simplify();
	
	/**
	 * @return Are parameters required for calculating the function's value
	 */
	protected abstract boolean dependsOnParameters();
	
	
	// GETTERS & SETTERS	-------------------------------------------
	
	/**
	 * @return The function that holds this function. Null if this function isn't inside 
	 * another function
	 */
	public ComplexFunction getParent()
	{
		return this.parent;
	}
	
	/**
	 * Moves the function under another function
	 * @param newParent The new function that will hold this function
	 */
	public void setParent(ComplexFunction newParent)
	{
		this.parent = newParent;
	}
	
	/**
	 * @return The modifier used for modifying this function's values
	 */
	protected FunctionModifier getModifier()
	{
		return this.modifier;
	}
	
	
	// OTHER METHODS	-----------------------------------------------
	
	/**
	 * A function should be able to return a value. The value may or may not be based on the 
	 * given parameters. This value is affected by the function's modifier as well.
	 * @param args The arguments used as a part of the functions
	 * @return A value that may or may or be based on the given arguments
	 */
	public double getValue(double[] args)
	{
		return this.modifier.modify(getValueWithoutModification(args));
	}
	
	/**
	 * @return The parent function that is not part of any other function
	 */
	public Function getTopFunction()
	{
		Function top = this;
		
		while (top.getParent() != null)
			top = top.getParent();
		
		return top;
	}
	
	/**
	 * Creates a child based on the "mother" and "father" functions. The relationship is 
	 * not symmetric, fathers affect the child in ways mothers do not and other way around. 
	 * The called function will act as the mother. The mother won't be affected by this 
	 * function call.
	 * 
	 * @param father The father function used for creating the child (won't be modified)
	 * @return A new function that is created from the two functions.
	 */
	public Function createChild(Function father)
	{
		Random random = new Random();
		double chosen = random.nextDouble();
		
		// 33% The child only resembles the father
		if (chosen < 0.33)
			return father.createCopy();
		// 33% The child (only) gets the father's function modifier
		else if (chosen < 0.66)
		{
			Function child = createCopy();
			child.modifier = father.modifier;
			return child;
		}
		// 33% Creates a new function that has both functions combined
		else
			return new ComplexFunction(createCopy(), 
					father.createCopy(), 
					OperatorGenerator.createRandomOperator(), 
					FunctionModifier.getRandomModifier(), null);
	}
	
	/**
	 * Mutation may change the function's structure
	 */
	public void mutate()
	{
		Random random = new Random();
		
		// If the function is light, it may create a new simple function beside it
		if (random.nextDouble() < (1 - getSubFunctionAmount() / 150.0) * 0.3)
			new ComplexFunction(this, SimpleFunctionGenerator.createSimpleFunction(2, null), 
					OperatorGenerator.createRandomOperator(), 
					FunctionModifier.getRandomModifier(), null);
		// A function's modifier may also change randomly
		else if (random.nextDouble() < 0.05)
			this.modifier = FunctionModifier.getRandomModifier();
	}
}
