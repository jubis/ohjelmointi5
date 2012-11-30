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

//TODO: ajastin, setFont on resize, miinan todennäköisyys
asd

@SuppressWarnings("serial")
public class Miinapeli extends JFrame {
	private final static String[] VAIKEUSTASOT = { "Phuksi",
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
	
	private void alustaNakyma() {
		this.alustaMiinakentta();
		this.alustaTilapaneeli();
		this.alustaValikkorivi();
		
		this.asetteleNakyma();
		
	}
	
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
	private void alustaTilapaneeli() {
		this.tilapaneeli = new JPanel();
		this.tilakentta = new JLabel( "tila vakaa" );
		
		this.tilapaneeli.add( this.tilakentta );
	}
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
	
	private void asetteleNakyma() {
		this.setLayout( new BorderLayout() );
		
		this.add( this.valikkorivi, BorderLayout.PAGE_START );
		this.add( this.miinakentta, BorderLayout.CENTER );
		this.add( this.tilapaneeli, BorderLayout.PAGE_END );
	}
	
	private void aloitaAlusta() {
		this.peliKaynnissa = true;
		this.tilakentta.setText( "Aloitit pelin alusta" );
		this.vaihdaMiinakentta();
	}
	
	private void vaihdaMiinakentta() {
		this.ruudukko = Peliruudukko.luoPeliruudukko( this.vaikeustaso, this );
		this.remove( this.miinakentta );
		this.alustaMiinakentta();
		this.add( this.miinakentta, BorderLayout.CENTER );
		this.revalidate();
	}
	
	public void miinat( int miinat, int miinojaAlussa ) {
		String tila = "Miinoja jäljellä: " + miinat + "/" + 
					  miinojaAlussa;
		this.tilakentta.setText( tila );
	}
	
	public void miina() {
		this.peliLoppui( "Miina" );
	}
	
	public void voitto() {
		this.peliLoppui( "Voitit pelin" );
	}
	
	private void peliLoppui( String syy ) {
		this.peliKaynnissa = false;
		String tila = "Peli loppui";
		if( ! syy.equals( "" ) ) {
			tila = tila + " - " + syy;
		}
		this.tilakentta.setText( tila );
	}
	
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( new Runnable() {
			
			@Override
			public void run() {
				JFrame miinapeli = new Miinapeli();
				miinapeli.setVisible( true );
			}
		} );
	}
	
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
