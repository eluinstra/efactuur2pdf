package nl.ordina.digikoppeling.ebf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class TransformDirToPDFs implements SystemInterface
{
	public static void main(String[] args) throws IOException
	{
		if (args.length != 1)
		{
			System.out.println("Usage: TransformDirToPDFs <path>");
			return;
		}
		new TransformDirToPDFs().transform(args[0]);
	}

	private void transform(String path) throws IOException
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
					new TransformFileToPDF().transform(f.toString());
				}
				catch (Exception e)
				{
					println("An unexpected error occurred: " + e.getMessage());
				}
			});
		}
	}
}
