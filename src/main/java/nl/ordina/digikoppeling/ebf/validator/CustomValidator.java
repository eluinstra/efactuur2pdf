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

import java.util.AbstractMap.SimpleEntry;

import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.StringUtils;

import lombok.val;
import nl.ordina.digikoppeling.ebf.transformer.XSLTransformer;

public class CustomValidator
{
	public void validate(byte[] xml) throws ValidatorException
	{
		val transformer = XSLTransformer.getInstance("/nl/clockwork/efactuur/nl/domain/ubl/Custom-Validation.xsl");
		try
		{
			val validationResult = transformer.transform(new String(xml));
			val result = filterResult(validationResult);
			if (StringUtils.isNotBlank(result))
				throw new ValidationException(result);
		}
		catch (TransformerException e)
		{
			throw new ValidationException(transformer.getXslErrors().toString(),e);
		}
	}

	private String filterResult(String xml) throws TransformerException
	{
		val transformer = XSLTransformer.getInstance("/nl/ordina/digikoppeling/ebf/xslt/ErrorFilter.xsl");
		return transformer.transform(xml,new SimpleEntry<String,Object>("failOnWarning","true"));
	}
}
