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


import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import nl.clockwork.efactuur.VersionHelper;
import nl.clockwork.efactuur.VersionNotFoundException;
import nl.ordina.digikoppeling.ebf.model.MessageVersion;
import org.apache.commons.lang3.StringUtils;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class DynamicInvoiceGenericodeValidator implements WithValidator
{
	@NonNull
	VersionHelper versionResolver;

	public void validate(byte[] xml, MessageVersion messageType) throws ValidatorException
	{
		getXslFile(messageType).map(transform(xml)).filter(StringUtils::isNotEmpty).ifPresent(this::throwValidationException);
	}

	private Optional<String> getXslFile(MessageVersion messageType) throws ValidationException
	{
		try
		{
			return versionResolver.getGenericodeXslPath(messageType.getType(), messageType.getFormat(), messageType.getVersion());
		}
		catch (VersionNotFoundException e)
		{
			throw new ValidationException(e);
		}
	}
}
