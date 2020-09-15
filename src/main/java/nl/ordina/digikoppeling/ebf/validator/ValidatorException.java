package nl.ordina.digikoppeling.ebf.validator;

public class ValidatorException extends Exception
{
	private static final long serialVersionUID = 1L;

	public ValidatorException(String message)
	{
		super(message);
	}

	public ValidatorException(String message, Exception cause)
	{
		super(message,cause);
	}

	public ValidatorException(Throwable exception)
	{
		super(exception);
	}

}
