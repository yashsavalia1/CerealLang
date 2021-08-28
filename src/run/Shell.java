package run;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Shell {

	public static void shell() {

		System.out.println("Welcome to the Cereal Programming Language Shell!\r\nUse the /help command for help\r\n");

		Scanner scanner = new Scanner(System.in);
		while (true) {

			System.out.print("Cereal > ");
			String text = "";
			try {
				text = scanner.nextLine();
			} catch (NoSuchElementException e) {
				System.out.println("If you are trying to exit use the '/exit' command next time");
			}

			if (text.startsWith("/")) {
				String command = text.substring(1);
				if (command.equals("exit")) {
					break;
				} else {
					System.out.println(CommandHandler.processCommand(command));
					continue;
				}
			}

			String output = Runner.run(new CerealFile("<stdin>", text));
			
            if (output == null) {
				System.out.println("FATAL EXCEPTION");
			}

            // FINAL OUTPUT
			if (!output.equals("null")) {
				System.out.println(output);
			}
		}

		scanner.close();

	}

}