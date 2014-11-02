package art_main;

/**
 * Operators are used for combining functions together
 * 
 * @author Mikko Hilpinen
 * @since 25.9.2014
 */
public interface Operator
{
	/**
	 * Operators give a new value based on two separate values
	 * @param value1 The first value used in the operator 
	 * @param value2 The second value used in the operator
	 * @return a value based on the two values
	 */
	public double getValue(double value1, double value2);
}
