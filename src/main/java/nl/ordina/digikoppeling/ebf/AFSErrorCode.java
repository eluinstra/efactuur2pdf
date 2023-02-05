/*
 * Copyright 2020 Ordina
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.ordina.digikoppeling.ebf;

public enum AFSErrorCode
{
	AFS100("AFS100", "Het verzoek voldoet niet aan de koppelvlakspecificaties en kan hierdoor niet door de Procesinfrastructuur worden verwerkt."),
	AFS400(
			"AFS400",
			"Er is een technische fout in de afleverservice van de Procesinfrastructuur opgetreden. Probeer het later opnieuw of neem contact op met de beheerder van de Procesinfrastructuur.");

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
