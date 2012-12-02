import java.awt.Color;
import java.awt.Font;
import java.awt.Point;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * 
 * Graafinen JButtoneilla toteutettu versio MiinaharavaRuudusta
 * Sisältää MiinaharavaRuudun oikeappisen toteutuksen.
 * 
 * Dokumentaatio ylikirjoitetuista metodeista kts. MiinaharavaRuutu
 * 
 */
@SuppressWarnings("serial")
public class Ruutunappi extends JButton implements MiinaharavaRuutu {
	/**
	 * Apuvärejä vihjeen ja miinan todennäköisyyden näyttämiseen.
	 * (Melko) tasainen skaala vihreästä punaiseen
	 */
	private static final Color[] VIHJEEN_VARI = { new Color(0,255,0),
												  new Color(120,255,0),
												  new Color(255,255,0),
												  new Color(255,120,0),
												  new Color(255,0,0) };
	public final Point sijainti;
	private final boolean miina;
	private RuudunTila tila = RuudunTila.TYHJA;
	private int ruudunLuku;
	private final Peliruudukko ruudukko;
	
	/**
	 * Tallentaa parametrit. Ei muuta.
	 * 
	 * @param sijainti Ruudun sijainti ruudukossa
	 * @param miina Onko tässä miina
	 * @param ruudukko Ruudukko, jossa ollaan
	 */
	public Ruutunappi( Point sijainti, boolean miina, Peliruudukko ruudukko ) {
		this.ruudukko = ruudukko;
		this.sijainti = sijainti;
		this.miina = miina;
	}
	
	@Override
	public int annaRuudunLuku() {
		return this.ruudunLuku; 
	}
	
	@Override
	public void asetaRuudunLuku( int luku ) {
		this.ruudunLuku = luku;
	}
	
	@Override
	public RuudunTila annaTila() {
		return this.tila;
	}
	
	@Override
	public boolean onkoMiina() {
		return miina;
	}

	@Override
	public Point annaSijainti() {
		return this.sijainti;
	}
	
	@Override
	public int avaa() {
		if( this.tila == RuudunTila.LIPPU ) {
			return Peliruudukko.OLI_LIPUTETTU;
		} else if( this.tila == RuudunTila.AUKI ) {
			return Peliruudukko.OLI_JO_AUKI;
		} else {
			this.tila = RuudunTila.AUKI;
		}
		
		this.naytaAuki();
		
		if( this.miina ) {
			this.naytaMiina();
			return Peliruudukko.OLI_MIINA;
		} else {
			this.naytaVihje( ruudunLuku );
			this.ruudukko.asetaNaapureihinMiinanTodennakoisyys( this.sijainti );
			return ruudunLuku;
		}
	}

	@Override
	public boolean liputa() {
		if( this.tila == RuudunTila.TYHJA ) {
			this.tila = RuudunTila.LIPPU;
			this.naytaLippu( true );
			return true;
		} else if( this.tila == RuudunTila.LIPPU ) {
			this.tila = RuudunTila.TYHJA;
			this.naytaLippu( false );
		}
		return false;
	}
	
	@Override
	public void miinanTodennakoisyys( int todennakoisyys ) {
		this.naytaMiinanTodennakoisyys( todennakoisyys );
	}
	/**
	 * Indikoi miinan todennäköisyyttä värin avulla. 
	 * 
	 * @param todennakoisyys Todennäköisyys numerona
	 */
	private void naytaMiinanTodennakoisyys( int todennakoisyys ) {
		int varinNumero = todennakoisyys;
		if( varinNumero > VIHJEEN_VARI.length ) {
			varinNumero = VIHJEEN_VARI.length;
		} else if( todennakoisyys == 0 ) {
			return;
		}
		this.setBackground( VIHJEEN_VARI[ varinNumero - 1 ] );
	}
	
	/** 
	 * Puhdistaa ruudun siltä varalta, että sen ulkonäkö
	 * ei ole enää neitseellinen.
	 * 
	 * @param teksti Puhdistetaanko teksti
	 * @param tausta Puhdistetaanko tausta
	 */
	private void puhdista( boolean teksti, boolean tausta ) {
		if( teksti ) {
			this.setText( null );
			this.setForeground( null );
		}
		if( tausta ) this.setBackground( null );
	}
	
	/**
	 * Puhdistetaan ruudusta teksti ja tausta neitseelliseksi
	 */
	private void puhdista() {
		this.puhdista( true, true );
	}
	/**
	 * Näyttää ruudun auki (oli siellä mitä tahansa)
	 */
	private void naytaAuki() {
		this.setBorder( null );
		this.setBackground( Color.gray );
		// asetetaan teksti ruudun kokoiseksi, niin peli näyttää 
		// eri vaikeustasoilla järkevältä
		this.setFont( new Font( "Arial", Font.PLAIN, this.getSize().height ) );
	}
	/**
	 * Näytää tai poistaa ruudusta lipun 
	 * 
	 * @param nayta Näytetäänkö vai poistetaanko
	 */
	private void naytaLippu( boolean nayta ) {
		this.puhdista();
		if( nayta ) {
			this.asetaKuva( "lippu.gif" );
		} else {
			this.asetaKuva( null );
			// palautetaan ruutuun todennäköisyyden vihjeväri
			this.naytaMiinanTodennakoisyys( 
			                 this.ruudukko.laskeVihjeidenKeskiarvo( sijainti ) );
		}
	}
	/**
	 * Näppärä metodi pelin loppuun, jossa näytetään liputusten onnistumiset.
	 * Tää handlaa myös logiikan.
	 */
	private void naytaLipunOnnistuminen() {
		if( this.tila == RuudunTila.LIPPU ) {
			if( this.miina ) {
				this.setBackground( Color.green );
			} else {
				this.setBackground( Color.yellow );
			}
		}
	}
	/**
	 * Laittaa ruutuun miinan.
	 * Ei sisällä logiikkaa!
	 */
	private void naytaMiina() {
		this.puhdista( true, false );
		this.asetaKuva( "pommi.gif" );
	}
	/**
	 * Tätä kutsutaan, kun tiedetään, että ruutuun laitetaan
	 * vihjenumero tai ruutu on tyhjä.
	 * Ei sisällä (juurikaan) logiikkaa!
	 * @param vihjeluku
	 */
	private void naytaVihje( int vihjeluku ) {
		this.puhdista( true, false );
		if( vihjeluku > 0 ) {
			// asetetaan vihjeelle väri, joka ilmaisee ruudun "vaarallisuutta"
			this.setForeground( vihjeenVari( vihjeluku ) );
			this.setText( Integer.toString( vihjeluku ) );
		}
	}
	/**
	 * Antaa vihjetekstin värin vihjenumeron perusteella
	 * 
	 * @param vihje Väritettävän vihjeen numero
	 * 
	 * @return Vihjeelle kuuluva väri
	 */
	private static Color vihjeenVari( int vihje ) {
		int vari = vihje - 1;
		if( vihje >= VIHJEEN_VARI.length ) {
			vari = VIHJEEN_VARI.length - 1;
		}
		return VIHJEEN_VARI[vari];
	}
	/**
	 * Asettaa ruutuun halutun kuvatiedoston
	 * Kuvatiedostolla null poistaa kuvan.
	 * 
	 * @param tiedosto Haluttu kuvatiedosto tai null kuvan poistamiseksi
	 */
	private void asetaKuva( String tiedosto ) {
		if( tiedosto == null ) {
			this.setIcon( null );
		} else {
			Icon kuva = new ImageIcon( tiedosto );
			this.setIcon( kuva );
		}
	}

	@Override
	public void paljasta() {
		if( this.miina && this.tila == RuudunTila.AUKI ) {
			this.setBackground( Color.red );
		}
		if( this.miina && this.tila == RuudunTila.TYHJA ) {
			this.avaa();
		}
		this.naytaLipunOnnistuminen();
	}
	
	@Override 
	public boolean tarkista() {
		if( !this.miina &&
			this.tila != RuudunTila.AUKI ) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void liputaMiina() {
		naytaLipunOnnistuminen();
		if( this.miina && this.tila != RuudunTila.LIPPU ) {
			this.liputa();
		}
	}

}
