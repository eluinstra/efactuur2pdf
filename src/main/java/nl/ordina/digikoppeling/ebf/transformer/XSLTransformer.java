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

import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.event.PipelineConfiguration;
import net.sf.saxon.event.Receiver;
import net.sf.saxon.om.NamespaceBinding;
import net.sf.saxon.om.NodeName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.SchemaType;
import net.sf.saxon.type.SimpleType;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XSLTransformer
{
	protected transient Log logger = LogFactory.getLog(this.getClass());
	private static Map<String,XSLTransformer> transformers = new HashMap<String,XSLTransformer>();
	private Templates templates;
	private StringBuffer xslErrors;

	private Receiver receiver = new Receiver()
	{
		public String getSystemId()
		{
			return null;
		}
		public void attribute(NodeName attName, SimpleType typeCode, CharSequence value, int locationId, int properties) throws XPathException
		{
		}
		public void characters(CharSequence arg0, int arg1, int arg2) throws XPathException
		{
			xslErrors.append(arg0);
		}
		public void close() throws XPathException
		{
		}
		public void comment(CharSequence arg0, int arg1, int arg2) throws XPathException
		{
		}
		public void endDocument() throws XPathException
		{
		}
		public void endElement() throws XPathException
		{
		}
		public PipelineConfiguration getPipelineConfiguration()
		{
			return null;
		}
		public void namespace(NamespaceBinding namespaceBinding, int properties) throws XPathException
		{
		}
		public void open() throws XPathException
		{
		}
		public void processingInstruction(String arg0, CharSequence arg1, int arg2, int arg3) throws XPathException
		{
		}
		public void setPipelineConfiguration(PipelineConfiguration arg0)
		{
		}
		public void setSystemId(String arg0)
		{
		}
		public void setUnparsedEntity(String arg0, String arg1, String arg2) throws XPathException
		{
		}
		public void startContent() throws XPathException
		{
		}
		public void startDocument(int arg0) throws XPathException
		{
		}
		@Override
		public void startElement(NodeName elemName, SchemaType typeCode, int locationId, int properties) throws XPathException
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
