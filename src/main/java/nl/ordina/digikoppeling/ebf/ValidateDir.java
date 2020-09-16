package nl.ordina.digikoppeling.ebf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ValidateDir implements SystemInterface
{
	public static void main(String[] args) throws IOException
	{
		if (args.length != 1)
		{
			System.out.println("Usage: ValidateDir <directory>");
			return;
		}
		new ValidateDir().validate(args[0]);
	}

	private void validate(String path) throws IOException
	{
		try (Stream<Path> files = Files.list(Paths.get(path)))
		{
			files.filter(f-> f.getFileName().toString().endsWith(".xml"))
			.sorted()
			.forEach(f ->
			{
				println(f.getFileName().toString());
				try
				{
					new ValidateFile().validate(f.toString());
				}
				catch (Exception e)
				{
					println("An unexpected error occurred: " + e.getMessage());
				}
			});
		}
	}
}
