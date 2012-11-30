import java.util.LinkedList;
import java.util.List;

public class MuutettavaIteraattori<T> {
	private List<T> alkiot;
	private int indeksi = 0;
	
	public MuutettavaIteraattori() {
		this.alkiot = new LinkedList<T>();
	}
	
	public MuutettavaIteraattori( List<T> alkiot ) {
		this.alkiot = alkiot;
	}

	public void lisaa( T alkio ) {
		this.alkiot.add( alkio );
	}
	
	public void lisaaKaikki( List<T> lisattavat ) {
		this.alkiot.addAll( lisattavat );
	}
	
	public boolean sisaltaa( T alkioko ) {
		return this.alkiot.contains( alkioko );
	}
	
	public boolean onkoSeuraava() {
		if( this.alkiot.size() > indeksi ) {
			return true;
		} else {
			return false;
		}
	}

	public T seuraava() {
		return this.alkiot.get( indeksi++ );
	}

}
