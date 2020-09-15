package nl.ordina.digikoppeling.ebf.model;

import java.io.Serializable;

public class MessageVersion implements Serializable
{
	private final static long serialVersionUID = 1L;
	private nl.clockwork.efactuur.Constants.MessageType type;
	private nl.clockwork.efactuur.Constants.MessageFormat format;
	private String version;

	public MessageVersion(nl.clockwork.efactuur.Constants.MessageType type, nl.clockwork.efactuur.Constants.MessageFormat format, String version)
	{
		this.type = type;
		this.format = format;
		this.version = version;
	}

	public nl.clockwork.efactuur.Constants.MessageType getType()
	{
		return type;
	}

	public void setType(nl.clockwork.efactuur.Constants.MessageType type)
	{
		this.type = type;
	}

	public nl.clockwork.efactuur.Constants.MessageFormat getFormat()
	{
		return format;
	}

	public void setFormat(nl.clockwork.efactuur.Constants.MessageFormat format)
	{
		this.format = format;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	@Override
	public String toString()
	{
		return version;
	}

}
