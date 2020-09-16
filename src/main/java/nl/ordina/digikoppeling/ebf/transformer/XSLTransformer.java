package nl.ordina.digikoppeling.ebf.transformer;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.event.PipelineConfiguration;
import net.sf.saxon.event.Receiver;
import net.sf.saxon.om.AttributeMap;
import net.sf.saxon.om.NamespaceMap;
import net.sf.saxon.om.NodeName;
import net.sf.saxon.s9api.Location;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.SchemaType;

public class XSLTransformer
{
	protected transient Log logger = LogFactory.getLog(this.getClass());
	private static Map<String,XSLTransformer> transformers = new HashMap<String,XSLTransformer>();
	private Templates templates;
	private StringBuffer xslErrors;

	private Receiver receiver = new Receiver()
	{
		@Override
		public String getSystemId()
		{
			return null;
		}
		@Override
		public void characters(CharSequence chars, Location location, int properties) throws XPathException
		{
			xslErrors.append(chars);
		}
		@Override
		public void close() throws XPathException
		{
		}
		@Override
		public void comment(CharSequence content, Location location, int properties) throws XPathException
		{
		}
		@Override
		public void endDocument() throws XPathException
		{
		}
		@Override
		public void endElement() throws XPathException
		{
		}
		@Override
		public PipelineConfiguration getPipelineConfiguration()
		{
			return null;
		}
		@Override
		public void open() throws XPathException
		{
		}
		@Override
		public void processingInstruction(String name, CharSequence data, Location location, int properties) throws XPathException
		{
		}
		@Override
		public void setPipelineConfiguration(PipelineConfiguration arg0)
		{
		}
		@Override
		public void setSystemId(String arg0)
		{
		}
		@Override
		public void setUnparsedEntity(String arg0, String arg1, String arg2) throws XPathException
		{
		}
		public void startDocument(int arg0) throws XPathException
		{
		}
		@Override
		public void startElement(NodeName elemName, SchemaType type, AttributeMap attributes, NamespaceMap namespaces, Location location, int properties) throws XPathException
		{
		}
		@Override
		public boolean usesTypeAnnotations()
		{
			return false;
		}
	};

/*	private ErrorListener errors = new ErrorListener()
	{
		public void warning(TransformerException exception) throws TransformerException
		{
			xslErrors.append(exception.getLocalizedMessage());
		}

		public void error(TransformerException exception) throws TransformerException
		{
			xslErrors.append(exception.getLocalizedMessage());
		}

		public void fatalError(TransformerException exception) throws TransformerException
		{
			xslErrors.append(exception.getLocalizedMessage());
		}

	};
*/
	public static XSLTransformer getInstance(String xslFile) throws TransformerException
	{
		if (!transformers.containsKey(xslFile))
			transformers.put(xslFile,new XSLTransformer(xslFile));
		return transformers.get(xslFile);
	}

	private XSLTransformer(String xslFile)
	{
		xslErrors = new StringBuffer();
		try
		{
			templates = new TransformerFactoryImpl().newTemplates(new StreamSource(this.getClass().getResourceAsStream(xslFile),this.getClass().getResource(xslFile).toString()));
		}
		catch (TransformerConfigurationException e)
		{
			throw new RuntimeException(e);
		}
	}

	public String transform(String xml) throws TransformerException
	{
		xslErrors = new StringBuffer();
		Transformer transformer = templates.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		//transformer.setErrorListener(this.errors);
		//((net.sf.saxon.jaxp.TransformerImpl)transformer).getUnderlyingController().setMessageEmitter(new MessageWarner());
		((net.sf.saxon.jaxp.TransformerImpl)transformer).getUnderlyingController().setMessageEmitter(this.receiver);
		StreamSource xmlsource = new StreamSource(new StringReader(xml));
		StringWriter writer = new StringWriter();
		StreamResult output = new StreamResult(writer);
		transformer.transform(xmlsource,output);
		writer.flush();
		return writer.toString();
	}

	public StringBuffer getXslErrors()
	{
		return xslErrors;
	}
	
	public static void main(String[] args) throws TransformerException, IOException
	{
		XSLTransformer transformer = getInstance("/template.xsl");
		System.out.println(transformer.transform(IOUtils.toString(XSLTransformer.class.getResourceAsStream("/input.xml"))));
	}
}
