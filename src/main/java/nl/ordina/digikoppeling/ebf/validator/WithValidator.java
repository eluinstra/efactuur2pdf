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
import java.util.function.Function;
import javax.xml.transform.TransformerException;
import lombok.val;
import nl.ordina.digikoppeling.ebf.transformer.XSLTransformer;

public interface WithValidator
{
	default Function<String,String> transform(byte[] xml) throws ValidationException
	{
		return xslFile ->
		{
			val transformer = XSLTransformer.getInstance(xslFile);
			try
			{
				return transformer.transform(xml);
			}
			catch (TransformerException e)
			{
				throw new ValidationException(transformer.getXslErrors(),e);
			}
		};
	}

	default Function<String,String> filterErrors(boolean failOnWarning) throws ValidationException
	{
		return xml ->
		{
			val transformer = XSLTransformer.getInstance("/nl/ordina/digikoppeling/ebf/xslt/ErrorFilter.xsl");
			try
			{
				return transformer.transform(xml,new SimpleEntry<String,Object>("failOnWarning",failOnWarning ? "true" : "false"));
			}
			catch (TransformerException e)
			{
				throw new ValidationException(transformer.getXslErrors(),e);
			}
		};
	}

	default void throwValidationException(String result)
	{
		throw new ValidationException(result);
	}
}
