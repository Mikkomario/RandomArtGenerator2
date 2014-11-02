package art_main;

import java.util.Random;

import art_main.SimpleFunctionGenerator.ConstantFunction;

/**
 * ComplexFunctions use two functions in order to calculate their value
 * 
 * @author Mikko Hilpinen
 * @since 25.9.2014
 */
public class ComplexFunction extends Function
{	
	// ATTRIBUTES	-----------------------------------------------------
	
	private Function term1;
	private Function term2;
	private Operator operator;
	
	
	// CONSTRUCTOR	-----------------------------------------------------
	
	/**
	 * Creates a new complex function that uses the given parameter functions and operators
	 * 
	 * @param term1 The first term of the function
	 * @param term2 The second term of the function
	 * @param operator The operator used for combining the terms
	 * @param modifier The modifier that is used for modifying the function's value
	 * @param parent The function that holds this function
	 */
	public ComplexFunction(Function term1, Function term2, Operator operator, 
			FunctionModifier modifier, ComplexFunction parent)
	{
		super(modifier, parent);
		
		// Initializes attributes
		this.term1 = term1;
		this.term2 = term2;
		this.operator = operator;
		
		// Informs the terms that they are now under this function
		term1.setParent(this);
		term2.setParent(this);
	}
	
	
	// IMPLEMENTED METHODS	-------------------------------------------
	
	@Override
	protected double getValueWithoutModification(double[] args)
	{
		return this.operator.getValue(this.term1.getValue(args), 
				this.term2.getValue(args));
	}

	@Override
	protected int getSubFunctionAmount()
	{
		return 2 + this.term1.getSubFunctionAmount() + this.term2.getSubFunctionAmount();
	}

	@Override
	public ComplexFunction createCopy()
	{
		return new ComplexFunction(this.term1.createCopy(), this.term2.createCopy(), 
				this.operator, getModifier(), null);
	}
	
	@Override
	public void simplify()
	{
		// TODO: Make DRY
		
		// If the subFunctions don't depend on parameters, they can be made into simple 
		// functions. Otherwise they must be simplified in another way
		if (!this.term1.dependsOnParameters())
			this.term1 = new ConstantFunction(
					this.term1.getValueWithoutModification(new double[0]), 
					this.term1.getModifier(), this);
		else
			this.term1.simplify();
		
		if (!this.term2.dependsOnParameters())
			this.term2 = new ConstantFunction(
					this.term2.getValueWithoutModification(new double[0]), 
					this.term2.getModifier(), this);
		else
			this.term2.simplify();
	}

	@Override
	protected boolean dependsOnParameters()
	{
		// returns if either one of the function's subFunctions depends on parameters
		return this.term1.dependsOnParameters() || this.term2.dependsOnParameters();
	}
	
	@Override
	public Function createChild(Function father)
	{
		// There is a chance that only a function on the lower level will be affected by 
		// the father
		Random random = new Random();
		int subFunctionAmount = getSubFunctionAmount();
		
		if (random.nextDouble() < 1 - (1.0 / subFunctionAmount))
		{
			ComplexFunction child = createCopy();
			
			// The change can affect the first term
			if (random.nextDouble() < (this.term2.getSubFunctionAmount() + 1.0) / 
					subFunctionAmount)
				child.setTerm1(child.term1.createChild(father));
			// Or the second term
			else
				child.setTerm2(child.term2.createChild(father));
		}
		
		// Otherwise creates the child like any other function
		return super.createChild(father);
	}
	
	@Override
	public void mutate()
	{
		Random random = new Random();
		
		// Complex functions may transform their terms into simple functions
		if (random.nextDouble() < getSubFunctionAmount() / 150.0)
		{
			if (random.nextDouble() < 0.5)
				setTerm1(SimpleFunctionGenerator.createSimpleFunction(2, this));
			else
				setTerm2(SimpleFunctionGenerator.createSimpleFunction(2, this));
		}
		else
			// They can also mutate normally
			super.mutate();
		
		// Complex functions also mutate their terms
		this.term1.mutate();
		this.term2.mutate();
	}
	
	
	// OTHER METHODS	-----------------------------------------------
	
	private void setTerm1(Function newTerm)
	{
		this.term1.setParent(null);
		this.term1 = newTerm;
		this.term1.setParent(this);
	}
	
	// TODO: Make this DRY
	private void setTerm2(Function newTerm)
	{
		this.term2.setParent(null);
		this.term2 = newTerm;
		this.term2.setParent(this);
	}
}
