import java.awt.Color;
import java.awt.Font;
import java.awt.Point;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;


@SuppressWarnings("serial")
public class Ruutunappi extends JButton implements MiinaharavaRuutu {
	private static final Color[] VIHJEEN_VARI = { new Color(0,255,0),
												  new Color(120,255,0),
												  new Color(255,255,0),
												  new Color(255,120,0),
												  new Color(255,0,0) };
	
	public final Point sijainti;
	private final boolean miina;
	private RuudunTila tila = RuudunTila.TYHJA;
	private int ruudunLuku;
	
	public Ruutunappi( Point sijainti, boolean miina ) {
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

	private void naytaAuki() {
		this.setBorder( null );
		this.setBackground( Color.gray );
		this.setFont( new Font( "Arial", Font.PLAIN, this.getSize().height ) );
	}
	
	private void naytaLippu( boolean nayta ) {
		if( nayta ) {
			this.asetaKuva( "lippu.gif" );
		} else {
			this.asetaKuva( null );
		}
	}
	
	private void naytaLipunOnnistuminen() {
		if( this.tila == RuudunTila.LIPPU ) {
			if( this.miina ) {
				this.setBackground( Color.green );
			} else {
				this.setBackground( Color.yellow );
			}
		}
	}

	private void naytaMiina() {
		this.asetaKuva( "pommi.gif" );
	}

	private void naytaVihje( int vihjeluku ) {
		if( vihjeluku > 0 ) {
			// asetetaan vihjeelle väri, joka ilmaisee ruudun "vaarallisuutta"
			this.setForeground( vihjeenVari( vihjeluku ) );
			this.setText( Integer.toString( vihjeluku ) );
		}
	}
	
	private static Color vihjeenVari( int vihje ) {
		int vari = vihje - 1;
		if( vihje >= VIHJEEN_VARI.length ) {
			vari = VIHJEEN_VARI.length - 1;
		}
		return VIHJEEN_VARI[vari];
	}
	
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
