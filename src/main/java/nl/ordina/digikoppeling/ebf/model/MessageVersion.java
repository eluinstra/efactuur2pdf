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
package nl.ordina.digikoppeling.ebf.model;


import java.io.Serializable;

public class MessageVersion implements Serializable
{
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
