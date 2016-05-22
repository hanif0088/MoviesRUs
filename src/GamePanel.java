import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.sql.*;
import javax.swing.ImageIcon;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

public class GamePanel extends JPanel implements ActionListener
{
	JTextField searchField;
	JButton searchButton;
	JComboBox<String> platformSelection, genreSelection;

	String[] genreNames = {"All", "Action","Sport","Fighting","Role-Playing", "Adventure","Racing"};
	String[] platformFilters ={"All", "Xbox 360", "Xbox One", "PS3","PS4", "Wii", "Wii U","PC"};

	JLabel headerLabel,
	       searchByLabel,
	       genreLabel,
	       searchLabel;

	JPanel compPanel,
		   searchButtonPanel,
		   genrePanel,
		   platformPanel,
		   searchTFPanel;

	Statement 	myStatement;
	String 		myUsername;
	String 		genreString = null;
	String 		platformString = null;
	String 		keywordString = null;

	//====================================================================================
	GamePanel(Statement urStatement,String urUsername)
	{
		myStatement = urStatement;
		myUsername = urUsername;

		compPanel = new JPanel(new GridLayout(8,1));
		searchButtonPanel = new JPanel();
		genrePanel = new JPanel();
		platformPanel = new JPanel();
		searchTFPanel = new JPanel();

		searchField = new JTextField(50);

		searchButton = new JButton("Search",new ImageIcon("iconSearch.png"));
		searchButton.addActionListener(this);
		searchButtonPanel.add(searchButton);

		headerLabel = new JLabel("Search for games by keywords, genres, and platforms.", JLabel.CENTER);
		searchByLabel = new JLabel("Choose a Platform:");
		genreLabel = new JLabel("Select Genre:");
		searchLabel = new JLabel("Keywords :");

		platformSelection = new JComboBox<String>(platformFilters);
		genreSelection = new JComboBox<String>(genreNames);

		platformPanel.add(searchByLabel);
		platformPanel.add(platformSelection);

		genrePanel.add(genreLabel);
		genrePanel.add(genreSelection);

		searchTFPanel.add(searchLabel);
		searchTFPanel.add(searchField);

		compPanel.add(headerLabel);
		compPanel.add(platformPanel);
		compPanel.add(genrePanel);
		compPanel.add(searchTFPanel);
		compPanel.add(searchButtonPanel);

		add(compPanel);
	}

	//==============================================================================================
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == searchButton)
		{
			genreString = "'%'";     //"%" means select all genre at the begining
			platformString = "'%'";
			keywordString = "'%'";

			if ( !genreSelection.getSelectedItem().toString().equals("All") )
			{
				genreString = genreSelection.getSelectedItem().toString().replaceAll("'", "\\\\'");//replace occurrences of ' with \'
				genreString = "'%"+genreString+"%'";
			}
			if ( !platformSelection.getSelectedItem().toString().equals("All") )
			{
				platformString = platformSelection.getSelectedItem().toString().replaceAll("'", "\\\\'");//replace occurrences of ' with \'
				platformString = "'%"+platformString+"%'";
			}

			if (searchField.getText().trim().length() > 0)
			{
				keywordString = searchField.getText().trim().replaceAll("'", "\\\\'");//replace occurrences of ' with \'
				keywordString = "'%"+keywordString+"%'";
			}

			performTheQuery();
		}
	}

	//========================================================================================
	void performTheQuery()
	{
		try
		{
			String gameSearchQuery =
				"select temp.itemID as 'ItemID',temp.title as 'Title',temp.game_genre as 'Genre',temp.platform as 'Platform',temp.version as 'Version', temp.copies as 'In Stock' "+
				"from "+

				"(select i.itemID, i.title, g.game_genre, g.platform,g.version, i.number_copies_available as copies "+
				"from Items i "+
				"inner join Games g on g.itemID = i.itemID and g.game_genre LIKE "+genreString+" and g.platform LIKE "+platformString+" "+
				")temp "+

				"where temp.title LIKE "+ keywordString +" "+          //following conditions just to match keyword
				"OR   temp.game_genre LIKE "+ keywordString +" "+
				"OR   temp.platform LIKE  "+ keywordString +" ";
			ResultSet resultSet = myStatement.executeQuery( gameSearchQuery );
			if(!resultSet.next())
			{
				//show Joptionpane if result set is empty
				JOptionPane.showMessageDialog(null,"No results found, please try again.");
			}
			else
			{
				resultSet.previous();//move Result set cursor to previous row
				new TableDialog(resultSet,myStatement,myUsername);   // some result found..so showing the JTable
			}
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}
}//end of class
