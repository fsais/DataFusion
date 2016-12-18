

// Mettre l'interface graphique dans une classe appart pourrait etre plus propre



/*package fusion;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class UserInterface {

	private static String sourceInfoFile = "Fusion\\sourceInfo.txt";
	private static Fusion fusion;
	private static JFrame guiFrame = new JFrame();

	public static void main (String[] args){
		//GUI to submit thresholds and start the process
		fusion = new Fusion();
		guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		guiFrame.setTitle("Parameters"); 
		guiFrame.setSize(350,350);  
		guiFrame.setLocationRelativeTo(null);
		final JPanel listPanel = new JPanel(); 
		listPanel.setVisible(true); 

		JButton sourceButton = new JButton("Edit Source Information");
		JButton rulesButton = new JButton("Edit Logic Rules");

		JLabel homogeneityLabel = new JLabel("Threshold for Homogeneity");
		final JTextField homogeneityTextfield = new JTextField("0.10");
		JLabel frequencyLabel = new JLabel("Threshold for Occurence Frequency");
		final JTextField frequencyTextfield = new JTextField("0.01");

		homogeneityTextfield.setInputVerifier(new MyInputVerifier());
		frequencyTextfield.setInputVerifier(new MyInputVerifier());

		JButton constructButton = new JButton("Construct graph");
		JButton loadButton = new JButton("Load graph");
		JButton fusionButton = new JButton("Data fusion");

		sourceButton.addActionListener(new ActionListener() {             
			public void actionPerformed(ActionEvent event) {                
				try {
					java.awt.Desktop.getDesktop().open(new File(sourceInfoFile));
				} catch (IOException ex) {
					Logger.getLogger(Fusion.class.getName()).log(Level.SEVERE, null, ex);
				}                
			}
		});

		constructButton.addActionListener(new ActionListener() {             
			private Fusion fusion;
			public void actionPerformed(ActionEvent event) {

				if (homogeneityTextfield.getText().length() != 0 && frequencyTextfield.getText().length() != 0){

					Fusion.homogeneityThreshold = Float.parseFloat(homogeneityTextfield.getText());
					Fusion.occurenceFrequencyThreshold = Float.parseFloat(frequencyTextfield.getText());
					try {

						fusion.constructGraph();

					} catch (FileNotFoundException ex) {
						Logger.getLogger(Fusion.class.getName()).log(Level.SEVERE, null, ex);
					} catch (IOException ex) {
						Logger.getLogger(Fusion.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
				else{
					//JOptionPane.showMessageDialog(this, "Please fill in all the fields!", defaultCloseOperation);
				}

			} });

		loadButton.addActionListener(new ActionListener() {             
			private Fusion fusion;
			public void actionPerformed(ActionEvent event) {
				fusion.loadGraph();
			}
		} );

		fusionButton.addActionListener(new ActionListener() {             
			private Fusion fusion;
			public void actionPerformed(ActionEvent event) {
				fusion.executeFusion();
			}
		} );

		listPanel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		listPanel.add(sourceButton, c);
		c.gridx = 0;
		c.gridy = 1;
		listPanel.add(rulesButton, c);
		c.gridx = 0;
		c.gridy = 2;
		listPanel.add(homogeneityLabel, c);
		c.gridx = 1;
		c.gridy = 2;
		listPanel.add(homogeneityTextfield, c);
		c.gridx = 0;
		c.gridy = 3;
		listPanel.add(frequencyLabel, c);
		c.gridx = 1;
		c.gridy = 3;
		listPanel.add(frequencyTextfield, c);
		c.gridx = 0;
		c.gridy = 4;
		listPanel.add(constructButton, c);
		c.gridx = 1;
		c.gridy = 4;
		listPanel.add(loadButton, c);
		c.gridx = 0;
		c.gridy = 5;
		listPanel.add(fusionButton, c);


		guiFrame.add(listPanel, BorderLayout.CENTER); 
		guiFrame.setVisible(true); 

	}
}
*/