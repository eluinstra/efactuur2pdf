package nl.ordina.digikoppeling.ebf.validator;

public class ValidationException extends ValidatorException
{
	private static final long serialVersionUID = 1L;

	public ValidationException(String message)
	{
		super(message);
	}

	public ValidationException(String message, Exception cause)
	{
		super(message,cause);
	}

	public ValidationException(Throwable exception)
	{
		super(exception);
	}

}
