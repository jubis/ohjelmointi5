import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Graafinen toteutus miinakentästä
 */
@SuppressWarnings("serial")
public class Pelipaneeli extends JPanel {
	
	public Pelipaneeli( Peliruudukko ruudukko ) {
		int korkeus = ruudukko.annaKorkeus();
		int leveys = ruudukko.annaLeveys();
		Map<Point, MiinaharavaRuutu> napit = ruudukko.annaNapit();
		
		//käytetään GridLayouttia koska helpompi ja tarkoituksenmukaisempi
		this.setLayout( new GridLayout( korkeus, leveys ) );
		
		this.asetaNapit( napit, korkeus, leveys );
	}
	
	private void asetaNapit( Map<Point, MiinaharavaRuutu> napit, 
	                         int korkeus, 
	                         int leveys ) {
		for( int y = 0; y < korkeus; y++ ) {
			for ( int x = 0; x < leveys; x++ ) {
				MiinaharavaRuutu ruutu = napit.get( new Point( x, y ) );
				this.add( (JButton)ruutu );
			}
		}
	}

	/**
	 * Palauttaa tämän käyttöliittymän kanssa käytettävän MiinaharavaRuudun.
	 * Pelipaneelin kanssa MiinaharavaRuutuina käytetään Ruutunappia.
	 * 
	 * @param sijainti Ruudun sijainti x- ja y-koordinaattien avulla
	 * @param miina Tuleeko ruutuun miina
	 * 
	 * @return Tietojen perusteella luotu Ruutunappi-olio
	 */
	public static MiinaharavaRuutu luoSopivaMiinaharavaRuutu( Point sijainti, 
	                                                          boolean miina,
	                                                          Peliruudukko ruudukko ) {
		return new Ruutunappi( sijainti, miina, ruudukko );
	}
}
