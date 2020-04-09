package scripts.api.ark;

/*Courtesy of Encoded from Tribot*/
public interface Task {

    Priority priority();

    boolean validate();

    void execute();

}
