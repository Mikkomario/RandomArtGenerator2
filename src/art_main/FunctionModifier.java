package art_main;

import java.util.Random;

/**
 * FunctionModifiers are additional effects that can be given to a function. They change 
 * a functions behavior somehow.
 * 
 * @author Mikko Hilpinen
 * @since 25.9.2014
 */
public enum FunctionModifier
{
	/**
	 * Sin(x)
	 */
	SIN, 
	/**
	 * Cos(x)
	 */
	COS, 
	/**
	 * Tan(x)
	 */
	TAN, 
	/**
	 * Arcsin(x)
	 */
	ARCSIN, 
	/**
	 * Arcos(x)
	 */
	ARCOS, 
	/**
	 * Arctan(x)
	 */
	ARCTAN, 
	/**
	 * Square root of x
	 */
	SQRT, 
	/**
	 * No modification, x
	 */
	NONE, 
	/**
	 * Cube root of x
	 */
	CBRT;
	
	
	// OTHER METHODS	------------------------------------------
	
	/**
	 * Transforms the value somehow and returns the modified value
	 * 
	 * @param value The value that will be transformed
	 * @return A transformed value
	 */
	public double modify(double value)
	{
		switch (this)
		{
			case SIN: return Math.sin(value);
			case COS: return Math.cos(value);
			case TAN: return Math.tan(value);
			case ARCSIN: return Math.asin(value);
			case ARCOS: return Math.acos(value);
			case ARCTAN: return Math.atan(value);
			case SQRT: return Math.sqrt(value);
			case CBRT: return Math.cbrt(value);
			
			default: return value;
		}
	}
	
	/**
	 * @return A randomly picked function modifier. 
	 * NONE is returned more often than the others.
	 */
	public static FunctionModifier getRandomModifier()
	{
		Random random = new Random();
		
		// 50% No modifier is used
		if (random.nextDouble() < 0.5)
			return NONE;
		
		// Otherwise a random modifier is picked
		return values()[random.nextInt(values().length)];
	}
}
