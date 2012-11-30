import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Peliohjain {
	public Peliohjain( Peliruudukko ruudukko ) {
		this.asetaKuuntelijat( ruudukko );
	}
	
	private void asetaKuuntelijat( Peliruudukko ruudukko ) {
		MiinaruudunKuutelija kuuntelija = new MiinaruudunKuutelija( ruudukko );
		for( int y = 0; y < ruudukko.annaKorkeus(); y++ ) {
			for ( int x = 0; x < ruudukko.annaLeveys(); x++ ) {
				Ruutunappi nappi = (Ruutunappi)ruudukko.annaRuutu( x, y );
				nappi.addMouseListener( kuuntelija );
			}
		}
	}

	// tehdään hiirenkuuntelija omaan luokkaansa
	// 1. jotta se voi periä MouseAdapterin (joka vähentää turhaa ylikirjoittamista)
	private class MiinaruudunKuutelija extends MouseAdapter {
		private final Peliruudukko ruudukko;
		
		public MiinaruudunKuutelija( Peliruudukko ruudukko ) {
			this.ruudukko = ruudukko;
		}

		@Override
		public void mouseClicked( MouseEvent e ) {
			if( e.getButton() == MouseEvent.BUTTON1 ) {
				this.ruudukko.avaa( (Ruutunappi)(e.getComponent()) );
			} else if( e.getButton() == MouseEvent.BUTTON3 ) {
				this.ruudukko.asetaLippu( (Ruutunappi)(e.getComponent()) );
			}
		}
	}
}
