package fr.uha.ensisa.gm.projet;

public enum Direction {
    TOP(0),
    RIGHT(1),
    BOTTOM(2),
    LEFT(3);

    private final int value;
    Direction(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Direction get(int value) {
        for (Direction e : Direction.values()) {
            if (e.value == value) {
                return e;
            }
        }
        return null;
    }
}
