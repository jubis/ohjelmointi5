import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

/**
 * Pelilogiikka
 */
public class Peliruudukko {	
	/**
	 * Ruudun avaamiseen liittyvät virheet
	 */
	public static final int OLI_JO_AUKI = -1;
	public static final int OLI_LIPUTETTU = -2;
	public static final int OLI_MIINA = -3;
	
	private Map <Point,MiinaharavaRuutu> napit;
	private int	leveys;
	private int	korkeus;
	private int miinoja;
	private Miinapeli peli;
	private Random random = new Random();
	
	/**
	 * Alustaa Peliruudukon
	 * @param korkeus Ruudukon korkeus
	 * @param leveys Ruudukon leveys
	 * @param miinoja Miinojen määrä ruudukossa (ohjeellinen arvo)
	 * @param peli Viitaus pelin pääluokkaan
	 */
	public Peliruudukko( int korkeus, 
	                     int leveys, 
	                     int miinoja, 
	                     Miinapeli peli ) {
		this.leveys = Math.abs( leveys );
		this.korkeus = Math.abs( korkeus );
		this.miinoja = miinoja;
		this.peli = peli;
		
		this.napit = new HashMap <Point,MiinaharavaRuutu> ( leveys * korkeus );
		
		this.luoRuudut();
		this.asetaVihjeluvut();
		
		this.peli.miinat( this.miinoja, this.miinoja );
	}
	
	/**
	 * Factorymetodi, joka helpottaa ruudukon luomista pienellä magialla
	 * @param vaikeus Pelin vaikeus (1-7)
	 * @param peli Viittaus pelin pääluokkaan
	 * 
	 * @return Valmis peliruudukko-olio toiveittesi mukaan
	 */
	public static Peliruudukko luoPeliruudukko( int vaikeus, Miinapeli peli ) {
		int leveys = vaikeus*vaikeus;
		int korkeus = leveys;
		
		vaikeus = Math.abs( vaikeus );
		if( vaikeus > 7 ) {
			vaikeus = 7;
		}
		int miinoja = (int)Math.pow( vaikeus, 4 )/(10-vaikeus);
		return new Peliruudukko( korkeus, 
		                         leveys, 
		                         miinoja,
		                         peli );
	}
	/**
	 * Luo pelissä olevat ruudut
	 */
	private void luoRuudut() {
		// toteutetaan miinojen laskeminen olion avulla,
		// jotta miinan päättäminen voidaan kokonaan ulkoistaa,
		// mutta arpomisen hoitava metodi pysyy silti kartalla miinojen kokonaismäärästä
		Miinalaskuri laskuri = new Miinalaskuri( this.korkeus*this.leveys );
		for( int y = 0; y < this.korkeus; y++ ) {
			for ( int x = 0; x < this.leveys; x++ ) {
				Point sijainti = new Point( x, y );
				
				boolean miina = this.arvoMiina( laskuri );
				laskuri.vahennaRuutu();
				
				MiinaharavaRuutu nappi = 
					Pelipaneeli.luoSopivaMiinaharavaRuutu( sijainti, 
					                                       miina,
					                                       this );
				
				napit.put( sijainti, nappi ); 
			}
		}
		// korjataan attribuuttiin miinojen todellinen määrä
		this.miinoja = laskuri.miinoja();
	}
	/**
	 * Asettaa vihjenumerot, kun ruudut on jo luotu
	 */
	private void asetaVihjeluvut() {
		for( int y = 0; y < this.korkeus; y++ ) {
			for ( int x = 0; x < this.leveys; x++ ) {
				Point sijainti = new Point( x, y );
				int vihje = this.laskeNaapureidenMiinat( sijainti );
				this.napit.get( sijainti ).asetaRuudunLuku( vihje );
			}
		}
	}
	/**
	 * Arpoo, kuuluko seuraavaan ruutuun miinaa vai ei
	 * 
	 * @param laskuri Miinalaskuri-olio, jolla kommunikoidaan tämän metodin
	 * 				  ja ruutujen luojan välillä 
	 * 
	 * @return Tuleeko miinaa vai ei
	 */
	private boolean arvoMiina( Miinalaskuri laskuri ) {
		int miinojaJaljella = this.miinoja-laskuri.miinoja();
		//lasketaan miinan todennäköisyys joka kerta uudestaan,
		//jotta saadaan lopulta mahdollisimman oikea määrä miinoja
		double miinaTod = (double)(miinojaJaljella) / (laskuri.ruutuja());
		if( this.random.nextDouble() < miinaTod && 
			laskuri.miinoja() < this.miinoja ) {
			laskuri.lisaaMiina();
			return true;
		} else {
			return false;
		}
	}
	
	public int annaKorkeus() {
		return this.korkeus;
	}
	public int annaLeveys() {
		return this.leveys;
	}
	public int annaKoko() {
		return this.leveys*this.korkeus;
	}
	
	public MiinaharavaRuutu annaRuutu( int x, int y ) {
		return this.annaRuutu( new Point( x, y ) );
	}
	/**
	 * Apumetodi, olet itse vastuussa pyyntösi onnistumisesta
	 * 
	 * @param sijainti Ruudun sijainti ruudukossa
	 * 
	 * @return Haluttu ruutu
	 */
	public MiinaharavaRuutu annaRuutu( Point sijainti ) {
		MiinaharavaRuutu ruutu = this.napit.get( sijainti );
		if( ruutu != null ) {
			return ruutu;
		} else {
			throw new IndexOutOfBoundsException();
		}
	}
	/**
	 * Antaa ruudun vihjenumeron. Olet itse vastuussa pyyntösi
	 * mahdottomuudesta.
	 * 
	 * @param x Koordinaatti x
	 * @param y Koordinaatti y
	 * 
	 * @return Vihjenumero halutusta ruudusta
	 */
	public int annaVihjanumero( int x, int y ) {
		MiinaharavaRuutu ruutu = this.napit.get( new Point( x, y ) );
		if( ruutu != null ) {
			return ruutu.annaRuudunLuku();
		} else {
			throw new IndexOutOfBoundsException();
		}
	}
	
	public boolean onAuki( int x, int y ) {
		return this.vertaaTilaa( x, y, RuudunTila.AUKI );
	}
	
	public boolean onLiputettu( int x, int y ) {
		return this.vertaaTilaa( x, y, RuudunTila.LIPPU  );
	}
	
	public boolean onMiina( int x, int y ) {
		return this.annaRuutu( x, y ).onkoMiina();
	}
	/**
	 * Näppärä apumetodi saantimetodien pelkistämiseksi
	 */
	private boolean vertaaTilaa( int x, int y, RuudunTila tila ) {
		if( this.annaRuutu( x, y ).annaTila() == tila ) {
			return true;
		} else {
			return false;
		}
	}
	
	public Map<Point,MiinaharavaRuutu> annaNapit() {
		return this.napit;
	}
	
	public int avaa( int x, int y ) {
		return this.avaa( this.annaRuutu( x, y ) );
	}
	/**
	 * Pelilogiikka ruudun avaamiseksi
	 * 
	 * @param ruutu Avattava ruutu
	 * 
	 * @return Virheilmoitus tai ruudun vihjeluku
	 */
	public int avaa( MiinaharavaRuutu ruutu ) {
		if( peli.onLoppunut() ) {
			return -4;
		}
		int vastaus = ruutu.avaa();
		if( vastaus == 0 ) {
			(new Avaaja( ruutu.annaSijainti() )).kaynnista();
		} else if( vastaus == Peliruudukko.OLI_MIINA ) {
			this.miina();
		}
		
		if( this.peliVoitettu() ) {
			this.voitto();
		}
		return vastaus;
	}
	
	public boolean asetaLippu( int x, int y ) {
		return this.asetaLippu( this.annaRuutu( x, y ) );
	}
	/**
	 * Pelilogiikka ruudun liputtamiseksi ja lipun poistamiseksi
	 * 
	 * @param ruutu Liputettava ruutu
	 * 
	 * @return Liputettiinko vai poistettiinko lippu
	 */
	public boolean asetaLippu( MiinaharavaRuutu ruutu ) {
		if( peli.onLoppunut() ) {
			return false;
		}
		boolean vastaus =  ruutu.liputa();
		this.peli.miinat( this.laskeMiinojaJaljella(), this.miinoja );
		return vastaus;
	}
	/**
	 * Kutsutaan, kun osutaan miinaan
	 */
	private void miina() {
		this.paljastaNapit();
		this.peli.miina();
	}
	/**
	 * Kutsutaan, kun halutaan lopettaa peli voittoon
	 */
	private void voitto() {
		this.liputaMiinat();
		this.peli.voitto();
	}
	/**
	 * Paljastaa ruutujen miinat tappion yhteydessä
	 */
	private void paljastaNapit() {
		for( Entry<Point,MiinaharavaRuutu> ruutu : this.napit.entrySet() ) {
			ruutu.getValue().paljasta();
		}
	}
	/**
	 * Liputtaa miinat voiton yhteydessä
	 */
	private void liputaMiinat() {
		for( Entry<Point,MiinaharavaRuutu> ruutu : this.napit.entrySet() ) {
			ruutu.getValue().liputaMiina();
		}
	}
	/**
	 * Tarkistaa, onko peli voitettu
	 * ts. onko kaikki miinattomat ruudut jo avattu
	 * @return
	 */
	private boolean peliVoitettu() {
		for( Entry<Point,MiinaharavaRuutu> ruutu : this.napit.entrySet() ) {
			if( ! ruutu.getValue().tarkista() ) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Laskee kuinka monta miinaa on vielä liputtamatta.
	 * HUOM! Liputetuksi miinaksi lasketaan myös väärin liputetut.
	 * 
	 * @return Miinojen kokonaismäärä vähennettynä lippujen määrä
	 */
	private int laskeMiinojaJaljella() {
		int laskuri = 0;
		for( Entry<Point,MiinaharavaRuutu> ruutu : this.napit.entrySet() ) {
			if( ruutu.getValue().annaTila() == RuudunTila.LIPPU ) {
				laskuri++;
			}
		}
		return this.miinoja - laskuri;
	}
	/**
	 * Laskee miinat naapuriruuduista
	 * @param sijainti Keskusta
	 * @return Miinojen määrä
	 */
	private int laskeNaapureidenMiinat( Point sijainti ) {
		int laskuri = 0;
		for( MiinaharavaRuutu ruutu : this.annaNaapurit( sijainti ) ) {
			if( ruutu.onkoMiina() ) {
				laskuri++;
			}
		}
		return laskuri;
	}
	/**
	 * Kertoo naapuriruuduille, millä todennäköisyydellä niissä on miinat
	 * @param sijaintiKeskusta
	 */
	public void asetaNaapureihinMiinanTodennakoisyys( Point sijainti ) {
		for( MiinaharavaRuutu ruutu : this.annaNaapurit( sijainti ) ) {
			if( ruutu.annaTila() == RuudunTila.TYHJA ) {
				ruutu.miinanTodennakoisyys( this.laskeVihjeidenKeskiarvo( ruutu.annaSijainti() ) );
			}
		}
	}
	/**
	 * Laskee vihjeiden keskiarvot miinan todennäköisyyttä varten
	 * @param sijainti
	 * @return
	 */
	public int laskeVihjeidenKeskiarvo( Point sijainti ) {
		double vihjeluvutSumma = 0;
		int maara = 0;
		List<MiinaharavaRuutu> tutkittavat = this.annaNaapurit( sijainti );
		tutkittavat.add( this.annaRuutu( sijainti ) );
		for( MiinaharavaRuutu ruutu : tutkittavat ) {
			//System.out.println( ruutu );
			if( ruutu.annaTila() == RuudunTila.AUKI ) {
				vihjeluvutSumma += ruutu.annaRuudunLuku();
				maara++;
			}
		}
		if( maara > 0 ) {
			System.out.println( vihjeluvutSumma );
			return (int)Math.ceil( vihjeluvutSumma / maara );
		} else {
			return 0;
		}
	}
	
	private List<MiinaharavaRuutu> annaNaapurit( Point sijainti ) {
		List<MiinaharavaRuutu> naapurit = new ArrayList<MiinaharavaRuutu>();
		for( Suunta s : Suunta.values() ) {
			MiinaharavaRuutu ruutu = annaRuutuSuunnassa( (Point)(sijainti), s );
			if( ruutu != null ) {
				naapurit.add( ruutu );
			}
		}
		return naapurit;
	}
	/**
	 * Apumetodi naapureiden tarkkaan hakemiseen
	 */
	private MiinaharavaRuutu annaRuutuSuunnassa( Point sijainti, Suunta s ) {
		Point vektori = annaSuuntaNaapuriin( s );
		Point uusiSijainti = (Point)sijainti.clone();
		uusiSijainti.translate( vektori.x, vektori.y );
		return this.napit.get( uusiSijainti );
	}
	/**
	 * Apumetodi, joka muuttaa suunnan Point-tyyppiseksi "vektoriksi"
	 * @param s
	 * @return
	 */
	private static Point annaSuuntaNaapuriin( Suunta s ) {
		switch( s ) {
			case ETELA:
				return new Point( 0, 1 );
			case ITA:
				return new Point( 1, 0 );
			case KAAKKO:
				return new Point( 1, 1 );
			case KOILINEN:
				return new Point( 1, -1 );
			case LANSI:
				return new Point( -1, 0 );
			case LOUNAS:
				return new Point( -1, 1 );
			case LUODE:
				return new Point( -1, -1 );
			case POHJOINEN:
				return new Point( 0, -1 );
			default:
				throw new IllegalArgumentException();
		}
	}
	
	private enum Suunta {
		POHJOINEN,
		KOILINEN,
		ITA,
		KAAKKO,
		ETELA,
		LOUNAS,
		LANSI,
		LUODE;
	}
	
	/**
	 * Apuluokka tyhjien ruutujen automaattiseen avaamiseen
	 */
	private class Avaaja {
		private List<Point> loydetyt = new ArrayList<Point>();
		
		public Avaaja( Point aloitus ) {
			this.loydetyt.add( aloitus );
		}
		
		public void kaynnista() {
			this.kayLapiTyhjiaRuutuja();
		}
		
		private void kayLapiTyhjiaRuutuja() {
			Point sijainti;
			for ( int i = 0; i < this.loydetyt.size(); i++ ) {
				sijainti = this.loydetyt.get( i );
				this.loydetyt.addAll( this.avaaTyhjatNaapurit( sijainti ) );
			}
		}
		
		private List<Point> avaaTyhjatNaapurit( Point keskus ) {
			List<Point> avoimetNaapurit = new LinkedList<Point>();
			Point sijainti;
			for( MiinaharavaRuutu ruutu : annaNaapurit( keskus ) ) {
				if( ruutu == null ) continue;
				sijainti = ruutu.annaSijainti();
				if( ! this.loydetyt.contains( sijainti ) ) {
					ruutu.avaa();
					if( ruutu.annaRuudunLuku() == 0 ) {
						avoimetNaapurit.add( sijainti );
					}
				}
			}
			
			return avoimetNaapurit;
		}
	}
	
	/**
	 * Apuluokka miinan päättäjän ja ruutujen luojan väliseen kommunikaatioon
	 */
	private class Miinalaskuri {
		private int miinat;
		private int ruutujaJaljella;
		
		public Miinalaskuri( int ruutujaYhteensa ) {
			this.ruutujaJaljella = ruutujaYhteensa;
		}
		
		public int miinoja() {
			return this.miinat;
		}
		
		public void lisaaMiina() {
			this.miinat++;
		}
		
		public int ruutuja() {
			return this.ruutujaJaljella;
		}
		
		
		public void vahennaRuutu() {
			this.ruutujaJaljella--;
		}
	}
}
