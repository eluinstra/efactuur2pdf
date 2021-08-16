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

import nl.ordina.digikoppeling.ebf.AFSErrorCode;
import nl.ordina.digikoppeling.ebf.model.EBFError;
import nl.ordina.digikoppeling.ebf.validator.ValidationException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class EBFErrorTransformer
{

	public EBFError transform(Exception e)
	{
		return exceptionToFoutType(e);
	}

	private EBFError exceptionToFoutType(Throwable throwable)
	{
		EBFError error = new EBFError();
		AFSErrorCode afleverError = knownValidationException(throwable) ? AFSErrorCode.AFS100 : AFSErrorCode.AFS400;
		error.setFoutcode(afleverError.foutCode());
		error.setFoutbeschrijving(afleverError.foutBeschrijving() + "\nFoutmelding:\n" + throwable);
		return error;
	}

	private boolean knownValidationException(Throwable throwable)
	{
		for (Throwable t : ExceptionUtils.getThrowables(throwable))
		 if (t instanceof ValidationException || t instanceof SAXParseException || t instanceof SAXException)
			 return true;
		return false;
	}

}
