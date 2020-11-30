package it.unifi;

public class KernelMatrix {
    private final Integer[][] matrix;
    private final float multFactor;

    public KernelMatrix(Integer[][] matrix, float multFactor) {
        this.matrix = matrix;
        this.multFactor = multFactor;
    }

    public Integer[][] getMatrix() {
        return matrix;
    }

    public float getMultFactor() {
        return multFactor;
    }
}
