package nl.ordina.digikoppeling.ebf.model;

public class EBFError
{
	protected String foutcode;
	protected String foutbeschrijving;

	public EBFError()
	{
	}

	public EBFError(final String foutcode, final String foutbeschrijving)
	{
		this.foutcode = foutcode;
		this.foutbeschrijving = foutbeschrijving;
	}

	public String getFoutcode()
	{
		return foutcode;
	}

	public void setFoutcode(String foutcode)
	{
		this.foutcode = foutcode;
	}

	public String getFoutbeschrijving()
	{
		return foutbeschrijving;
	}

	public void setFoutbeschrijving(String foutbeschrijving)
	{
		this.foutbeschrijving = foutbeschrijving;
	}

	@Override
	public String toString()
	{
		return "EBFError [foutcode=" + foutcode + ", foutbeschrijving=" + foutbeschrijving + "]";
	}

}
