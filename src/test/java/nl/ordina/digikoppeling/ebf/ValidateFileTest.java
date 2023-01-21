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

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.stream.Stream;
import javax.xml.transform.TransformerConfigurationException;
import lombok.val;
import nl.clockwork.efactuur.Constants;
import nl.clockwork.efactuur.Constants.MessageFormat;
import nl.clockwork.efactuur.Constants.MessageType;
import nl.ordina.digikoppeling.ebf.model.MessageVersion;
import nl.ordina.digikoppeling.ebf.validator.ValidatorException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ValidateFileTest
{
	@DisplayName("Validate valid invoices")
	@ParameterizedTest
	@MethodSource("validInvoices")
	void validateValidInvoice(String invoice) throws ValidatorException, TransformerConfigurationException
	{
		assertThatCode(() -> new ValidateFile().validate(invoice.getBytes())).as("Validation").doesNotThrowAnyException();
	}

	private static Stream<Arguments> validInvoices() throws URISyntaxException, IOException
	{
		return Stream.of(arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/nlcius/NLCIUS e-factuur vb 01.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/nlcius/NLCIUS e-factuur vb 02.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/nlcius/NLCIUS e-factuur vb 04.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/nlcius/NLCIUS e-factuur vb 03a.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/nlcius/NLCIUS e-factuur vb 03b.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/nlcius/NLCIUS e-factuur vb 03c.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/nlcius/NLCIUS e-factuur vb 05.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/nlcius/NLCIUS e-factuur vb 06.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/nlcius/NLCIUS e-factuur vb 07.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/nlcius/NLCIUS e-factuur vb 08.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/nlcius/NLCIUS e-factuur vb 09.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/nlcius/NLCIUS e-factuur vb 10.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-NL-Invoice-1.0-Example00.xml")),
				// arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-NL-Invoice-1.0-Example01.xml")),
				// arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-NL-Invoice-1.0-Example02.xml")),
				// arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-NL-Invoice-1.0-Example03.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-1.6.2-Example07.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-1.6.2-Example14.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-1.6.3-Example07.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-1.6.3-Example14.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-1.7-Example07.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-1.7-Example14.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-1.7c-Example07.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-1.7c-Example14.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-1.8-Example07.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-1.8-Example14.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-1.8-beta2-Example07.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-1.8-beta2-Example14.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-1.9-Example07.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-1.9-Example14.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-NL-Invoice-1.9-Example07.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-NL-Invoice-1.9-Example08.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-NL-Invoice-1.9-Example09.xml")),
				arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/ubl/UBL-Invoice-2.0-Example.xml")));
	}

	@DisplayName("Validate invalid invoice")
	@ParameterizedTest
	@MethodSource("invalidInvoices")
	void validateInvalidInvoice(String invoice) throws ValidatorException, TransformerConfigurationException
	{
		assertThatCode(() -> new ValidateFile().validate(invoice.getBytes())).as("Validation").hasMessageContaining("UBL-CUSTOM-01");
	}

	private static Stream<Arguments> invalidInvoices() throws URISyntaxException, IOException
	{
		return Stream.of(arguments(WithFile.readFile("nl/ordina/digikoppeling/ebf/nlcius/NLCIUS e-factuur vb 01b.xml")));
	}

}
