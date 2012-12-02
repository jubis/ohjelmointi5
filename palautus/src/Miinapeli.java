import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;

//TODO: ajastin, setFont on resize

/**
 * Peli pääluokka, jossa luodaan ikkuna ja pelin yleisiä fasiliteettejä.
 * Ei varsinaisesti sisällä pelilogikkaa.
 */
@SuppressWarnings("serial")
public class Miinapeli extends JFrame {
	/**
	 * Vaiheusasteet nätisti taulukossa
	 */
	public final static String[] VAIKEUSTASOT = { "Phuksi",
												  "Tupsu",
												  "Phabu",
												  "DI",
												  "Tohtori",
												  "Ikiteekkari" };
	
	private JPanel miinakentta;
	private JPanel tilapaneeli;
	private JLabel tilakentta;
	private JMenuBar valikkorivi;
	
	private JMenu valikkoPeli;
	private JMenu valikkoVaikeustaso;
	private JMenu valikkoApua;
	private JMenuItem valikkonappulaPeliAloitaAlusta;
	private JMenuItem valikkonappulaPeliLopeta;
	private JMenuItem valikkonappulaApuaOhjeet;

	private Peliruudukko ruudukko;
	private int vaikeustaso = 3;
	private boolean peliKaynnissa;
	
	/**
	 * Alustaa näkymän ja asettaa sen näkyville
	 */
	public Miinapeli() {
		super( "Miinapeli" );
		
		this.setDefaultCloseOperation( EXIT_ON_CLOSE );
		
		this.alustaNakyma();
		
		this.pack();
		this.setLocationRelativeTo( null );
	}
	
	public boolean onLoppunut() {
		return !this.peliKaynnissa;
	}
	
	/**
	 * Alustaa kaikki näkymän osat ja asettelee ne paikalleen
	 */
	private void alustaNakyma() {
		this.alustaMiinakentta();
		this.alustaTilapaneeli();
		this.alustaValikkorivi();
		
		this.asetteleNakyma();
		
	}
	
	/**
	 * Alustaa keskipaneelin eli miinakentän.
	 * Jos peliä ei ole vielä aloitettu, miinakentän paikalle tulee
	 * tyhjä paneeli
	 */
	private void alustaMiinakentta() {
		// jos peliä ei ole vielä aloitettu, luodaan tyhjä harmaa paneeli
		if( this.ruudukko == null ) {
			this.miinakentta = new JPanel();
		} else {
			this.miinakentta = new Pelipaneeli( this.ruudukko );
			new Peliohjain( this.ruudukko );
		}
		this.miinakentta.setPreferredSize( new Dimension( 500, 500 ) );
	}
	/**
	 * Alustaa pelin alareunassa olevan tilapaneelin ja sen sisällön
	 */
	private void alustaTilapaneeli() {
		this.tilapaneeli = new JPanel();
		this.tilakentta = new JLabel( "tila vakaa" );
		
		this.tilapaneeli.add( this.tilakentta );
	}
	/**
	 * Alustaa ikkunan yläosassa olevan valikkorin ja sen kaikki valikot.
	 * Asettaa myös kuuntelijat.
	 */
	private void alustaValikkorivi() {
		this.valikkorivi = new JMenuBar();
		
		// Peli-valikko
		this.valikkoPeli = new JMenu( "Peli" );
		this.valikkonappulaPeliAloitaAlusta = new JMenuItem( "Aloita alusta" );
		this.valikkonappulaPeliLopeta = new JMenuItem( "Lopeta" );
		
		this.valikkoPeli.add( this.valikkonappulaPeliAloitaAlusta );
		this.valikkoPeli.add( this.valikkonappulaPeliLopeta );
		
		this.valikkorivi.add( this.valikkoPeli );
		
		// Vaikeustaso-valikko
		this.valikkoVaikeustaso = new JMenu( "Vaikeustaso" );
		
		ButtonGroup bg = new ButtonGroup();
		for( int i = 0; i < VAIKEUSTASOT.length; i++ ){
			lisaaVaikeustasoValinta( VAIKEUSTASOT[ i ], 
			                         i + 2, 
			                         this.valikkoVaikeustaso,
			                         bg );
		}
		
		this.valikkorivi.add( this.valikkoVaikeustaso );
		
		// Apua-valikko
		this.valikkoApua = new JMenu( "Apua" );
		this.valikkonappulaApuaOhjeet = new JMenuItem( "Ohjeet" );
		
		this.valikkoApua.add( this.valikkonappulaApuaOhjeet );
		
		this.valikkorivi.add( this.valikkoApua );
		
		this.lisaaValikkoonKuuntelijat();
	}
	/**
	 * Apumetodi, jolla on helppo lisätä vaikaustasoja vaikeustasovalikkoon
	 * 
	 * @param teksti Tason nimi 
	 * @param vaikeustaso Tason vaikeus numerona
	 * @param valikko Valikko, johon valinta lisätään
	 * @param ryhma ButtonGroup, jonka osaksi kaikki vaikeustasonapit tulevat
	 * 				Tämän täytyy olla kaikille vaikeustasonapeille sama.
	 */
	private void lisaaVaikeustasoValinta( String teksti,
	                                             int vaikeustaso,
	                                             JMenu valikko, 
	                                             ButtonGroup ryhma ) {
		JRadioButtonMenuItem nappi = new JRadioButtonMenuItem( teksti );
		nappi.addActionListener( new VaikeustasoKuuntelija( vaikeustaso ) );
		
		if( vaikeustaso == this.vaikeustaso ) {
			nappi.setSelected( true );
		}
		
		ryhma.add( nappi );
		valikko.add( nappi );
	}
	/**
	 * Asettaa yksinkertaisille valikkonapeille kuuntelijat ja 
	 * määrittelee kuuntelijan toiminnan.
	 */
	private void lisaaValikkoonKuuntelijat() {
		// käytetään kuuntelijoihin anonyymejä sisäluokkia aina kun mahdollista
		// koska ne vähentävät turhan koodin määrää (ja ovat silti helppoja lukea)
		this.valikkonappulaPeliLopeta.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent arg0 ) {
				System.exit( 0 );
			}
		} );
		
		this.valikkonappulaPeliAloitaAlusta.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				aloitaAlusta();	
			}
		} );
		
		this.valikkonappulaApuaOhjeet.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				new OhjeIkkuna();
			}
		} );
	}
	/**
	 * Asettelee pääikkunan näkymän nätisti
	 */
	private void asetteleNakyma() {
		this.setLayout( new BorderLayout() );
		
		this.add( this.valikkorivi, BorderLayout.PAGE_START );
		this.add( this.miinakentta, BorderLayout.CENTER );
		this.add( this.tilapaneeli, BorderLayout.PAGE_END );
	}
	/**
	 * Aloittaa pelin alusta, eli muuttaa statukset yms 
	 * ja vaihtaa miinakentän paikalle uuden freshin miinakentän
	 */
	private void aloitaAlusta() {
		this.peliKaynnissa = true;
		this.tilakentta.setText( "Aloitit pelin alusta" );
		this.vaihdaMiinakentta();
	}
	/**
	 * Apumetodi, jolla miinakenttä vaihdetaan.
	 * 1. luo uuden ruudukon
	 * 2. poistaa vanhan kentän
	 * 3. alustaa ja asettelee uuden kentän
	 * 4. varmistaa, että ikkuna lasketaan ja piirretään uudestaan
	 */
	private void vaihdaMiinakentta() {
		this.ruudukko = Peliruudukko.luoPeliruudukko( this.vaikeustaso, this );
		this.remove( this.miinakentta );
		this.alustaMiinakentta();
		this.add( this.miinakentta, BorderLayout.CENTER );
		this.revalidate();
	}
	/**
	 * Tähän metodiin kuuluu ilmoittaa, 
	 * mikä on jäljellä olevien miinojen tilanne.
	 * Hoitaa tiedon eteenpäin tilapaneeliin.
	 * 
	 * @param miinat
	 * @param miinojaAlussa
	 */
	public void miinat( int miinat, int miinojaAlussa ) {
		String tila = "Miinoja jäljellä: " + miinat + "/" + 
					  miinojaAlussa;
		this.tilakentta.setText( tila );
	}
	/**
	 * Kutsutaan, kun pelaaja osuu miinaan
	 */
	public void miina() {
		this.peliLoppui( "Miina" );
	}
	/**
	 * Kutsutaan, kun syystä tai toisesta pelaaja on voittanut pelin
	 * (vastuu jätetään pelilogiikalle)
	 */
	public void voitto() {
		this.peliLoppui( "Voitit pelin" );
	}
	/**
	 * Yleinen metodi, jolla peli lopetetaan ja tulostetaan
	 * ilmoitus syyn kera tilapaneeliin
	 * 
	 * @param syy
	 */
	private void peliLoppui( String syy ) {
		this.peliKaynnissa = false;
		String tila = "Peli loppui";
		if( ! syy.equals( "" ) ) {
			tila = tila + " - " + syy;
		}
		this.tilakentta.setText( tila );
	}
	
	public static void main( String[] args ) {
		// luodaan Runnable-olio, joka käynnistää pelin
		// annetaan tämä olio tapahtumankäsittelysäikeelle suoritettavaksi
		SwingUtilities.invokeLater( new Runnable() {
			
			@Override
			public void run() {
				JFrame miinapeli = new Miinapeli();
				miinapeli.setVisible( true );
			}
		} );
	}
	
	/**
	 * Vaikeustasovalikkoa varten luotu apuluokka, jota käytetään
	 * vaikeustasonappien kuuntelemiseen.
	 * Asettaa suoraan vaikeustason, kun kuunneltava nappi valitaan.
	 */
	private class VaikeustasoKuuntelija implements ActionListener {
		private final int vaikeustaso;

		public VaikeustasoKuuntelija( int vaikeustaso ) {
			this.vaikeustaso = vaikeustaso;
		}
		
		@Override
		public void actionPerformed( ActionEvent e ) {
			Miinapeli.this.vaikeustaso = this.vaikeustaso;
		}
		
	}
}
