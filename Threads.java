package lesson5;

import java.util.Arrays;

public class Threads {

    private static final int SIZE = 10_000_000;
    private static final int HALF_SIZE = SIZE / 2;

    public static void main(String[] args) {
        float[] arr1 = newArr(SIZE);
        time(() -> sequential(arr1), "sequential");

        float[] arr2 = newArr(SIZE);
        time(() -> parallel(arr2), "parallel");
        System.out.println("Are arrays equal? " + Arrays.equals(arr1, arr2));
    }

    private static void sequential(float[] arr) {
        sequential(arr, 0);
    }

    private static void sequential(float[] arr, int shift) {
        for (int i = 0; i < arr.length; i++) {
            float value = arr[i];
            arr[i] = changeValue(i + shift, value);
        }
    }

    private static void parallel(float[] arr) {
        float[] part1 = Arrays.copyOfRange(arr, 0, HALF_SIZE);
        float[] part2 = Arrays.copyOfRange(arr, HALF_SIZE, arr.length);

        Thread thread1 = new Thread(() -> sequential(part1, 0));
        Thread thread2 = new Thread(() -> sequential(part2, HALF_SIZE));

        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
        System.arraycopy(part1, 0, arr, 0, HALF_SIZE);
        System.arraycopy(part2, 0, arr, HALF_SIZE, HALF_SIZE);

    }

    private static float[] newArr(int size) {
        float[] arr = new float[size];
        for (float elements : arr)
             elements = 1.0f;
        return arr;
    }

    private static float changeValue(int i, float value) {
        return (float) (value * Math.sin(0.2f + i / 5.0) * Math.cos(0.2f + i / 5.0) * Math.cos(0.4f + i / 2.0));
    }

    private static void time (Runnable act, String methodName) {
        long start = System.currentTimeMillis();
        act.run();
        long finish = System.currentTimeMillis();
        System.out.printf("%s method lasts %d ms%n", methodName, finish - start);
    }
}
