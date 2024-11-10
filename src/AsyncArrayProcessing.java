import java.util.*;
import java.util.concurrent.*;

public class AsyncArrayProcessing {
    private static final int DEFAULT_LOWER_BOUND = 0;
    private static final int DEFAULT_UPPER_BOUND = 100;
    private static final int NUM_THREADS = 4;

    public static void main(String[] args) {
        System.out.println("1. Діапазон за замовчуванням");
        System.out.println("2. Задати діапазон");
        Scanner scanner = new Scanner(System.in);
        int choice, lowerBound, upperBound;

        try {
            choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    lowerBound = DEFAULT_LOWER_BOUND;
                    upperBound = DEFAULT_UPPER_BOUND;
                    break;
                case 2:
                    System.out.println("Введіть нижню межу:");
                    lowerBound = scanner.nextInt();
                    System.out.println("Введіть верхню межу:");
                    upperBound = scanner.nextInt();
                    break;
                default:
                    System.out.println("Ви вибрали неіснуючий варіант");
                    return;
            }
        } catch (Exception e) {
            System.out.println("Помилка! Наступного разу введіть число.");
            return;
        }

        int arraySize = new Random().nextInt(21) + 40;
        int[] numbers = new Random().ints(arraySize, lowerBound, upperBound + 1).toArray();
        int chunkSize = (int) Math.ceil(numbers.length / (double) NUM_THREADS);

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        List<Future<int[]>> futures = new ArrayList<>();

        long startTime = System.nanoTime();

        System.out.println("Початкові дані: ");
        System.out.println("Вхідний масив: \n" + Arrays.toString(numbers));

        for (int i = 0; i < numbers.length; i += chunkSize + 1) {
            int end = Math.min(i + chunkSize, numbers.length);
            Future<int[]> future = executor.submit(new ArrayProcessingTask(numbers, i, end));
            futures.add(future);
        }

        CopyOnWriteArrayList<Integer> totalProducts = new CopyOnWriteArrayList<>();

        for (int i = 0; i < futures.size(); i++) {
            try {
                while (!futures.get(i).isDone()) {
                    System.out.printf("Завдання №%d ще виконується...\n", i + 1);
                }

                int[] partialResult = futures.get(i).get();
                for (int product : partialResult) {
                    totalProducts.add(product);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < futures.size(); i++) {
            if (futures.get(i).isCancelled()) {
                System.out.printf("Завдання №%d було скасовано.\n", i + 1);
            } else if (futures.get(i).isDone()) {
                System.out.printf("Завдання №%d було успішно виконано.\n", i + 1);
            }
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;

        System.out.println("Попарні добутки: " + totalProducts);
        System.out.println("Час виконання: " + duration + " ms");

        executor.shutdown();
    }


}
