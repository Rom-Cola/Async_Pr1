import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ArrayProcessingTask implements Callable<int[]> {
    private final int[] numbers;
    private final int start;
    private final int end;

    public ArrayProcessingTask(int[] numbers, int start, int end) {
        this.numbers = numbers;
        this.start = start;
        this.end = end;
    }

    @Override
    public int[] call() {
        List<Integer> products = new ArrayList<>();
        for (int i = start; i < end; i += 2) {
            if (i == numbers.length - 1) {
                break;
            }
            products.add(numbers[i] * numbers[i + 1]);
        }
        return products.stream().mapToInt(Integer::intValue).toArray();
    }
}