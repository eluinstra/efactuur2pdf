package nl.ordina.digikoppeling.ebf;

import java.io.FileInputStream;
import java.io.IOException;

import nl.clockwork.efactuur.Constants;
import nl.clockwork.efactuur.DigikoppelingVersionHelper;
import nl.ordina.digikoppeling.ebf.model.EBFError;
import nl.ordina.digikoppeling.ebf.model.MessageVersion;
import nl.ordina.digikoppeling.ebf.processor.MessageParser;
import nl.ordina.digikoppeling.ebf.processor.ParseException;
import nl.ordina.digikoppeling.ebf.transformer.EBFErrorTransformer;
import nl.ordina.digikoppeling.ebf.validator.CustomValidator;
import nl.ordina.digikoppeling.ebf.validator.DynamicInvoiceGenericodeValidator;
import nl.ordina.digikoppeling.ebf.validator.DynamicInvoiceSchematronValidator;
import nl.ordina.digikoppeling.ebf.validator.DynamicInvoiceXSDValidator;
import nl.ordina.digikoppeling.ebf.validator.ValidationException;
import nl.ordina.digikoppeling.ebf.validator.ValidatorException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ValidateFile
{
	protected transient Log logger = LogFactory.getLog(this.getClass());
	private boolean failOnWarning = false;

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
				CustomValidator customValidator = new CustomValidator();
				customValidator.validate(content);
			}
		}
		catch (ParseException e)
		{
			throw new ValidationException(e);
		}
	}

	public static void main(String[] args)
	{
		if (args.length != 1)
		{
			System.out.println("Usage: MessageValidator <filename>");
			return;
		}
		try
		{
			new ValidateFile().validate(IOUtils.toByteArray(new FileInputStream(args[0])));
			System.out.println("Message valid.");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			System.out.println("Message invalid:");
			e.printStackTrace();
			EBFError ebfError = new EBFErrorTransformer().transform(e);
			System.out.println(ebfError.getFoutcode());
			System.out.println(ebfError.getFoutbeschrijving());
		}
	}
}
