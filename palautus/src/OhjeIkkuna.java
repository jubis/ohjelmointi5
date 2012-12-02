import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;


/**
 * Näppärä pikku ohjeikkuna toteutettuna JDialogilla
 * Hakee ohjeet tiedostosta ja näyttää ne
 */
@SuppressWarnings("serial")
public class OhjeIkkuna extends JDialog {
	private JTextArea ohjeAlue;
	
	public OhjeIkkuna() {
		this.setSize( 655, 560 );
		this.setLayout( null );
		
		this.luoOhjeAlue( luoOhjeet() );
		
		//this.pack();
		this.setLocationRelativeTo( null );
		this.setVisible( true );
	}
	
	private void luoOhjeAlue( String teksti ) {
		if( teksti == null ) {
			this.add( new JLabel( "Ohjeiden lataaminen epäonnistui" ) );
		} else {
			this.ohjeAlue = new JTextArea( teksti );
			
			this.ohjeAlue.setFont( new Font( "Comic Sans MS", Font.PLAIN, 14 ) );
			this.ohjeAlue.setEditable( false );
			this.ohjeAlue.setLineWrap( true );
			this.ohjeAlue.setWrapStyleWord( true );
			this.ohjeAlue.setBounds( 20, 20, 600, 460 );
			this.add( this.ohjeAlue );
		}
	}
	
	private static String luoOhjeet() {
		String teksti = "";
		try {
			InputStreamReader isr = 
						new InputStreamReader( new FileInputStream( "ohjeet.txt" ),
						                       "UTF-8" );
			BufferedReader br = new BufferedReader( isr );
			String rivi = "";
			while( ( rivi = br.readLine() ) != null ) {
				teksti += rivi;
			}
			br.close();
		} catch ( IOException e ) {
			return null;
		}
		
		teksti = teksti.replaceAll( "\\\\n", System.lineSeparator() );
		return teksti;
	}
}