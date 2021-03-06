package nl.ordina.digikoppeling.ebf.validator;

import javax.xml.transform.TransformerException;

import nl.ordina.digikoppeling.ebf.transformer.XSLTransformer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CustomValidator
{
	protected transient Log logger = LogFactory.getLog(this.getClass());

	public void validate(byte[] xml) throws ValidatorException
	{
		XSLTransformer transformer = null;
		try
		{
			String xslFile = "/nl/clockwork/efactuur/nl/domain/ubl/Custom-Validation.xsl";
			if (!StringUtils.isEmpty(xslFile))
			{
				transformer = XSLTransformer.getInstance(xslFile);
				String result = transformer.transform(new String(xml));
				result = filterResult(result);
					//if (result.contains("flag=\"fatal\""))
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
	}

	private String filterResult(String result)
	{
		//FIXME: improve???
		boolean tagActive = false;
		StringBuffer filteredResult = new StringBuffer();
		for (final String temp: result.split("\n"))
		{
			if (temp.contains("<svrl:failed-assert"))
				tagActive = true;
			if (tagActive == true)
				//if (result.contains("flag=\"fatal\""))
					filteredResult.append(temp);
			if (temp.contains("</svrl:failed-assert"))
				tagActive = false;
		}
		return filteredResult.toString();
	}

}
