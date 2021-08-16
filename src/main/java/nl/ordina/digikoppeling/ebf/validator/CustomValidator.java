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

import javax.xml.transform.TransformerException;

import nl.ordina.digikoppeling.ebf.transformer.XSLTransformer;

import org.apache.commons.lang3.StringUtils;

public class CustomValidator
{
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
