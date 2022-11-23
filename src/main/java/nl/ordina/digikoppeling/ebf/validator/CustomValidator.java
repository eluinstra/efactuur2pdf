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

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringUtils;

import lombok.NonNull;
import lombok.val;
import net.sf.saxon.TransformerFactoryImpl;
import nl.ordina.digikoppeling.ebf.transformer.XSLTransformer;

public class CustomValidator
{
	@NonNull
	Templates errorTemplates;

	
	public CustomValidator() throws TransformerConfigurationException {
		errorTemplates = getSaxonXslTemplates("/nl/ordina/digikoppeling/ebf/xslt/ErrorFilter.xsl");
	}

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

	private String filterResult(String xml) throws TransformerException
	{
		val transformer = errorTemplates.newTransformer();
		transformer.setParameter("failOnWarning", "true");
		StreamSource xmlsource = new StreamSource(new StringReader(xml));
		StringWriter writer = new StringWriter();
		StreamResult output = new StreamResult(writer);
		transformer.transform(xmlsource,output);
		writer.flush();
		return writer.toString();
	}

	private Templates getSaxonXslTemplates(String xslFile) throws TransformerConfigurationException
	{
		return new TransformerFactoryImpl().newTemplates(new StreamSource(getClass().getResourceAsStream(xslFile),getClass().getResource(xslFile).toString()));
	}
}
