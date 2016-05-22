import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.sql.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReturnDialog extends JDialog implements ActionListener, DocumentListener
{
	JLabel			rentalIDLabel;
	JLabel			dateLabel;

	JTextField		rentalIDField;
	JTextField		dateField;

	JButton         returnButton;
	JButton         cancelButton;
	String			rentalID;
	String			returnDate;

	Statement 		myStatement;

	//====================================================================================
	ReturnDialog(Statement urStatement)
	{
		myStatement = urStatement;

		rentalIDLabel = new JLabel("Rental ID/Receipt No :");
		rentalIDLabel.setBounds(40, 20, 120, 20);
		rentalIDField = new JTextField(30);
		rentalIDField.getDocument().addDocumentListener(this);
		rentalIDField.setBounds(180, 20, 200, 20);


		dateLabel = new JLabel("Today's Date:");
		dateLabel.setBounds(40, 50, 120, 20);
		dateField = new JTextField(30);
		dateField.setBounds(180, 50, 200, 20);

		Date curDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		String curDateString = dateFormat.format(curDate);
		dateField.setText(curDateString);
		dateField.setEditable(false);

		returnButton = new JButton("Return");
		returnButton.setBounds(130, 140, 90, 40);
		returnButton.addActionListener(this);
		returnButton.setEnabled(false);

		cancelButton = new JButton("Cancel");
		cancelButton.setBounds(240, 140, 90, 40);
		cancelButton.addActionListener(this);

		setLayout(null);
		add(rentalIDLabel);
		add(rentalIDField);
		add(dateLabel);
		add(dateField);
		add(returnButton);
		add(cancelButton);
		setupMainFrame();
	}

    //========================================================================================================
    public void insertUpdate(DocumentEvent de)
    {
        rentalID = rentalIDField.getText().trim();

        if( !rentalID.equals("") )
        {
           returnButton.setEnabled(true);//enable the return button if rentalID field isn't empty
        }
    }
    //========================================================================================================
    public void changedUpdate(DocumentEvent de){}
    //=======================================================================================
    public void removeUpdate(DocumentEvent de)
    {
        rentalID = rentalIDField.getText().trim();

        if( rentalID.equals("") )
        {
           returnButton.setEnabled(false);//disable the return button if rentalID field is empty
        }
    }

    //=====================================================================================
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == returnButton)
		{
			rentalID = rentalIDField.getText().trim();
			rentalID = rentalID.replaceAll("'", "\\\\'");//replace occurrences of ' with \'
			rentalID = "'" + rentalID+ "'";
			returnDate = dateField.getText().trim();
			returnDate = "'" + returnDate+ "'";

			ResultSet rs;
        	try
        	{
				String query = "SELECT * FROM Rent_Description r WHERE r.rentalID = " + rentalID + ";";
				rs = myStatement.executeQuery(query);

				if(!rs.next())
				{
					//if result set is empty then RentalID doesn't exists!
					rs.close();
					throw new MyException("Rental ID/Reciept No. doesn't exists!");//throw an exception
				}

				String query2 = "SELECT r.returned_date FROM Rent_Description r WHERE r.rentalID = " + rentalID + ";";
				rs = myStatement.executeQuery(query2);

				rs.first();
				if(rs.getDate("returned_date") != null)//if the returned_date isn't null means someone returned the item already
				{
					rs.close();
					throw new MyException("Customer has already returned this item!");//throw an exception
				}

				String updateTableSQL =
						   "UPDATE Rent_Description"
						+ " SET returned_date = "+ returnDate
						+ " WHERE rentalID = "+rentalID+";";
				int m = myStatement.executeUpdate(updateTableSQL);//update Rent_Description on the database (just update return date)

				if(m>0)
				{
					this.dispose();//dispose the dialog
					JOptionPane.showMessageDialog(this,"Item has been successfully returned!");
				}

        	}//end try

			catch(MyException myEx)
			{
				JOptionPane.showMessageDialog(this,myEx.getMessage());
			}
			catch ( SQLException sqlException )
			{
				sqlException.printStackTrace();
			}
			catch ( Exception exception )
			{
				exception.printStackTrace();
			}

		}
        else if(e.getSource() ==  cancelButton)
        {
            this.dispose();//dispose the dialog
        }
	}
    //============================================================================
    void setupMainFrame()
    {
        Toolkit tk;
        Dimension d;
        tk = Toolkit.getDefaultToolkit();
        d = tk.getScreenSize();
        setSize(d.width / 3, d.height / 3);
        setLocation(d.width / 4, d.height / 4);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        setTitle("Movies-R-Us");
        setVisible(true);
    }
}//end of class







