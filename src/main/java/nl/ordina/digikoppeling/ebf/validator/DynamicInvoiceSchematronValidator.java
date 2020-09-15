package nl.ordina.digikoppeling.ebf.validator;

import javax.xml.transform.TransformerException;

import nl.clockwork.efactuur.VersionHelper;
import nl.clockwork.efactuur.VersionNotFoundException;
import nl.ordina.digikoppeling.ebf.model.MessageVersion;
import nl.ordina.digikoppeling.ebf.transformer.XSLTransformer;

import org.apache.commons.lang.StringUtils;

public class DynamicInvoiceSchematronValidator
{
	private final VersionHelper versionResolver;
	private final boolean failOnWarning;

	public DynamicInvoiceSchematronValidator(VersionHelper versionResolver, boolean failOnWarning)
	{
		this.versionResolver = versionResolver;
		this.failOnWarning = failOnWarning;
	}

	public void validate(byte[] xml, MessageVersion messageType) throws ValidatorException
	{
		XSLTransformer transformer = null;
		try
		{
			String xslFile = versionResolver.getSchematronXslPath(messageType.getType(),messageType.getFormat(),messageType.getVersion());
			if (!StringUtils.isEmpty(xslFile))
			{
				transformer = XSLTransformer.getInstance(xslFile);
				String result = transformer.transform(new String(xml));
				result = filterResult(result);
				if (StringUtils.isNotBlank(result))
					//if (result.contains("failed-assert"))
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

	private String filterResult(String xml)
	{
		//FIXME: improve???
		boolean tagActive = false;
		StringBuffer result = new StringBuffer();
		for (final String temp: xml.split("\n"))
		{
			if (temp.contains("<svrl:failed-assert") && (failOnWarning || xml.contains("flag=\"fatal\"")))
				tagActive = true;
			if (tagActive == true)
					result.append(temp);
			if (temp.contains("</svrl:failed-assert"))
				tagActive = false;
		}
		return result.toString();
	}
}