package rohan.app.com.ozlo.models;

/**
 * Created by rohan on 10-03-2017.
 */

public class Bot {
    private String input;
    private int type;

    public Bot(String input, int type) {
        this.input = input;
        this.type = type;
    }

    public String getInput() {
        return input;
    }

    public int getType() {
        return type;
    }
}
