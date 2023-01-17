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
package nl.ordina.digikoppeling.ebf;


import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.transform.TransformerConfigurationException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import nl.clockwork.efactuur.Constants;
import nl.clockwork.efactuur.DigikoppelingVersionHelper;
import nl.ordina.digikoppeling.ebf.processor.MessageParser;
import nl.ordina.digikoppeling.ebf.processor.ParseException;
import nl.ordina.digikoppeling.ebf.validator.CustomValidator;
import nl.ordina.digikoppeling.ebf.validator.DynamicInvoiceGenericodeValidator;
import nl.ordina.digikoppeling.ebf.validator.DynamicInvoiceSchematronValidator;
import nl.ordina.digikoppeling.ebf.validator.DynamicInvoiceXSDValidator;
import nl.ordina.digikoppeling.ebf.validator.ValidationException;
import nl.ordina.digikoppeling.ebf.validator.ValidatorException;
import org.apache.commons.io.IOUtils;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ValidateFile implements SystemInterface
{
	boolean failOnWarning = true;

	public static void main(String[] args) throws ValidatorException, IOException, TransformerConfigurationException
	{
		if (args.length != 1)
		{
			System.out.println("Usage: ValidateFile <filename>");
			return;
		}
		new ValidateFile().validate(args[0]);
	}

	public void validate(String filename) throws ValidatorException, IOException, TransformerConfigurationException
	{
		try
		{
			new ValidateFile().validate(IOUtils.toByteArray(new FileInputStream(filename)));
			println("Message valid.");
		}
		catch (ValidationException e)
		{
			println("Message invalid:\n" + e.getMessage());
		}
	}

	private void validate(byte[] content) throws ValidatorException, TransformerConfigurationException
	{
		try
		{
			val messageVersion = new MessageParser().getMessageVersion(content);
			println("MessageType: " + messageVersion.getType());
			println("MessageFormat: " + messageVersion.getFormat());
			println("MessageVersion: " + messageVersion.getVersion());
			val xsdValidator = new DynamicInvoiceXSDValidator();
			xsdValidator.setVersionResolver(new DigikoppelingVersionHelper());
			xsdValidator.validate(content,messageVersion);
			val schematronValidator = new DynamicInvoiceSchematronValidator(new DigikoppelingVersionHelper(),failOnWarning);
			schematronValidator.validate(content,messageVersion);
			if (messageVersion.getFormat().equals(Constants.MessageFormat.UBL))
			{
				val genericodeValidator = new DynamicInvoiceGenericodeValidator();
				genericodeValidator.setVersionResolver(new DigikoppelingVersionHelper());
				genericodeValidator.validate(content,messageVersion);
				val customValidator = new CustomValidator();
				customValidator.validate(content);
			}
		}
		catch (ParseException e)
		{
			throw new ValidationException(e);
		}
	}
}
