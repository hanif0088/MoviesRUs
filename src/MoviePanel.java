import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.sql.*;
import javax.swing.ImageIcon;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

public class MoviePanel extends JPanel implements ActionListener
{
	JTextField searchField,keywordField;
	JCheckBox awardButton,previouslyViewedButton;
	JButton searchButton;
	JComboBox<String> searchBySelection,genreSelection;

	String[] genreNames = {"All", "Action","Comedy","Drama","Sci-Fi","Adventure","Thriller","Fantasy","Crime"};
	String[] selectionFilters = {"Actor", "Director","Title"};

	JLabel headerLabel,
	       searchByLabel,
	       keywordLabel,
	       genreLabel,
	       searchLabel,
	       logoLabel;

	JPanel compPanel,
		   searchTFPanel,
		   searchButtonPanel,
		   keywordPanel,
		   genrePanel,
		   checkBoxPanel;

	String actorName = null;
	String directorName = null;
	String movieTitle = null;
	String keywordString = null;
	String genreString = null;
	boolean viewOnlyAward = false;
	boolean viewOnlyNotCheckedOut = false;

	Statement 	myStatement;
	String 		myUsername;
	//====================================================================================
	MoviePanel(Statement urStatement,String urUsername)
	{
		myStatement = urStatement;
		myUsername = urUsername;

		logoLabel = new JLabel(new ImageIcon("logo.png"));
		headerLabel = new JLabel("Search for movies by keywords, genres, and more!", JLabel.CENTER);
		searchByLabel = new JLabel("Search By:");
		keywordLabel = new JLabel("                   Keywords :");
		genreLabel = new JLabel("Select Genre:");

		compPanel = new JPanel(new GridLayout(6,1));
		searchButtonPanel = new JPanel();
		searchTFPanel = new JPanel();
		keywordPanel = new JPanel();
		genrePanel = new JPanel();
		checkBoxPanel = new JPanel();

		searchField = new JTextField(40);
		keywordField = new JTextField(42);

		awardButton = new JCheckBox("View only award-winning movies");
		previouslyViewedButton = new JCheckBox("View only movies you haven't checked out previously");

		searchButton = new JButton("Search",new ImageIcon("iconSearch.png"));
		searchButton.addActionListener(this);
		searchButtonPanel.add(searchButton);

		searchBySelection = new JComboBox<String>(selectionFilters);
		searchBySelection.addActionListener(this);

		genreSelection = new JComboBox<String>(genreNames);

		searchTFPanel.add(searchByLabel);
		searchTFPanel.add(searchBySelection);
		searchTFPanel.add(searchField);

		keywordPanel.add(keywordLabel);
		keywordPanel.add(keywordField);

		genrePanel.add(genreLabel);
		genrePanel.add(genreSelection);

		checkBoxPanel.add(awardButton);
		checkBoxPanel.add(previouslyViewedButton);

	    compPanel.add(headerLabel);
		compPanel.add(searchTFPanel);
		compPanel.add(keywordPanel);
		compPanel.add(genrePanel);
		compPanel.add(checkBoxPanel);
		compPanel.add(searchButtonPanel);
		add(logoLabel);
		add(compPanel);
	}
	//==============================================================================================
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == searchButton)
		{
			actorName = "'%' ";     //"%" means select all actors
			directorName = "'%' ";
			movieTitle = "'%' ";
			keywordString = "'%' ";
			genreString = "'%' ";
	 		viewOnlyAward = false;
	 		viewOnlyNotCheckedOut = false;

			if ( searchBySelection.getSelectedItem().toString().equals("Actor") )
			{
				if (searchField.getText().trim().length() > 0)
					actorName = "'%" + searchField.getText().trim().replaceAll("'", "\\\\'") + "%' ";//replace occurrences of ' with \'
			}
			else if ( searchBySelection.getSelectedItem().toString().equals("Director") )
			{
				if (searchField.getText().trim().length() > 0)
					directorName = "'%" + searchField.getText().trim().replaceAll("'", "\\\\'") + "%' ";//replace occurrences of ' with \'
			}
			else if ( searchBySelection.getSelectedItem().toString().equals("Title") )
			{
				if (searchField.getText().trim().length() > 0)
					movieTitle = "'%" + searchField.getText().trim().replaceAll("'", "\\\\'") + "%' ";//replace occurrences of ' with \'
			}

			if ( !genreSelection.getSelectedItem().toString().equals("All") )
				genreString = "'%" + genreSelection.getSelectedItem().toString().replaceAll("'", "\\\\'") + "%' ";//replace occurrences of ' with \'

			if (keywordField.getText().trim().length() > 0)
				keywordString = "'%" + keywordField.getText().trim().replaceAll("'", "\\\\'") + "%' ";//replace occurrences of ' with \'

			if (awardButton.isSelected())
				viewOnlyAward = true;
			if (previouslyViewedButton.isSelected())
				viewOnlyNotCheckedOut = true;

			performTheQuery();
		}
	}

	//========================================================================================
	void performTheQuery()
	{
		ResultSet rs;
		// Everything in the following is in the query each time, regardless of awards or excluding previously watched movies

		String query =
		 "select temp.itemID,temp.title as 'Title',group_concat(temp.actor_name) as "
		+ "'Movie Actors',temp.dir_name as 'Director',temp.movie_genre as 'Genre',temp.award_name as 'Award',temp.copies as 'In Stock' "
		+ "from (select i.itemID, i.title, a.actor_name, m.movie_genre, d.dir_name, i.number_copies_available as copies, "
		+ "(case when aw.award_name is not null then aw.award_name else '' end) as award_name "
		+ "from Items i "
		+ "inner join Casted_By c on c.itemID = i.itemID and i.title LIKE " + movieTitle
		+ "inner join Actors a on a.castID = c.castID and a.actor_name LIKE " + actorName
		+ "inner join Movies m on m.itemID = i.itemID and m.movie_genre LIKE " + genreString
		+ "inner join Directors d on m.dirID = d.dirID and d.dir_name LIKE " + directorName
		+ "left outer join Award aw on aw.itemID = i.itemID)temp "
	    + "where temp.title LIKE " + keywordString
		+ "OR  temp.actor_name LIKE " + keywordString
		+ "OR   temp.movie_genre LIKE " + keywordString
		+ "OR   temp.dir_name LIKE " + keywordString
		+ "OR   temp.award_name LIKE " + keywordString
		+ "group by temp.itemID";

		try
		{
			if( viewOnlyAward ==false && viewOnlyNotCheckedOut== false )
			{
				//view all movies regardless only award wining or hasn't checked out!

				rs = myStatement.executeQuery(query);
				if(!rs.next())
				{
					//show Joptionpane if result set is empty
					JOptionPane.showMessageDialog(null,"No results found, please try again.");
				}
				else
				{
					rs.previous();//move ResultSet cursor to previous row
					new TableDialog(rs,myStatement,myUsername);   // some result found..so showing the JTable
				}
			}
			//-------------------------------------------------------------------------------------
			else if( viewOnlyAward ==false && viewOnlyNotCheckedOut== true )
			{
				//not view the award wining but view the hasn't checked out movies!

				query = "select * from (" + query + ")temp2 "
						+ "where temp2.itemID NOT IN "
						+ "(select r.itemID "
						+ "from Rent_Description r "
						+ "where r.userID = '" + myUsername + "')";

				rs = myStatement.executeQuery(query);
				if(!rs.next())
				{
					//show Joptionpane if result set is empty
					JOptionPane.showMessageDialog(null,"No results found, please try again.");
				}
				else
				{
					rs.previous();//move ResultSet cursor to previous row
					new TableDialog(rs,myStatement,myUsername);   // some results found..so showing the JTable
				}
			}
			//------------------------------------------------------------------------------------------
			else if( viewOnlyAward ==true && viewOnlyNotCheckedOut== false )
			{
				//view only award wining but not the hasn't checked out!

				query = "select * from (" + query + ")temp2 "
						+ "where temp2.itemID IN "
						+ "(select a.itemID "
						+ "from Award a)";

				rs = myStatement.executeQuery(query);
				if(!rs.next())
				{
					//show Joptionpane if result set is empty
					JOptionPane.showMessageDialog(null,"No results found, please try again.");
				}
				else
				{
					rs.previous();//move ResultSet cursor to previous row
					new TableDialog(rs,myStatement,myUsername);   // some results found..so showing the JTable
				}
			}
			//----------------------------------------------------------------------------------
			else if( viewOnlyAward ==true && viewOnlyNotCheckedOut== true)
			{
				//view the award wining and view the hasn't checked out movies!

				query = "select * from (" + query + ")temp2 "
						+ "where temp2.itemID IN "
						+ "(select a.itemID "
						+ "from Award a)"
						+ " AND "
						+ "temp2.itemID NOT IN "
						+ "(select r.itemID "
						+ "from Rent_Description r "
						+ "where r.userID = '" + myUsername + "')";

				rs = myStatement.executeQuery(query);
				ResultSet tempResultSet = rs;
				if(!rs.next())
				{
					//show Joptionpane if result set is empty
					JOptionPane.showMessageDialog(null,"No results found, please try again.");
				}
				else
				{
					rs.previous();//move ResultSet cursor to previous row
					new TableDialog(rs,myStatement,myUsername);   // some results found..so showing the JTable
				}
			}
		}//end try
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}//end of class
