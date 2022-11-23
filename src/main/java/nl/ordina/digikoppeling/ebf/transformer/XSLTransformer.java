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
package nl.ordina.digikoppeling.ebf.transformer;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;

import lombok.val;
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
	private static Map<String,XSLTransformer> transformers = new HashMap<>();
	private Templates templates;
	private StringBuilder xslErrors;

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
	public static XSLTransformer getInstance(String xslFile)
	{
		transformers.computeIfAbsent(xslFile,XSLTransformer::new);
		return transformers.get(xslFile);
	}

	private XSLTransformer(String xslFile)
	{
		xslErrors = new StringBuilder();
		try
		{
			templates = new TransformerFactoryImpl().newTemplates(new StreamSource(this.getClass().getResourceAsStream(xslFile),this.getClass().getResource(xslFile).toString()));
		}
		catch (TransformerConfigurationException e)
		{
			throw new IllegalStateException(e);
		}
	}

	public String transform(String xml, Entry<String, Object>...parameters) throws TransformerException
	{
		xslErrors = new StringBuilder();
		val transformer = templates.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		//transformer.setErrorListener(this.errors);
		//((net.sf.saxon.jaxp.TransformerImpl)transformer).getUnderlyingController().setMessageEmitter(new MessageWarner());
		((net.sf.saxon.jaxp.TransformerImpl)transformer).getUnderlyingController().setMessageEmitter(this.receiver);
		for (val p: parameters)
			transformer.setParameter(p.getKey(), p.getValue());
		val xmlsource = new StreamSource(new StringReader(xml));
		val writer = new StringWriter();
		val output = new StreamResult(writer);
		transformer.transform(xmlsource,output);
		writer.flush();
		return writer.toString();
	}

	public StringBuilder getXslErrors()
	{
		return xslErrors;
	}
	
	public static void main(String[] args) throws TransformerException, IOException
	{
		val transformer = getInstance("/template.xsl");
		System.out.println(transformer.transform(IOUtils.toString(XSLTransformer.class.getResourceAsStream("/input.xml"))));
	}
}
