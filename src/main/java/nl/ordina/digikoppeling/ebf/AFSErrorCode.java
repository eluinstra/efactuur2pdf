package nl.ordina.digikoppeling.ebf;

public enum AFSErrorCode
{
	AFS100("AFS100","Het verzoek voldoet niet aan de koppelvlakspecificaties en kan hierdoor niet door de Procesinfrastructuur worden verwerkt."),
	AFS400("AFS400","Er is een technische fout in de afleverservice van de Procesinfrastructuur opgetreden. Probeer het later opnieuw of neem contact op met de beheerder van de Procesinfrastructuur.");

	private final String foutCode;
	private final String foutBeschrijving;

	AFSErrorCode(String foutCode, String foutBeschrijving)
	{
		this.foutCode = foutCode;
		this.foutBeschrijving = foutBeschrijving;
	}

	public final String foutCode()
	{
		return foutCode;
	}

	public final String foutBeschrijving()
	{
		return foutBeschrijving;
	}
}
