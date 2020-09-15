package nl.ordina.digikoppeling.ebf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import nl.ordina.digikoppeling.ebf.validator.ValidatorException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TransformDirToPDFs
{
	protected transient Log logger = LogFactory.getLog(this.getClass());

	public static void main(String[] args)
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
				//boolean expectedResult = f.getFileName().toString().contains("ok");
				try
				{
					TransformFileToPDF.validateAndTransform(f.toString());
					System.out.println("Message valid.");
				}
				catch (ValidatorException e)
				{
					System.out.println("Message invalid:" + e.getMessage());
					e.printStackTrace();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			});
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
