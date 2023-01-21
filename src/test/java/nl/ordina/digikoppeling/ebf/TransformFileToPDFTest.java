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

import static nl.clockwork.efactuur.Constants.MESSAGE_VERSION_UBL_1_1;
import static nl.clockwork.efactuur.Constants.MESSAGE_VERSION_UBL_1_6_2;
import static nl.clockwork.efactuur.Constants.MESSAGE_VERSION_UBL_1_6_3;
import static nl.clockwork.efactuur.Constants.MESSAGE_VERSION_UBL_1_7;
import static nl.clockwork.efactuur.Constants.MESSAGE_VERSION_UBL_1_8;
import static nl.clockwork.efactuur.Constants.MESSAGE_VERSION_UBL_1_8_beta2;
import static nl.clockwork.efactuur.Constants.MESSAGE_VERSION_UBL_1_9;
import static nl.clockwork.efactuur.Constants.MESSAGE_VERSION_UBL_2_0;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;
import lombok.val;
import nl.clockwork.efactuur.Constants.MessageFormat;
import nl.clockwork.efactuur.Constants.MessageType;
import nl.ordina.digikoppeling.ebf.model.MessageVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TransformFileToPDFTest implements WithPdf
{
	@DisplayName("Transform invoices to PDF")
	@ParameterizedTest
	@MethodSource("afleverberichten")
	void transformFileToPDF(String invoice, MessageVersion messageVersion) throws Exception
	{
		val result = new TransformFileToPDF().createPDF(invoice.getBytes(),messageVersion);
		val pdf = loadPdfDocument(result);
		assertThat(pdf.getNumberOfPages()).isEqualTo(1);
		val content = toString(pdf);
		assertThat(content).contains("BerichtSoort: E-Factuur")
				.contains("Berichtnummer: 1")
				.contains("Invoice Formaat: " + messageVersion.getFormat() + messageVersion.getVersion())
				.contains("Ontvangst Digipoort: ")
				.contains(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
		pdf.close();
	}

	private static Stream<Arguments> afleverberichten() throws URISyntaxException, IOException
	{
		return Stream.of(arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/nlcius/NLCIUS e-factuur vb 01.xml"),messageVersion(MESSAGE_VERSION_UBL_2_0)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/nlcius/NLCIUS e-factuur vb 02.xml"),messageVersion(MESSAGE_VERSION_UBL_2_0)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/nlcius/NLCIUS e-factuur vb 04.xml"),messageVersion(MESSAGE_VERSION_UBL_2_0)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/nlcius/NLCIUS e-factuur vb 03a.xml"),messageVersion(MESSAGE_VERSION_UBL_2_0)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/nlcius/NLCIUS e-factuur vb 03b.xml"),messageVersion(MESSAGE_VERSION_UBL_2_0)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/nlcius/NLCIUS e-factuur vb 03c.xml"),messageVersion(MESSAGE_VERSION_UBL_2_0)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/nlcius/NLCIUS e-factuur vb 05.xml"),messageVersion(MESSAGE_VERSION_UBL_2_0)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/nlcius/NLCIUS e-factuur vb 06.xml"),messageVersion(MESSAGE_VERSION_UBL_2_0)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/nlcius/NLCIUS e-factuur vb 07.xml"),messageVersion(MESSAGE_VERSION_UBL_2_0)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/nlcius/NLCIUS e-factuur vb 08.xml"),messageVersion(MESSAGE_VERSION_UBL_2_0)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/nlcius/NLCIUS e-factuur vb 09.xml"),messageVersion(MESSAGE_VERSION_UBL_2_0)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/nlcius/NLCIUS e-factuur vb 10.xml"),messageVersion(MESSAGE_VERSION_UBL_2_0)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-NL-Invoice-1.0-Example00.xml"),messageVersion(MESSAGE_VERSION_UBL_1_1)),
				// arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-NL-Invoice-1.0-Example01.xml"),messageVersion(MESSAGE_VERSION_UBL_1_1)),
				// arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-NL-Invoice-1.0-Example02.xml"),messageVersion(MESSAGE_VERSION_UBL_1_1)),
				// arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-NL-Invoice-1.0-Example03.xml"),messageVersion(MESSAGE_VERSION_UBL_1_1)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-1.6.2-Example07.xml"),messageVersion(MESSAGE_VERSION_UBL_1_6_2)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-1.6.2-Example14.xml"),messageVersion(MESSAGE_VERSION_UBL_1_6_2)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-1.6.3-Example07.xml"),messageVersion(MESSAGE_VERSION_UBL_1_6_3)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-1.6.3-Example14.xml"),messageVersion(MESSAGE_VERSION_UBL_1_6_3)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-1.7-Example07.xml"),messageVersion(MESSAGE_VERSION_UBL_1_7)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-1.7-Example14.xml"),messageVersion(MESSAGE_VERSION_UBL_1_7)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-1.7c-Example07.xml"),messageVersion(MESSAGE_VERSION_UBL_1_7)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-1.7c-Example14.xml"),messageVersion(MESSAGE_VERSION_UBL_1_7)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-1.8-Example07.xml"),messageVersion(MESSAGE_VERSION_UBL_1_8)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-1.8-Example14.xml"),messageVersion(MESSAGE_VERSION_UBL_1_8)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-1.8-beta2-Example07.xml"),messageVersion(MESSAGE_VERSION_UBL_1_8_beta2)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-1.8-beta2-Example14.xml"),messageVersion(MESSAGE_VERSION_UBL_1_8_beta2)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-1.9-Example07.xml"),messageVersion(MESSAGE_VERSION_UBL_1_9)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-1.9-Example14.xml"),messageVersion(MESSAGE_VERSION_UBL_1_9)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-NL-Invoice-1.9-Example07.xml"),messageVersion(MESSAGE_VERSION_UBL_1_9)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-NL-Invoice-1.9-Example08.xml"),messageVersion(MESSAGE_VERSION_UBL_1_9)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-NL-Invoice-1.9-Example09.xml"),messageVersion(MESSAGE_VERSION_UBL_1_9)),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-2.0-Example.xml"),messageVersion(MESSAGE_VERSION_UBL_2_0)));
	}

	private static MessageVersion messageVersion(String messageVersion)
	{
		return MessageVersion.builder().type(MessageType.INVOICE).format(MessageFormat.UBL).version(messageVersion).build();
	}

	@DisplayName("Transform invoice to PDF and validate PDF")
	@Test
	void transformFileToPDFExtended() throws Exception
	{
		val messageVersion = messageVersion(MESSAGE_VERSION_UBL_2_0);
		val result =
				new TransformFileToPDF().createPDF(WithFile.readFile("nl/ordina/digikoppeling/ebf/nlcius/NLCIUS e-factuur vb 01a.xml").getBytes(),messageVersion);
		val pdf = loadPdfDocument(result);
		assertThat(pdf.getNumberOfPages()).isEqualTo(1);
		val content = toString(pdf);
		assertThat(content).contains("BerichtSoort: E-Factuur")
				.contains("Berichtnummer: 1")
				.contains("Invoice Formaat: UBL2.0")
				.contains("Ontvangst Digipoort: ")
				.contains(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
				.contains("Factuurnummer: Factuurscenario1")
				.contains("Factuurdatum: 01-10-2019")
				.contains("Vervaldatum: 20-03-2019")
				.contains("Leverancier B.V.")
				.contains("t.a.v. Piet de leveranciercontactpersoon")
				.contains("tel.: 06-987654321")
				.contains("email: Piet@leveranciercontactpersoon@.nl")
				.contains("Kerkstraat 2")
				.contains("AA, Amsterdam")
				.contains("BTW Nummer: NL8200.98.395.B.01")
				.contains("IBAN: NL02ABNA0123456789")
				.contains("KvK: 32141305")
				.contains("Logius")
				.contains("t.a.v. Jan de klantcontactpersoon")
				.contains("tel.: 06-11223344")
				.contains("email: Jan@klantcontactpersoon.nl")
				.contains("Postbus 96810")
				.contains("2509 JE, Den Haag")
				.contains("NL000000000000")
				.contains("Referentie: 45856478")
				.contains("Betalingskenmerk: 1111452145876521")
				.contains("Factuurdatum: 2019-02-18")
				.contains("1 (Optioneel veld) Item Description factuurregel 1")
				.contains("BTW0%")
				.contains("100,00 22,00 21,00 2.200,00")
				.contains(" (Optioneel veld) Item Description factuurregel 2")
				.contains("BTW 21%")
				.contains("200,00 5,00 21,00 1.000,00")
				.contains("3 (optioneel veld) Artikelbeschrijving factuurregel")
				.contains("3 BTW9%")
				.contains("1,00 50,00 9,00 50,00")
				.contains("3 factuuregels, met 9% en 21% BTW")
				.contains("Omschrijving: 30dagen")
				.contains("Kortingen: -1,00")
				.contains("Toeslagen: 1,00")
				.contains("Totaal ex BTW: 3.250,00")
				.contains("BTW (21,00%) over 3200.00: 672,00")
				.contains("BTW (9,00%) over 50.00: 4,50")
				.contains("Totaal te betalen: 3.926,50");
	}

}
