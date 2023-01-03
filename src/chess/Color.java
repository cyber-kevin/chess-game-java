package chess;

public enum Color {

    EMPTY(""),
    BLACK("\u001B[1;33m"), //but YELLOW haha
    RED("\u001B[1;31m"),
    GREEN("\u001B[1;32m"),
    YELLOW("\u001B[1;33m"),
    CYAN("\u001B[1;36m"),
    WHITE("\u001B[1;37m"),
    RED_BACKGROUND("\033[41m"),
    GREEN_BACKGROUND("\033[42m"),
    CYAN_BACKGROUND("\033[46m");

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
