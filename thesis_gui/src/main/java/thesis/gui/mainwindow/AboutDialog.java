package thesis.gui.mainwindow;

import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import thesis.core.utilities.CoreUtils;
import thesis.core.utilities.VersionID;

@SuppressWarnings("serial")
public class AboutDialog extends JDialog
{
   private final String wikiAddr;

   public AboutDialog(JFrame parent)
   {
      super(parent, "Thesis Simulator - About", true);
      wikiAddr = "https://github.com/tansir1/thesis/wiki";

      setLayout(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.anchor = GridBagConstraints.LINE_START;

      add(new JLabel("Version: "), gbc);
      gbc.gridx++;
      gbc.ipadx = 10;
      VersionID version = CoreUtils.loadVersionID();
      add(new JLabel(version.toString()), gbc);

      gbc.gridx = 0;
      gbc.gridy++;
      gbc.gridwidth = 2;
      add(new JLabel("Written by Charles Tullock in partial fulfillment of doctoral degree requirements."), gbc);

      gbc.gridy++;
      add(new JLabel("Documentation is available on the wiki at\n " + wikiAddr), gbc);

      gbc.gridy++;
      gbc.anchor = GridBagConstraints.CENTER;
      JButton openWikiBtn = new JButton("Open wiki in browser");
      openWikiBtn.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            openBrowserToWiki();
         }
      });
      add(openWikiBtn, gbc);

      this.pack();
   }

   private void openBrowserToWiki()
   {
      if (Desktop.isDesktopSupported())
      {
         try
         {
            Desktop.getDesktop().browse(new URI(wikiAddr));
         }
         catch (IOException e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
         catch (URISyntaxException e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
      else
      {
         String msg = "Your desktop does not support browser integration with java. "
               + "To view the wiki manually navigate to " + wikiAddr;
         JOptionPane.showMessageDialog(this, msg, "Failed to open wiki", JOptionPane.ERROR_MESSAGE);
      }
   }

}
