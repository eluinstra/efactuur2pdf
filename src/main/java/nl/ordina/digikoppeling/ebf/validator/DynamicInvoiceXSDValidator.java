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
package nl.ordina.digikoppeling.ebf.validator;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;
import nl.clockwork.efactuur.VersionHelper;
import nl.clockwork.efactuur.VersionNotFoundException;
import nl.ordina.digikoppeling.ebf.model.MessageVersion;
import org.apache.commons.io.input.BOMInputStream;
import org.xml.sax.SAXException;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class DynamicInvoiceXSDValidator
{
	@NonNull
	VersionHelper versionResolver;

	public void validate(byte[] content, MessageVersion messageType) throws ValidatorException
	{
		getXsdFile(messageType).ifPresent(xsdFile -> validate(xsdFile, content));
	}

	private Optional<String> getXsdFile(MessageVersion messageType) throws ValidationException
	{
		try
		{
			return versionResolver.getXsdPath(messageType.getType(), messageType.getFormat(), messageType.getVersion());
		}
		catch (VersionNotFoundException e)
		{
			throw new ValidationException(e);
		}
	}

	private void validate(String xsdFile, byte[] content) throws ValidationException
	{
		try
		{
			val schema = getSchema(xsdFile);
			val source = new StreamSource(new BOMInputStream(new ByteArrayInputStream(content)));
			schema.newValidator().validate(source);
		}
		catch (SAXException | IOException e)
		{
			throw new ValidationException(e);
		}
	}

	private Schema getSchema(String xsdFile) throws SAXException
	{
		val factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		val source = new StreamSource(getClass().getResourceAsStream(xsdFile), getClass().getResource(xsdFile).toString());
		return factory.newSchema(source);
	}
}
