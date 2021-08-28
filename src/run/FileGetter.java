package run;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static format.PrinterFormat.*;

public class FileGetter {
    
    public static void getFile(String directory) {
        try {
            File file = new File(directory);
            CerealFile cFile = readFile(file);
            if (!cFile.fileName.substring(cFile.fileName.lastIndexOf(".") + 1).equals("cereal")) {
                System.out.println(formatWarn("This is not a .cereal file but it is still being attempted to run"));
            }
            System.out.println("'" + cFile.fileText + "'");
            String output = Runner.run(cFile);

            // FINAL OUTPUT
            System.out.println(output);

        } catch (Exception e) {
            System.out.println(formatError("Could not read file at directory: '" + directory + "'"));
        }
    }

    private static CerealFile readFile(File file) throws IOException {
		String fileName = file.getName();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder stringBuilder = new StringBuilder();
		String line = "";

		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append("\n");
		}
		// delete the last new line separator

		if (stringBuilder.toString().equals("")) {
			reader.close();
			return new CerealFile(fileName, "");
		}

		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		reader.close();
		
		String text = stringBuilder.toString();

		return new CerealFile(fileName, text);

	}
}
