package nl.ordina.digikoppeling.ebf;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Locale;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.xml.sax.SAXException;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.lib.StandardErrorListener;
import nl.clockwork.efactuur.DigikoppelingVersionHelper;
import nl.clockwork.efactuur.VersionNotFoundException;
import nl.ordina.digikoppeling.ebf.model.MessageVersion;
import nl.ordina.digikoppeling.ebf.processor.MessageParser;
import nl.ordina.digikoppeling.ebf.validator.StringLogger;
import nl.ordina.digikoppeling.ebf.validator.ValidationException;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransformFileToPDF implements SystemInterface
{
	@NonNull
	Templates errorTemplates;

	public TransformFileToPDF() throws TransformerConfigurationException
	{
		errorTemplates = getSaxonXslTemplates("/nl/ordina/digikoppeling/ebf/xslt/ErrorToPDF.xsl");
	}

	public static void main(String[] args) throws Exception
	{
		if (args.length != 1)
		{
			System.out.println("Usage: TransformFileToPDF <filename>");
			return;
		}
		new TransformFileToPDF().transform(args[0]);
	}

	public void transform(String filename) throws Exception
	{
		val content = IOUtils.toByteArray(new FileInputStream(filename));
		val messageVersion = new MessageParser().getMessageVersion(content);
		println("MessageType: " + messageVersion.getType());
		println("MessageFormat: " + messageVersion.getFormat());
		println("MessageVersion: " + messageVersion.getVersion());
		val pdf = createPDF(content,messageVersion);
		IOUtils.write(pdf,new FileOutputStream(filename + ".pdf"));
		println("PDF file " + filename + ".pdf created");
	}

	private byte[] createPDF(final byte[] content, final MessageVersion messageVersion) throws TransformerConfigurationException, ValidationException, IOException, TransformerException, VersionNotFoundException, SAXException, URISyntaxException
	{
		val messageTransformer = new TransformFileToPDF();
		val canonical = messageTransformer.transformToCanonical(content,messageVersion);
		return messageTransformer.transformCanonicalToPDF(canonical,"1",messageVersion,"E-Factuur");
	}
	
	private byte[] transformToCanonical(byte[] content, MessageVersion messageVersion) throws ValidationException, IOException, javax.xml.transform.TransformerException, VersionNotFoundException
	{
		val result = new ByteArrayOutputStream();
		val templates = getSaxonXslTemplates(new DigikoppelingVersionHelper().getInvoiceToCanonicalPath(messageVersion.getType(),messageVersion.getFormat(),messageVersion.getVersion()));
		templates.newTransformer().transform(new StreamSource(new ByteArrayInputStream(content)),new StreamResult(result));
		result.flush();
		return result.toByteArray();
	}

	private byte[] transformCanonicalToPDF(byte[] content, String messageId, MessageVersion messageVersion, String berichtSoort) throws IOException, javax.xml.transform.TransformerException, VersionNotFoundException, SAXException, URISyntaxException
	{
		val result = new ByteArrayOutputStream();
		val src = new StreamSource(new ByteArrayInputStream(content));
		val fopFactory = FopFactory.newInstance(new File(".").toURI());
		try (val out = new BufferedOutputStream(result))
		{
			val fop = fopFactory.newFop(MimeConstants.MIME_PDF,out);
			val templates = getSaxonXslTemplates(new DigikoppelingVersionHelper().getCanonicalToPDFPath(messageVersion.getType(),messageVersion.getFormat(),messageVersion.getVersion()));
			val transformer = createTransformer(templates,messageId,messageVersion,berichtSoort);
			val r = new SAXResult(fop.getDefaultHandler());
			try
			{
				transformer.transform(src,r);
			}
			catch (javax.xml.transform.TransformerException e)
			{
				val logger = addStringLogger(transformer);
				println("The following transformation errors occurred:\n" + logger.getLog());
				return handleTransformerException(createTransformer(errorTemplates,messageId,messageVersion,berichtSoort),fopFactory,logger);
			}
		}
		return result.toByteArray();
	}

	private byte[] handleTransformerException(Transformer errorTransformer, FopFactory fopFactory, StringLogger logger) throws FOPException, TransformerConfigurationException, TransformerException, IOException
	{
		val result = new ByteArrayOutputStream();
		try (OutputStream errorOut = new BufferedOutputStream(result))
		{
			val fop = fopFactory.newFop(MimeConstants.MIME_PDF,errorOut);
			val r = new SAXResult(fop.getDefaultHandler());
			val src = new StreamSource(new ByteArrayInputStream(("<error>" + logger.getLog() + "</error>").getBytes()));
			errorTransformer.transform(src,r);
		}
		return result.toByteArray();
	}

	private StringLogger addStringLogger(Transformer transformer)
	{
		val listener = new StandardErrorListener();
		val logger = new StringLogger();
		listener.setLogger(logger);
		transformer.setErrorListener(listener);
		return logger;
	}

	private Transformer createTransformer(Templates templates, String messageId, MessageVersion messageVersion, String berichtSoort) throws TransformerConfigurationException
	{
		val result = templates.newTransformer();
		setParameters(result,messageId,messageVersion,berichtSoort);
		return result;
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

	private Templates getSaxonXslTemplates(String xslFile) throws TransformerConfigurationException
	{
		return new TransformerFactoryImpl().newTemplates(new StreamSource(getClass().getResourceAsStream(xslFile),getClass().getResource(xslFile).toString()));
	}
}
