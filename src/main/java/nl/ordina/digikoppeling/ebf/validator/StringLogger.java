package nl.ordina.digikoppeling.ebf.validator;

import java.io.StringWriter;

import javax.xml.transform.stream.StreamResult;

import net.sf.saxon.lib.Logger;

public class StringLogger extends Logger
{
	StringWriter log = new StringWriter();

	@Override
	public void println(String message, int severity)
	{
		log.write(message);
		log.write("\n");
	}

	@Override
	public StreamResult asStreamResult()
	{
		return new StreamResult(log);
	}

	public String getLog()
	{
		return log.toString();
	}
}
