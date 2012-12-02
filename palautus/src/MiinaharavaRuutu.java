import java.awt.Point;

/**
 * 
 * Yleinen rajapinta, jonka tässä pelilogiissa käytettävien
 * miinaharavaruutujen on toteutettava.
 * Minkäänlaista valmista logiikkaa ei valitettavasti ole olemassa.
 * 
 * Lue kuvaukset tarkkaan ja koodaa logiikka hyvin.
 * Tsemppiä!
 *
 */
public interface MiinaharavaRuutu {
	/**
	 * @return onko tässä ruudussa miina vai ei
	 */
	public boolean onkoMiina();
	/**
	 * @return antaa ruudun sijainnin miinaharavaruudukossa
	 */
	public Point annaSijainti();
	/**
	 * @return antaa ruudun vihjeluvun
	 */
	public int annaRuudunLuku();
	/**
	 * Ottaa vastaan ruudun vihjeluvun peliruudukolta
	 * 
	 * @param luku Vihjeluku
	 */
	public void asetaRuudunLuku( int luku );
	/**
	 * @return Palauttaa ruudun tilan RuudunTila-enumeraation avulla
	 */
	public RuudunTila annaTila();
	
	/**
	 * Tätä metodia kutsutaan aina, kun käyttäjä edes yrittää avata ruudun.
	 * Palauttaa varoituksen, jos ruutu oli jo auki, siinä oli miina tai
	 * se oli liputettu. Varoitukset kts. Peliruudukko.
	 * 
	 * Huolehtii myös ruudun ulkonäön muuttamisen esim. näytäXXX()-metodien avulla.
	 * 
	 * Tämän metodin oikeaoppinen toteutus kutsuu myös Peliruudukon metodia
	 * asetaNaapureihinMiinanTodennakoisyys( int todnak )
	 * 
	 * @return Ruudun vihjeluku tai varoitus
	 */
	public int avaa();

	
	/**
	 * Tätä kutsutaan aina, kun käyttäjä yrittää liputtaa ruudun.
	 * Jos ruutu on koskematon, se liputetaan.
	 * Jos ruutu on liputettu, se palautetaan koskemattomaski.
	 * Muuta ei saa tehdä.
	 * 
	 * Huolehtii ruudun ulkonäön muuttamisesta esim. näytäLippu()-metodin avulla.
	 * 
	 * @return Liputettiinko?
	 */
	public boolean liputa();
	
	/**
	 * Tätä metodia kutsutaan, kun ruudun halutaan indikoivan miinan
	 * todennäköisyyttä tässä ruudussa. 
	 * Välitä tämä tieto käyttäjälle jotenkin.
	 * 
	 * @param todennakoisyys
	 */
	public void miinanTodennakoisyys( int todennakoisyys );
	
	/**
	 * Paljastaa miinat pelin lopussa. Mielellään kertoo käyttäjälle
	 * mikä oli ensimmäinen miina.
	 */
	public void paljasta();

	/**
	 * Tarkistaa, onko miinaton ruutu auki
	 * 
	 * @return false, jos ruudussa on miina, mutta ruutu ei ole auki;
	 * 		   muutoin true	
	 */
	public boolean tarkista();
	
	/**
	 * Tätä kutsutaan, kun peli on voitettu ja kaikki miinat liputetaan
	 * automaattisesti. 
	 * Hyvään toteutukseen kuuluu, että kerrot käyttäjälle,
	 * mitkä liput hän oli itse jo laittanut.
	 */
	public void liputaMiina();
}
