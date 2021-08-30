package test;

import java.util.ArrayList;

import test.Commands.Command;
import test.Commands.DefaultIO;

public class CLI {

    ArrayList<Command> commands;
    DefaultIO dio;
    Commands c;

    public CLI(DefaultIO dio) {
        this.dio = dio;
        c = new Commands(dio);
        commands = new ArrayList<>();
        commands.add(c.new MenuCommand());
        commands.add(c.new UploadCommand());
        commands.add(c.new AlgorithmCommand());
        commands.add(c.new DetectCommand());
        commands.add(c.new DisplayReportsCommand());
        commands.add(c.new AnalyzeCommand());

    }
    // execute commands until exit  command
    public void start() {
        commands.get(0).execute();
        String value = dio.readText();
        int option = Integer.parseInt(value);
        while (1 <= option && option <= 6) {
            if (option == 6) {
                dio.write("bye\n");
                break;
            }
            commands.get(option).execute();
            commands.get(0).execute();
            option = Integer.parseInt(dio.readText());
        }

    }
}
