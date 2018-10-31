package test;
import java.util.function.Supplier;

public class Timing {
	public static void time(Runnable run) {
		time("Time",run);
	}

	public static void time(String preMessage, Runnable run) {
		long beg = System.nanoTime();
		run.run();
		long end = System.nanoTime();
		System.err.printf("%s : %d ms\n",preMessage,((end - beg) / 1000000));
	}
	
	
	public static <T> T time(Supplier<T> run){
		return time("Time",run);
	}
	
	public static <T> T time(String preMessage, Supplier<T> run){
		long beg = System.nanoTime();
		T result = run.get();
		long end = System.nanoTime();
		System.err.printf("%s : %d ms  => result : %s\n",preMessage,((end - beg) / 1000000),result);
		return result;
	}

	public static void main(String[] args) {
		time("Wait", () -> uneFonction());
		time("Code", () -> {
			for (int i = 0; i < 10000; i++) {
				i *= 2;
			}
		});
	}

	private static void uneFonction() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}

}
