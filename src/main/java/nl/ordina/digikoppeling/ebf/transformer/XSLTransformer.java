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
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;

import lombok.AccessLevel;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.lib.Logger;
import net.sf.saxon.lib.StandardErrorListener;
import nl.ordina.digikoppeling.ebf.validator.StringLogger;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class XSLTransformer
{
	private static Map<String,XSLTransformer> transformers = new HashMap<>();
	Templates templates;
	@NonFinal
	StringLogger logger;

	public static XSLTransformer getInstance(String xslFile)
	{
		return transformers.computeIfAbsent(xslFile,XSLTransformer::new);
	}

	private XSLTransformer(String xslFile)
	{
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
		val transformer = createTransformer();
		logger = new StringLogger();
		transformer.setErrorListener(createErrorListener(logger));
		for (val p: parameters)
			transformer.setParameter(p.getKey(), p.getValue());
		val xmlsource = new StreamSource(new StringReader(xml));
		return transform(transformer, xmlsource);
	}

	private Transformer createTransformer() throws TransformerConfigurationException {
		val transformer = templates.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		return transformer;
	}

	private ErrorListener createErrorListener(Logger logger)
	{
		val listener = new StandardErrorListener();
		listener.setLogger(logger);
		return listener;
	}

	private String transform(Transformer transformer, StreamSource xmlsource) throws TransformerException
	{
		val writer = new StringWriter();
		transformer.transform(xmlsource, new StreamResult(writer));
		writer.flush();
		return writer.toString();
	}

	public String getXslErrors()
	{
		return logger.getLog();
	}
	
	public static void main(String[] args) throws TransformerException, IOException
	{
		val transformer = getInstance("/template.xsl");
		System.out.println(transformer.transform(IOUtils.toString(XSLTransformer.class.getResourceAsStream("/input.xml"), StandardCharsets.UTF_8)));
	}
}
