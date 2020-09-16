package nl.ordina.digikoppeling.ebf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.ordina.digikoppeling.ebf.validator.ValidationException;

public class TransformDirToPDFs
{
	protected transient Log logger = LogFactory.getLog(this.getClass());

	public static void main(String[] args) throws IOException
	{
		if (args.length != 1)
		{
			System.out.println("Usage: TransformDirToPDFs <directory>");
			return;
		}
		try (Stream<Path> files = Files.list(Paths.get(args[0])))
		{
			files.forEach(f ->
			{
				System.out.println(f.getFileName().toString());
				try
				{
					TransformFileToPDF.transform(f.toString());
					System.out.println("Message valid.");
				}
				catch (ValidationException e)
				{
					System.out.println("Message invalid:" + e.getMessage());
				}
				catch (Exception e)
				{
					System.out.println("An unexpected error occurred: " + e.getMessage());
				}
			});
		}
	}

}
