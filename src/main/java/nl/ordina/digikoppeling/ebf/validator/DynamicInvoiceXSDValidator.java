package nl.ordina.digikoppeling.ebf.validator;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import nl.clockwork.efactuur.VersionHelper;
import nl.clockwork.efactuur.VersionNotFoundException;
import nl.ordina.digikoppeling.ebf.model.MessageVersion;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

public class DynamicInvoiceXSDValidator
{
	protected transient Log logger = LogFactory.getLog(this.getClass());
	private VersionHelper versionResolver;

	public void validate(byte[] content, MessageVersion messageType) throws ValidatorException
	{
		try
		{
			String xsdFile = versionResolver.getXsdPath(messageType.getType(),messageType.getFormat(),messageType.getVersion());
			if (!StringUtils.isEmpty(xsdFile))
			{
				StreamSource xmlStream = new StreamSource(new StringReader(new String(content)));
				SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				Schema schema = factory.newSchema(new StreamSource(this.getClass().getResourceAsStream(xsdFile),this.getClass().getResource(xsdFile).toString()));
				schema.newValidator().validate(xmlStream);
			}
		}
		catch (SAXException e)
		{
			throw new ValidationException(e);
		}
		catch (IOException e)
		{
			throw new ValidatorException(e);
		}
		catch (VersionNotFoundException e)
		{
			throw new ValidatorException(e);
		}
	}

	public void setVersionResolver(VersionHelper versionResolver)
	{
		this.versionResolver = versionResolver;
	}

}
