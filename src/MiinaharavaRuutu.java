import java.awt.Point;

public interface MiinaharavaRuutu {
	public boolean onkoMiina();

	public Point annaSijainti();

	public int annaRuudunLuku();
	
	public void asetaRuudunLuku( int luku );

	public RuudunTila annaTila();

	public int avaa();

	public boolean liputa();
	
	public void paljasta();

	/**
	 * Tarkistaa, onko miinaton ruutu auki
	 * 
	 * @return false, jos ruudussa on miina, mutta ruutu ei ole auki;
	 * 		   muutoin true	
	 */
	public boolean tarkista();
	
	public void liputaMiina();
}
