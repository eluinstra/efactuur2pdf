package nl.ordina.digikoppeling.ebf.validator;

import javax.xml.transform.TransformerException;

import nl.clockwork.efactuur.VersionHelper;
import nl.clockwork.efactuur.VersionNotFoundException;
import nl.ordina.digikoppeling.ebf.model.MessageVersion;
import nl.ordina.digikoppeling.ebf.transformer.XSLTransformer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DynamicInvoiceGenericodeValidator
{
	protected transient Log logger = LogFactory.getLog(this.getClass());
	private VersionHelper versionResolver;

	public void validate(byte[] xml, MessageVersion messageType) throws ValidatorException
	{
		XSLTransformer transformer = null;
		try
		{
			String xslFile = versionResolver.getGenericodeXslPath(messageType.getType(),messageType.getFormat(),messageType.getVersion());
			if (!StringUtils.isEmpty(xslFile))
			{
				transformer = XSLTransformer.getInstance(xslFile);
				String result = transformer.transform(new String(xml));
				if (!StringUtils.isEmpty(result))
					throw new ValidationException(result);
			}
		}
		catch (TransformerException e)
		{
			if (transformer == null)
				throw new ValidatorException(e);
			else
				throw new ValidationException(transformer.getXslErrors().toString(),e);
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
