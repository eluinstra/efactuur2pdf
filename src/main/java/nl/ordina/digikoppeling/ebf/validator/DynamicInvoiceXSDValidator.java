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

import java.io.IOException;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

import lombok.val;
import nl.clockwork.efactuur.VersionHelper;
import nl.clockwork.efactuur.VersionNotFoundException;
import nl.ordina.digikoppeling.ebf.model.MessageVersion;

public class DynamicInvoiceXSDValidator
{
	private VersionHelper versionResolver;

	public void validate(byte[] content, MessageVersion messageType) throws ValidatorException
	{
		try
		{
			val xsdFile = versionResolver.getXsdPath(messageType.getType(),messageType.getFormat(),messageType.getVersion());
			if (!StringUtils.isEmpty(xsdFile))
			{
				val xmlStream = new StreamSource(new StringReader(new String(content)));
				val factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				val schema = factory.newSchema(new StreamSource(this.getClass().getResourceAsStream(xsdFile),this.getClass().getResource(xsdFile).toString()));
				schema.newValidator().validate(xmlStream);
			}
		}
		catch (SAXException e)
		{
			throw new ValidationException(e);
		}
		catch (VersionNotFoundException | IOException e)
		{
			throw new ValidatorException(e);
		}
	}

	public void setVersionResolver(VersionHelper versionResolver)
	{
		this.versionResolver = versionResolver;
	}

}
