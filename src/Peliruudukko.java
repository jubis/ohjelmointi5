import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class Peliruudukko {	
	public static final int OLI_JO_AUKI = -1;
	public static final int OLI_LIPUTETTU = -2;
	public static final int OLI_MIINA = -3;
	
	private Map <Point,MiinaharavaRuutu> napit;
	private int	leveys;
	private int	korkeus;
	private int miinoja;
	private Miinapeli peli;
	private Random random = new Random();
	
	public Peliruudukko( int korkeus, 
	                     int leveys, 
	                     int miinoja, 
	                     Miinapeli peli ) {
		this.leveys = Apu.positiivinenInt( leveys );
		this.korkeus = Apu.positiivinenInt( korkeus );
		this.miinoja = miinoja;
		this.peli = peli;
		
		this.napit = new HashMap <Point,MiinaharavaRuutu> ( leveys * korkeus );
		
		this.luoRuudut();
		this.asetaVihjeluvut();
		
		this.peli.miinat( this.miinoja, this.miinoja );
	}
	
	public static Peliruudukko luoPeliruudukko( int vaikeus, Miinapeli peli ) {
		int leveys = vaikeus*vaikeus;
		int korkeus = leveys;
		
		vaikeus = Apu.positiivinenInt( vaikeus );
		if( vaikeus > 7 ) {
			vaikeus = 7;
		}
		int miinoja = (int)Math.pow( vaikeus, 4 )/(10-vaikeus);
		return new Peliruudukko( korkeus, 
		                         leveys, 
		                         miinoja,
		                         peli );
	}
	
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
					                                       miina );
				
				napit.put( sijainti, nappi ); 
			}
		}
		// korjataan attribuuttiin miinojen todellinen määrä
		this.miinoja = laskuri.miinoja();
	}
	
	private void asetaVihjeluvut() {
		for( int y = 0; y < this.korkeus; y++ ) {
			for ( int x = 0; x < this.leveys; x++ ) {
				Point sijainti = new Point( x, y );
				int vihje = this.laskeNaapureidenMiinat( sijainti );
				this.napit.get( sijainti ).asetaRuudunLuku( vihje );
			}
		}
	}
	
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
	
	public int annaMaksimipisteet() {
		return this.korkeus*this.leveys - this.miinoja;
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
		MiinaharavaRuutu ruutu = this.napit.get( new Point( x, y ) );
		if( ruutu != null ) {
			return ruutu;
		} else {
			throw new IndexOutOfBoundsException();
		}
	}
	
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
	
	public boolean asetaLippu( MiinaharavaRuutu ruutu ) {
		if( peli.onLoppunut() ) {
			return false;
		}
		boolean vastaus =  ruutu.liputa();
		this.peli.miinat( this.laskeMiinojaJaljella(), this.miinoja );
		return vastaus;
	}
	
	private void miina() {
		this.paljastaNapit();
		this.peli.miina();
	}
	
	private void voitto() {
		this.liputaMiinat();
		this.peli.voitto();
	}
	
	private void paljastaNapit() {
		for( Entry<Point,MiinaharavaRuutu> ruutu : this.napit.entrySet() ) {
			ruutu.getValue().paljasta();
		}
	}
	
	private void liputaMiinat() {
		for( Entry<Point,MiinaharavaRuutu> ruutu : this.napit.entrySet() ) {
			ruutu.getValue().liputaMiina();
		}
	}
	
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
	
	private int laskeNaapureidenMiinat( Point sijainti ) {
		int laskuri = 0;
		for( Suunta s : Suunta.values() ) {
			MiinaharavaRuutu ruutu = annaRuutuSuunnassa( (Point)(sijainti), s );
			if( ruutu != null && ruutu.onkoMiina() ) {
				laskuri++;
			}
		}
		return laskuri;
	}
	
	private MiinaharavaRuutu annaRuutuSuunnassa( Point sijainti, Suunta s ) {
		Point vektori = annaSuuntaNaapuriin( s );
		Point uusiSijainti = (Point)sijainti.clone();
		uusiSijainti.translate( vektori.x, vektori.y );
		return this.napit.get( uusiSijainti );
	}
	
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
	
	private class Avaaja {
		private List<Point> loydetyt = new ArrayList<Point>();
		
		public Avaaja( Point aloitus ) {
			this.loydetyt.add( aloitus );
		}
		
		public void kaynnista() {
			this.kayLapiListaa();
		}
		
		private void kayLapiListaa() {
			Point sijainti;
			for ( int i = 0; i < this.loydetyt.size(); i++ ) {
				sijainti = this.loydetyt.get( i );
				this.loydetyt.addAll( this.avaaTyhjatNaapurit( sijainti ) );
			}
		}
		
		private List<Point> avaaTyhjatNaapurit( Point keskus ) {
			List<Point> avoimetNaapurit = new LinkedList<Point>();
			MiinaharavaRuutu ruutu;
			Point sijainti;
			for( Suunta suunta : Suunta.values() ) {
				ruutu = annaRuutuSuunnassa( keskus, suunta );
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
