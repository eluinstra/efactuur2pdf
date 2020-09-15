package nl.ordina.digikoppeling.ebf.processor;

public class ParseException extends Exception
{
	private static final long serialVersionUID = 1L;

	public ParseException(String message)
	{
		super(message);
	}

	public ParseException(String message, Exception cause)
	{
		super(message,cause);
	}

	public ParseException(Throwable exception)
	{
		super(exception);
	}

}
