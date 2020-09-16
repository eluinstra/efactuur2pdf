package nl.ordina.digikoppeling.ebf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import lombok.AccessLevel;
import lombok.val;
import lombok.experimental.FieldDefaults;
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

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ValidateFile implements SystemInterface
{
	boolean failOnWarning = false;

	public static void main(String[] args) throws FileNotFoundException, ValidatorException, IOException
	{
		if (args.length != 1)
		{
			System.out.println("Usage: ValidateFile <filename>");
			return;
		}
		new ValidateFile().validate(args[0]);
	}

	public void validate(String filename) throws ValidatorException, IOException, FileNotFoundException
	{
		try
		{
			new ValidateFile().validate(IOUtils.toByteArray(new FileInputStream(filename)));
			println("Message valid.");
		}
		catch (ValidationException e)
		{
			println("Message invalid: " + e.getMessage());
		}
	}

	private void validate(byte[] content) throws ValidatorException
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
