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
package nl.ordina.digikoppeling.ebf;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.val;

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
		try (val files = Files.list(Paths.get(path)))
		{
			files.filter(f -> f.getFileName().toString().endsWith(".xml")).sorted().map(Object::toString).forEach(filename ->
			{
				println(filename);
				try
				{
					new TransformFileToPDF().transform(filename);
				}
				catch (Exception e)
				{
					println("An unexpected error occurred: " + e.getMessage());
				}
			});
		}
	}
}
