
public class Apu {
	public static double positiivinen( double luku ) {
		return (luku < 0) ? -luku : luku;
	}
	public static int positiivinenInt( int luku ) {
		return (int) positiivinen( luku );
	}
}
