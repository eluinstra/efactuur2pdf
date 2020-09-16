package nl.ordina.digikoppeling.ebf;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import nl.clockwork.efactuur.Constants;
import nl.clockwork.efactuur.DigikoppelingVersionHelper;
import nl.ordina.digikoppeling.ebf.model.MessageVersion;
import nl.ordina.digikoppeling.ebf.processor.MessageParser;
import nl.ordina.digikoppeling.ebf.processor.ParseException;
import nl.ordina.digikoppeling.ebf.validator.DynamicInvoiceGenericodeValidator;
import nl.ordina.digikoppeling.ebf.validator.DynamicInvoiceSchematronValidator;
import nl.ordina.digikoppeling.ebf.validator.DynamicInvoiceXSDValidator;
import nl.ordina.digikoppeling.ebf.validator.ValidationException;
import nl.ordina.digikoppeling.ebf.validator.ValidatorException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ValidateDir
{
	protected transient Log logger = LogFactory.getLog(this.getClass());
	private boolean failOnWarning;

	public void validate(byte[] content) throws ValidatorException
	{
		try
		{
			MessageVersion messageVersion = new MessageParser().getMessageVersion(content);
			System.out.println("MessageType: " + messageVersion.getType());
			System.out.println("MessageFormat: " + messageVersion.getFormat());
			System.out.println("MessageVersion: " + messageVersion.getVersion());
			DynamicInvoiceXSDValidator xsdValidator = new DynamicInvoiceXSDValidator();
			xsdValidator.setVersionResolver(new DigikoppelingVersionHelper());
			xsdValidator.validate(content,messageVersion);
			DynamicInvoiceSchematronValidator schematronValidator = new DynamicInvoiceSchematronValidator(new DigikoppelingVersionHelper(),failOnWarning);
			schematronValidator.validate(content,messageVersion);
			if (messageVersion.getFormat().equals(Constants.MessageFormat.UBL))
			{
				DynamicInvoiceGenericodeValidator genericodeValidator = new DynamicInvoiceGenericodeValidator();
				genericodeValidator.setVersionResolver(new DigikoppelingVersionHelper());
				genericodeValidator.validate(content,messageVersion);
			}
		}
		catch (ParseException e)
		{
			throw new ValidationException(e);
		}
	}

	public static void main(String[] args) throws IOException
	{
		if (args.length != 1)
		{
			System.out.println("Usage: ValidateDir <directory>");
			return;
		}
		Path filename = Paths.get(args[0]);
		try (Stream<Path> files = Files.list(filename))
		{
			files.filter(f-> f.getFileName().toString().endsWith(".xml"))
			.sorted()
			.forEach(f ->
			{
				System.out.println(f.getFileName().toString());
				try
				{
					new ValidateDir().validate(IOUtils.toByteArray(new FileInputStream(f.toFile())));
					System.out.println("Message valid.");
				}
				catch (ValidationException e)
				{
					System.out.println("Message invalid: " + e.getMessage());
				}
				catch (Exception e)
				{
					System.out.println("An unexpected error occurred: " + e.getMessage());
				}
			});
		}
	}
}
