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
package nl.ordina.digikoppeling.ebf.processor;


import java.io.ByteArrayInputStream;
import java.util.Optional;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import lombok.val;
import nl.clockwork.efactuur.Constants;
import nl.clockwork.efactuur.Constants.MessageFormat;
import nl.clockwork.efactuur.Constants.MessageType;
import nl.ordina.digikoppeling.ebf.model.MessageVersion;
import org.apache.commons.io.input.BOMInputStream;

public class MessageParser
{
	public MessageVersion getMessageVersion(byte[] message) throws ParseException
	{
		val factory = XMLInputFactory.newInstance();
		try
		{
			val reader = factory.createXMLStreamReader(new BOMInputStream(new ByteArrayInputStream(message)));
			readStartElement(reader);
			val messageFormat = getMessageFormat(reader);
			val messageType = getMessageType(reader);
			val messageVersion = getMessageVersion(reader, messageFormat);
			return new MessageVersion(messageType, messageFormat, messageVersion);
		}
		catch (XMLStreamException e)
		{
			throw new ParseException("Could not parse EBF message", e);
		}
	}

	private Constants.MessageFormat getMessageFormat(final XMLStreamReader reader) throws ParseException
	{
		val messageFormat = getMessageFormat(reader.getNamespaceURI());
		if (messageFormat == null)
			throw new ParseException("Could not determine EBF message format");
		return messageFormat;
	}

	private MessageType getMessageType(final XMLStreamReader reader) throws ParseException
	{
		val messageType = getMessageType(reader.getLocalName());
		return messageType.orElseThrow((() -> new ParseException("Could not determine EBF message type")));
	}

	private String getMessageVersion(final XMLStreamReader reader, final Constants.MessageFormat messageFormat) throws ParseException, XMLStreamException
	{
		if (messageFormat.equals(MessageFormat.UBL))
			return getUBLVersion(getCustomizationId(reader));
		else if (messageFormat.equals(MessageFormat.SETU))
			return getSETUVersion(getAdditionalRequirement(reader));
		else
			throw new ParseException("Could not determine EBF message version");
	}

	private MessageFormat getMessageFormat(String namespaceURI)
	{
		if (namespaceURI.startsWith("urn:oasis:names:specification:ubl"))
			return MessageFormat.UBL;
		else if (namespaceURI.startsWith("urn:digi-inkoop:ubl"))
			return MessageFormat.UBL;
		else if (namespaceURI.startsWith("http://www.nltaxonomie.nl/ubl/"))
			return MessageFormat.UBL;
		else if (namespaceURI.equals("http://ns.hr-xml.org/2007-04-15"))
			return MessageFormat.SETU;
		else if (namespaceURI.equals("http://www.openapplications.org/oagis"))
			return MessageFormat.SETU;
		else
			return null;
	}

	private Optional<MessageType> getMessageType(String rootElementName)
	{
		val messageType = Constants.rootTagToMessageType.get(rootElementName);
		return messageType != null ? Optional.of(messageType) : Optional.empty();
	}

	private String getUBLVersion(String idElementText) throws ParseException
	{
		if (idElementText != null)
			return translateUblMajorToSpecificVersion(idElementText);
		else
			throw new ParseException("UBL version not found");
	}

	private String getSETUVersion(String idElementText) throws ParseException
	{
		if (idElementText != null)
			return translateSetuMajorToSpecificVersion(idElementText);
		else
			throw new ParseException("SETU version not found");
	}

	private String translateUblMajorToSpecificVersion(String majorVersion) throws ParseException
	{
		val specificVersion = !majorVersion.equals("") ? Constants.ublMajorVersionToSpecificVersion.get(majorVersion) : null;
		if (specificVersion == null)
			throw new ParseException("Could not determine specific UBL version: " + majorVersion + " not recognized as major version");
		return specificVersion;
	}

	private String translateSetuMajorToSpecificVersion(String majorVersion) throws ParseException
	{
		val specificVersion = !majorVersion.equals("") ? Constants.setuMajorVersionToSpecificVersion.get(majorVersion) : null;
		if (specificVersion == null)
			throw new ParseException("Could not determine specific SETU version: " + majorVersion + " not recognized as major version");
		return specificVersion;
	}

	private String getCustomizationId(XMLStreamReader reader) throws XMLStreamException
	{
		while (reader.hasNext())
		{
			readStartElement(reader);
			if (reader.getLocalName().equals("CustomizationID"))
				return reader.getElementText();
		}
		return null;
	}

	private String getAdditionalRequirement(XMLStreamReader reader) throws XMLStreamException
	{
		while (reader.hasNext())
		{
			readStartElement(reader);
			if (reader.getLocalName().equals("AdditionalRequirement") && reader.getAttributeValue("", "requirementTitle").equals("VersionId"))
				return reader.getElementText();
		}
		return null;
	}

	private void readStartElement(XMLStreamReader reader) throws XMLStreamException
	{
		while (reader.hasNext())
		{
			reader.next();
			if (reader.getEventType() == XMLStreamReader.START_ELEMENT)
				return;
		}
	}
}
