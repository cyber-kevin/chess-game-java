package chess;

public enum Color {

    BLACK("\u001B[30m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    CYAN("\u001B[36m"),
    WHITE("\u001B[37m");

    private String color;

    private Color(String color) {
        this.color = color;
    }

    public String paint() {
        return color;
    }

    public String reset() {
        return "\u001B[0m";
    }


}
