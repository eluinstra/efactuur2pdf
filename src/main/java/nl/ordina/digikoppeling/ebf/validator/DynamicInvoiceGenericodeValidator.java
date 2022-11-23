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

import org.apache.commons.lang3.StringUtils;

import io.vavr.control.Try;
import lombok.val;
import nl.clockwork.efactuur.VersionHelper;
import nl.clockwork.efactuur.VersionNotFoundException;
import nl.ordina.digikoppeling.ebf.model.MessageVersion;
import nl.ordina.digikoppeling.ebf.transformer.XSLTransformer;

public class DynamicInvoiceGenericodeValidator
{
	private VersionHelper versionResolver;

	public void validate(byte[] xml, MessageVersion messageType) throws ValidatorException
	{
		try
		{
			val xslFile = versionResolver.getGenericodeXslPath(messageType.getType(),messageType.getFormat(),messageType.getVersion());
			if (!StringUtils.isEmpty(xslFile))
			{
				val transformer = Try.of(() -> XSLTransformer.getInstance(xslFile)).getOrElseThrow(e -> new ValidatorException(e));
				val result = Try.of(() -> transformer.transform(new String(xml))).getOrElseThrow(e -> new ValidationException(transformer.getXslErrors().toString(),e));
				if (!StringUtils.isEmpty(result))
					throw new ValidationException(result);
			}
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
