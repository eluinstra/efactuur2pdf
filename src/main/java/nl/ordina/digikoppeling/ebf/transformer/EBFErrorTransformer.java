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

import java.util.Arrays;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import lombok.val;
import nl.ordina.digikoppeling.ebf.AFSErrorCode;
import nl.ordina.digikoppeling.ebf.model.EBFError;
import nl.ordina.digikoppeling.ebf.validator.ValidationException;

public class EBFErrorTransformer
{
	public EBFError transform(Exception e)
	{
		return exceptionToFoutType(e);
	}

	private EBFError exceptionToFoutType(Throwable throwable)
	{
		val afleverError = knownValidationException(throwable) ? AFSErrorCode.AFS100 : AFSErrorCode.AFS400;
		val foutcode = afleverError.foutCode();
		val foutbeschrijving = afleverError.foutBeschrijving() + "\nFoutmelding:\n" + throwable.getMessage();
		return new EBFError(foutcode,foutbeschrijving);
	}

	private boolean knownValidationException(Throwable throwable)
	{
		return Arrays.stream(ExceptionUtils.getThrowables(throwable))
			.anyMatch(t -> t instanceof ValidationException || t instanceof SAXParseException || t instanceof SAXException);
	}

}
