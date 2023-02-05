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


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.XMLConstants;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.val;
import net.sf.saxon.lib.Logger;
import net.sf.saxon.lib.StandardErrorListener;
import nl.ordina.digikoppeling.ebf.validator.StringLogger;
import org.apache.commons.io.input.BOMInputStream;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class XSLTransformer
{
	private static Map<String, Templates> templatesCache = new HashMap<>();
	@NonNull
	Templates templates;
	@NonFinal
	StringLogger logger;

	public static XSLTransformer getInstance(String xslFile)
	{
		return new XSLTransformer(templatesCache.computeIfAbsent(xslFile, XSLTransformer::getTemplates));
	}

	private static Templates getTemplates(String xslFile)
	{
		try
		{
			val tf = XSLTransformer.createSaxonTransformerFactory();
			return tf.newTemplates(new StreamSource(XSLTransformer.class.getResourceAsStream(xslFile), XSLTransformer.class.getResource(xslFile).toString()));
		}
		catch (TransformerConfigurationException e)
		{
			throw new IllegalStateException(e);
		}
	}

	public static TransformerFactory createSaxonTransformerFactory() throws TransformerFactoryConfigurationError
	{
		val result = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
		result.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		result.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
		return result;
	}

	public String transform(String xml, Entry<String, Object>...parameters) throws TransformerException
	{
		val transformer = createTransformer();
		logger = setLogger(transformer);
		for (val p : parameters)
			transformer.setParameter(p.getKey(), p.getValue());
		val source = new StreamSource(new StringReader(xml));
		return transform(transformer, source);
	}

	private Transformer createTransformer() throws TransformerConfigurationException
	{
		val result = templates.newTransformer();
		result.setOutputProperty(OutputKeys.INDENT, "yes");
		result.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		return result;
	}

	private StringLogger setLogger(final Transformer transformer)
	{
		val result = new StringLogger();
		transformer.setErrorListener(createErrorListener(result));
		return result;
	}

	private ErrorListener createErrorListener(Logger logger)
	{
		val result = new StandardErrorListener();
		result.setLogger(logger);
		return result;
	}

	private String transform(Transformer transformer, StreamSource xmlsource) throws TransformerException
	{
		val writer = new StringWriter();
		transformer.transform(xmlsource, new StreamResult(writer));
		writer.flush();
		return writer.toString();
	}

	public String transform(byte[] xml) throws TransformerException
	{
		return transform(new ByteArrayInputStream(xml));
	}

	public String transform(InputStream xml) throws TransformerException
	{
		val transformer = createTransformer();
		logger = setLogger(transformer);
		val source = new StreamSource(new BOMInputStream(xml));
		return transform(transformer, source);
	}

	public String getXslErrors()
	{
		return logger.getLog();
	}

	public static void main(String[] args) throws TransformerException, IOException
	{
		val transformer = getInstance("/template.xsl");
		System.out.println(transformer.transform(XSLTransformer.class.getResourceAsStream("/input.xml")));
	}
}
