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

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class DynamicInvoiceSchematronValidator implements WithValidator
{
	@NonNull
	VersionHelper versionResolver;
	boolean failOnWarning;

	public void validate(byte[] xml, MessageVersion messageVersion) throws ValidatorException
	{
		getXslFile(messageVersion).map(transform(xml))
				.map(filterErrors(failOnWarning))
				.filter(result -> result.contains("failed-assert"))
				.ifPresent(this::throwValidationException);
	}

	private Optional<String> getXslFile(MessageVersion messageType) throws ValidationException
	{
		try
		{
			return versionResolver.getSchematronXslPath(messageType.getType(),messageType.getFormat(),messageType.getVersion());
		}
		catch (VersionNotFoundException e)
		{
			throw new ValidationException(e);
		}
	}
}