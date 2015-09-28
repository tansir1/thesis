package thesis.gui.mainwindow;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class AboutDialog extends JDialog
{
   public AboutDialog(JFrame parent)
   {
      super(parent, "Thesis Simulator - About", true);
      setLayout(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.anchor = GridBagConstraints.LINE_START;
      
      add(new JLabel("Version:"), gbc);
      gbc.gridx++;
      gbc.ipadx = 10;
      //TODO Load version numbers dynamically from somewhere
      add(new JLabel("0.0.0.0"), gbc);
      
      gbc.gridx = 0;
      gbc.gridy++;
      gbc.gridwidth = 2;
      add(new JLabel("Written by Charles Tullock in partial fulfillment of doctoral degree requirements."), gbc);
   }
   
   
}
