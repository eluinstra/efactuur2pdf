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
