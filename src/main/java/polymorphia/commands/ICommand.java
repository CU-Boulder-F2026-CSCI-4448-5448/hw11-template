package polymorphia.commands;

public interface ICommand {
    boolean execute();

    void executeCommand();

    CommandType getType();

    String optionString();
}
