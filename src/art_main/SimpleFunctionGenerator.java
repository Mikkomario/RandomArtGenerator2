package art_main;

import java.util.Random;

/**
 * SimpleFunctionGenerator creates very simple functions that may be used in more complex 
 * functions.
 * 
 * @author Mikko Hilpinen
 * @since 25.9.2014
 */
public class SimpleFunctionGenerator
{	
	// ATTRIBUTES	--------------------------------------------
	
	private static final double[] niceConstants = {1, 0, 0.5, 3.0 / 2.0, Math.PI, Math.E};
	private static final double[] niceRandomMultipliers = {-1, 100, 1, 10, -100, -10};
	
	
	// CONSTRUCTOR	--------------------------------------------
	
	private SimpleFunctionGenerator()
	{
		// The constructor is hidden since the interface is static
	}

	
	// OTHER METHODS	----------------------------------------
	
	/**
	 * @param maxParameterAmount How many parameters are provided when the function is used
	 * @param parent The function that will hold the generated function. Null if the generated 
	 * function is supposed to be individual.
	 * @return A randomly generated simple function
	 */
	public static Function createSimpleFunction(int maxParameterAmount, ComplexFunction parent)
	{
		Random random = new Random();
		
		// 50% Creates a parameter function
		if (random.nextDouble() < 0.5)
			return new ParameterFunction(random.nextInt(maxParameterAmount), 
					FunctionModifier.getRandomModifier(), parent);
		
		// Otherwise it's a constant function
		double chosen = random.nextDouble();
		double constant = 0;
		
		// may be a random number
		if (chosen < 0.5)
			constant = random.nextDouble() * 
				niceRandomMultipliers[random.nextInt(niceRandomMultipliers.length)];
		// Or a nice default
		else
			constant = niceConstants[random.nextInt(niceConstants.length)];
			
		return new ConstantFunction(constant, FunctionModifier.getRandomModifier(), parent);
	}
	
	
	// SUBCLASSES	---------------------------------------------
	
	/**
	 * ConstantFunction is a simple function that doesn't depend on parameters
	 * 
	 * @author Mikko Hilpinen
	 * @since 28.9.2014
	 */
	public static class ConstantFunction extends Function
	{
		// ATTRIBUTES	-----------------------------------------
		
		private double value;
		
		
		// CONSTRUCTOR	-----------------------------------------
		
		/**
		 * Creates a new constantFunction
		 * 
		 * @param constant The constant used in the function
		 * @param modifier How the constant is modified
		 * @param parent Which function this function is part of
		 */
		public ConstantFunction(double constant, FunctionModifier modifier, 
				ComplexFunction parent)
		{
			super(modifier, parent);
			
			// Initializes attributes
			this.value = constant;
		}
		
		
		// IMPLEMENTED METHODS	---------------------------------
		
		@Override
		public double getValueWithoutModification(double[] args)
		{
			return this.value;
		}

		@Override
		protected int getSubFunctionAmount()
		{
			return 0;
		}

		@Override
		public Function createCopy()
		{
			return new ConstantFunction(this.value, getModifier(), null);
		}
		
		@Override
		public void mutate()
		{
			// There is a chance that the constant value mutates somehow
			Random random = new Random();
			
			if (random.nextDouble() < 0.5)
			{
				if (random.nextDouble() < 0.25)
					this.value *= -1;
				else
					this.value *= 0.75 + 0.5 * random.nextDouble();
			}
			else
				super.mutate();
		}

		@Override
		public void simplify()
		{
			// Constant functions can't be made more simple
		}

		@Override
		protected boolean dependsOnParameters()
		{
			return false;
		}
	}
	
	private static class ParameterFunction extends Function
	{
		// ATTRIBUTES	------------------------------------------
		
		private int parameterIndex;
		
		
		// CONSTRUCTOR	-----------------------------------------
		
		public ParameterFunction(int parameterIndex, FunctionModifier modifier, 
				ComplexFunction parent)
		{
			super(modifier, parent);
			
			// Initializes attributes
			this.parameterIndex = parameterIndex;
			
			if (this.parameterIndex < 0)
				this.parameterIndex = 0;
		}
		
		
		// IMPLEMENTED METHODS	----------------------------------
		
		@Override
		public double getValueWithoutModification(double[] args)
		{
			// Checks the parameter availability
			if (this.parameterIndex >= args.length)
			{
				System.err.println("Can't retrieve the " + this.parameterIndex + 
						". parameter from the given parameter list");
				return 0;
			}
			
			return args[this.parameterIndex];
		}

		@Override
		protected int getSubFunctionAmount()
		{
			return 0;
		}

		@Override
		public Function createCopy()
		{
			return new ParameterFunction(this.parameterIndex, getModifier(), null);
		}

		@Override
		public void simplify()
		{
			// Parameter functions can't be made more simple
		}

		@Override
		protected boolean dependsOnParameters()
		{
			return true;
		}
	}
}
