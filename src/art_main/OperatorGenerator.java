package art_main;

import java.util.Random;

/**
 * OperatorGenerator is used for generating different operators
 * 
 * @author Mikko Hilpinen
 * @since 25.9.2014
 */
public class OperatorGenerator
{
	// CONSTRUCTOR	-------------------------------------------------------
	
	private OperatorGenerator()
	{
		// The constructor is hidden since the interface is static
	}
	
	
	// OTHER METHODS	------------------------------------------
	
	/**
	 * @return A randomly generated operator
	 */
	public static Operator createRandomOperator()
	{
		Random random = new Random();
		int chosen = random.nextInt(6);
		
		if (chosen < 1)
			return new AdditionOperator(1);
		if (chosen < 2)
			return new AdditionOperator(-1);
		if (chosen < 3)
			return new MultiplicationOperator();
		if (chosen < 4)
			return new DivisionOperator();
		if (chosen < 5)
			return new PowerOperator();
		
		return new ModuloOperator();
	}

	
	// SUBCLASSES	---------------------------------------------
	
	private static class AdditionOperator implements Operator
	{
		// ATTRIBUTES	-----------------------------------------
		
		private int sign;
		
		
		// CONSTRUCTOR	----------------------------------------
		
		public AdditionOperator(int sign)
		{
			// Initializes attributes
			this.sign = sign;
		}
		
		
		// IMPLEMENTED METHODS	--------------------------------
		
		@Override
		public double getValue(double value1, double value2)
		{
			return value1 + this.sign * value2;
		}
	}
	
	private static class MultiplicationOperator implements Operator
	{
		@Override
		public double getValue(double value1, double value2)
		{
			return value1 * value2;
		}	
	}
	
	private static class DivisionOperator implements Operator
	{
		@Override
		public double getValue(double value1, double value2)
		{
			return value1 / value2;
		}
	}
	
	private static class PowerOperator implements Operator
	{
		@Override
		public double getValue(double value1, double value2)
		{
			return Math.pow(value1, value2);
		}	
	}
	
	private static class ModuloOperator implements Operator
	{
		@Override
		public double getValue(double value1, double value2)
		{
			return value1 % value2;
		}
	}
}
