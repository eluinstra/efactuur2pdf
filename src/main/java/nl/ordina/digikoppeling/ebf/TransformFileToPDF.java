package nl.ordina.digikoppeling.ebf;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Locale;

import javax.xml.transform.Result;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.lib.StandardErrorListener;
import nl.clockwork.efactuur.Constants;
import nl.clockwork.efactuur.DigikoppelingVersionHelper;
import nl.clockwork.efactuur.VersionNotFoundException;
import nl.ordina.digikoppeling.ebf.model.MessageVersion;
import nl.ordina.digikoppeling.ebf.processor.MessageParser;
import nl.ordina.digikoppeling.ebf.validator.DynamicInvoiceGenericodeValidator;
import nl.ordina.digikoppeling.ebf.validator.DynamicInvoiceSchematronValidator;
import nl.ordina.digikoppeling.ebf.validator.DynamicInvoiceXSDValidator;
import nl.ordina.digikoppeling.ebf.validator.StringLogger;
import nl.ordina.digikoppeling.ebf.validator.ValidationException;
import nl.ordina.digikoppeling.ebf.validator.ValidatorException;

import org.apache.commons.io.IOUtils;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

public class TransformFileToPDF
{
	private Templates errorTemplates;
	private boolean failOnWarning;

	public TransformFileToPDF() throws TransformerConfigurationException
	{
		errorTemplates = getSaxonXslTemplates("/nl/ordina/digikoppeling/ebf/xslt/ErrorToPDF.xsl");
	}

	protected static void validateAndTransform(String filename) throws Exception
	{
		byte[] content = IOUtils.toByteArray(new FileInputStream(filename));
		MessageVersion messageVersion = new MessageParser().getMessageVersion(content);
		System.out.println("MessageType: " + messageVersion.getType());
		System.out.println("MessageFormat: " + messageVersion.getFormat());
		System.out.println("MessageVersion: " + messageVersion.getVersion());
		TransformFileToPDF messageTransformer = new TransformFileToPDF();
		try
		{
			messageTransformer.validate(content,messageVersion);
		}
		catch (ValidationException e)
		{
			e.printStackTrace();
		}
		content = messageTransformer.transformToCanonical(content,messageVersion);
		content = messageTransformer.transformCanonicalToPDF(content,"1",messageVersion,"E-Factuur");
		IOUtils.write(content,new FileOutputStream(filename + ".pdf"));
	}
	
	public void validate(byte[] content, MessageVersion messageVersion) throws ValidatorException
	{
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

	public byte[] transformToCanonical(byte[] content, MessageVersion messageVersion) throws ValidationException, IOException, javax.xml.transform.TransformerException, VersionNotFoundException
	{
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		Templates templates = getSaxonXslTemplates(new DigikoppelingVersionHelper().getInvoiceToCanonicalPath(messageVersion.getType(),messageVersion.getFormat(),messageVersion.getVersion()));
		templates.newTransformer().transform(new StreamSource(new ByteArrayInputStream(content)),new StreamResult(result));
		result.flush();
		return result.toByteArray();
	}

	public byte[] transformCanonicalToPDF(byte[] content, String messageId, MessageVersion messageVersion, String berichtSoort) throws IOException, javax.xml.transform.TransformerException, FOPException, VersionNotFoundException
	{
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		StreamSource src = new StreamSource(new ByteArrayInputStream(content));
		FopFactory fopFactory = FopFactory.newInstance();
		try (OutputStream out = new BufferedOutputStream(result))
		{
			Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF,out);
			Templates templates = getSaxonXslTemplates(new DigikoppelingVersionHelper().getCanonicalToPDFPath(messageVersion.getType(),messageVersion.getFormat(),messageVersion.getVersion()));
			Transformer transformer = templates.newTransformer();
			StringLogger logger = addStringLogger(transformer);
			setParameters(transformer,messageId,messageVersion,berichtSoort);
			Result r = new SAXResult(fop.getDefaultHandler());
			try
			{
				transformer.transform(src,r);
			}
			catch (javax.xml.transform.TransformerException e)
			{
				System.out.println(logger.getLog());
				e.printStackTrace();
				result = new ByteArrayOutputStream();
				try (OutputStream errorOut = new BufferedOutputStream(result))
				{
					fop = fopFactory.newFop(MimeConstants.MIME_PDF,errorOut);
					Transformer errorTransformer = errorTemplates.newTransformer();
					setParameters(errorTransformer,messageId,messageVersion,berichtSoort);
					r = new SAXResult(fop.getDefaultHandler());
					src = new StreamSource(new ByteArrayInputStream(("<error>" + logger.getLog() + "</error>").getBytes()));
					errorTransformer.transform(src,r);
				}
			}
		}
		return result.toByteArray();
	}

	private StringLogger addStringLogger(Transformer transformer)
	{
		StandardErrorListener listener = new StandardErrorListener();
		StringLogger logger = new StringLogger();
		listener.setLogger(logger);
		transformer.setErrorListener(listener);
		return logger;
	}

	private void setParameters(Transformer transformer, String messageId, MessageVersion messageVersion, String berichtSoort)
	{
		transformer.setParameter("message_id",messageId);
		transformer.setParameter("message_date",Calendar.getInstance(Locale.getDefault()).getTime());
		transformer.setParameter("message_format",messageVersion.getFormat());
		transformer.setParameter("message_version",messageVersion.getVersion());
		transformer.setParameter("bericht_soort",berichtSoort);
		transformer.setParameter("original_message_type",messageVersion.getFormat().toString());
	}

	public Templates getSaxonXslTemplates(String xslFile) throws TransformerConfigurationException
	{
		return new TransformerFactoryImpl().newTemplates(new StreamSource(this.getClass().getResourceAsStream(xslFile),this.getClass().getResource(xslFile).toString()));
	}

	public static void main(String[] args) throws Exception
	{
		if (args.length != 1)
		{
			System.out.println("Usage: TransformFileToPDF <filename>");
			return;
		}
		TransformFileToPDF.validateAndTransform(args[0]);
	}
}
