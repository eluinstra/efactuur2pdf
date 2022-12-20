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
import java.util.Optional;

import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.StringUtils;

import lombok.val;
import nl.clockwork.efactuur.VersionHelper;
import nl.clockwork.efactuur.VersionNotFoundException;
import nl.ordina.digikoppeling.ebf.model.MessageVersion;
import nl.ordina.digikoppeling.ebf.transformer.XSLTransformer;

public class DynamicInvoiceSchematronValidator
{
	private final VersionHelper versionResolver;
	private final boolean failOnWarning;

	public DynamicInvoiceSchematronValidator(VersionHelper versionResolver, boolean failOnWarning)
	{
		this.versionResolver = versionResolver;
		this.failOnWarning = failOnWarning;
	}

	public void validate(byte[] xml, MessageVersion messageVersion) throws ValidatorException
	{
		val transformer = createTransformer(messageVersion);
		if (transformer.isPresent())
		{
			try
			{
				val validationResult = transformer.get().transform(new String(xml));
				val result = filterErrors(validationResult, failOnWarning);
				if (StringUtils.isNotBlank(result))
					throw new ValidationException(result);
			}
			catch (TransformerException e)
			{
				throw new ValidationException(transformer.get().getXslErrors(),e);
			}
		}
	}

	private Optional<XSLTransformer> createTransformer(MessageVersion messageVersion) throws ValidatorException
	{
		try
		{
			val xslFile = versionResolver.getSchematronXslPath(messageVersion.getType(),messageVersion.getFormat(),messageVersion.getVersion());
			if (!StringUtils.isEmpty(xslFile))
				return Optional.of(XSLTransformer.getInstance(xslFile));
			else
				return Optional.empty();
		}
		catch (VersionNotFoundException e)
		{
			throw new ValidatorException(e);
		}
	}

	private String filterErrors(String xml, boolean failOnWarning) throws TransformerException
	{
		val transformer = XSLTransformer.getInstance("/nl/ordina/digikoppeling/ebf/xslt/ErrorFilter.xsl");
		return transformer.transform(xml,new SimpleEntry<String, Object>("failOnWarning", failOnWarning ? "true" : "false"));
	}
}