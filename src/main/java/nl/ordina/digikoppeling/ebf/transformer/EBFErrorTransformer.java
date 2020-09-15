package nl.ordina.digikoppeling.ebf.transformer;

import nl.ordina.digikoppeling.ebf.AFSErrorCode;
import nl.ordina.digikoppeling.ebf.model.EBFError;
import nl.ordina.digikoppeling.ebf.validator.ValidationException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class EBFErrorTransformer
{
	protected transient Log logger = LogFactory.getLog(getClass());

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
