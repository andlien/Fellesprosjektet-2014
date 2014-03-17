package gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.StyleContext.SmallAttributeSet;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import models.Appointment;
import models.Person;

import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;

import db.DBConnection;

public class AppointmentPanel extends JDialog {

	private JTextField nameField, emailField;
	private JLabel nameLabel, dateLabel, emailLabel;
	private JCalendar calender;
	private JLabel startTimeLabel, endTimeLabel, roomLabel, alarmLabel;
	private JButton saveButton, addButton, shallButton, shallNotButton, addExternal;
	private JComboBox starTimeHourPropertyComponent, starTimeMinutesPropertyComponent, endTimeHourPropertyComponent,endTimeMinutePropertyComponent,roomPropertyComponent, alarmPropertyComponent;
	private JScrollPane participantsPane;
	private final String[] hourStrings = { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16" , "17", "18", "19", "20", "21", "22", "23"}; 
	private final String[] minuteStrings = { "00","15","30","45"};
	private final String[] alarms = { "På","Av"};
	private JDateChooser dateChooser;
	private Appointment app;
	private HashMap<String, String> oldRows;
	private ArrayList<String> currentRows;
	private Person currentUser, host;
	private DefaultTableModel  tableModel;
	private String[] tableHeaders = { "Deltakere", "Status" };
	private GridBagConstraints nameLabelConstraint, nameFieldConstraint, dateLabelConstraint, dateChooserConstraint, startTimeLabelConstraint,
	endTimeLabelConstraint, roomLabelConstraint, alarmLabelConstraint, starTimePropertyComponentConstraint,
	starTimeMinutesPropertyComponentConstraint, endTimeHourPropertyComponentConstraint, endTimeMinutePropertyComponentConstraint,
	roomPropertyComponentConstraint, alarmPropertyComponentConstraint, participantsPaneConstraint, saveButtonConstraints,
	addButtonConstraints, shallButtonConstraints, shallNotButtonConstraints, emailLabelConstraint, emailFieldConstraint, addExternalConstraint;

	private JTable table;


	public AppointmentPanel(final MainFrame jf, final Person user){
		super(jf, "Avtale", true);

		currentUser = user;
		host = user;
		oldRows = new HashMap<String, String>();

		makeGui(jf);
		setVisible(true);
	}

	public AppointmentPanel(final MainFrame jf, Appointment app, Person user){
		super(jf, "Avtale", true);

		currentUser = user;
		this.app = app;

		getInitialParticipants();
		makeGui(jf);


		if (!currentUser.equals(host)) {

			this.nameField.setEditable(false);
			this.dateChooser.getDateEditor().setEnabled(false);
			//this.dateChooser.getDateEditor().se
			this.starTimeHourPropertyComponent.setEnabled(false);
			this.starTimeMinutesPropertyComponent.setEnabled(false);
			this.endTimeHourPropertyComponent.setEnabled(false);
			this.endTimeMinutePropertyComponent.setEnabled(false);
			this.roomPropertyComponent.setEnabled(false);
			this.addExternal.setEnabled(false);
			this.addButton.setEnabled(false);
			this.calender.setEnabled(false);
			this.emailField.setEnabled(false);
			this.table.setEnabled(false);
		}
		setVisible(true);
	}




	public void updateParticipantRows(HashMap<String, String> participants) {
		String[][] s = new String[participants.size() + 1][2];
		String[] initialTable = { host.getUsername(), "Host" };
		s[0] = initialTable;
		currentRows = new ArrayList<String>();

		int i = 0;
		for (String e : participants.keySet()) {
			s[i+1][0] = e;
			s[i+1][1] = participants.get(e);
			currentRows.add(e);
			i++;
		}

		tableModel = new DefaultTableModel(s, tableHeaders) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table.setModel(tableModel);
	}

	public void makeGui(MainFrame jf) {

		class dateChooserListener implements PropertyChangeListener  {
			public void propertyChangeListener(PropertyChangeEvent ae){
				/*TODO if ("date".equals(ae.getPropertyName())) {
	                System.out.print 
	              ln(ae.getPropertyName()
	                    + ": " + (Date) ae.getNewValue());
	            }
				 */
			}
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				// TODO Auto-generated method stub

			}
		}

		setSize(600, 400);
		setLayout(new GridBagLayout());
		nameField= new JTextField();
		nameLabel= new JLabel("Name");
		dateLabel= new JLabel("Date");
		calender = new JCalendar();
		startTimeLabel= new JLabel("Starttid");
		endTimeLabel= new JLabel("Sluttid");
		roomLabel= new JLabel("Møterom");
		alarmLabel = new JLabel("Alarm");
		saveButton = new JButton("Lagre");
		addButton= new JButton("Legg til/fjern");
		shallButton= new JButton("Skal");
		shallNotButton = new JButton("Skal ikke");
		emailLabel = new JLabel("Epost til ekstern deltager");
		addExternal= new JButton("Legg til");
		emailField = new JTextField("");
		table = new JTable();

		dateChooser = new JDateChooser();
		dateChooser.getDateEditor().addPropertyChangeListener(new dateChooserListener());
		//nameField.setSize(100,10);
		calender.add(dateChooser);

		nameLabelConstraint = new GridBagConstraints();
		nameLabelConstraint.gridx = 0;
		nameLabelConstraint.gridy = 0;
		nameLabelConstraint.fill=GridBagConstraints.HORIZONTAL;
		add(nameLabel, nameLabelConstraint);

		nameFieldConstraint = new GridBagConstraints();
		nameFieldConstraint.gridx = 1;
		nameFieldConstraint.gridy = 0;
		nameFieldConstraint.fill = GridBagConstraints.HORIZONTAL;
		nameFieldConstraint.gridwidth=2;
		add(nameField, nameFieldConstraint);

		dateLabelConstraint = new GridBagConstraints();
		dateLabelConstraint.gridx = 0;
		dateLabelConstraint.gridy = 1;
		dateLabelConstraint.fill= GridBagConstraints.HORIZONTAL;
		add(dateLabel, dateLabelConstraint);


		dateChooserConstraint = new GridBagConstraints();
		dateChooserConstraint.gridx=1;
		dateChooserConstraint.gridy=1;
		dateChooserConstraint.fill =GridBagConstraints.HORIZONTAL;
		dateChooserConstraint.gridwidth=2;
		add(dateChooser,dateChooserConstraint);


		startTimeLabelConstraint = new GridBagConstraints();
		startTimeLabelConstraint.gridx=0;
		startTimeLabelConstraint.gridy=2;
		startTimeLabelConstraint.fill=GridBagConstraints.HORIZONTAL;
		add(startTimeLabel, startTimeLabelConstraint);

		endTimeLabelConstraint = new GridBagConstraints();
		endTimeLabelConstraint.gridx=0;
		endTimeLabelConstraint.gridy=3;
		endTimeLabelConstraint.fill=GridBagConstraints.HORIZONTAL;
		add(endTimeLabel,endTimeLabelConstraint);

		roomLabelConstraint= new GridBagConstraints();
		roomLabelConstraint.gridx=0;
		roomLabelConstraint.gridy=4;
		roomLabelConstraint.fill=GridBagConstraints.HORIZONTAL;
		roomLabelConstraint.anchor=GridBagConstraints.NORTH;
		add(roomLabel,roomLabelConstraint);

		alarmLabelConstraint = new GridBagConstraints();
		alarmLabelConstraint.gridx=0;
		alarmLabelConstraint.gridy=5;
		alarmLabelConstraint.fill=GridBagConstraints.HORIZONTAL;
		alarmLabelConstraint.anchor = GridBagConstraints.NORTH;
		add(alarmLabel,alarmLabelConstraint);


		starTimeHourPropertyComponent= new JComboBox(hourStrings);
		starTimePropertyComponentConstraint= new GridBagConstraints();
		starTimePropertyComponentConstraint.gridx=1;
		starTimePropertyComponentConstraint.weightx=0.5;
		starTimePropertyComponentConstraint.fill=GridBagConstraints.HORIZONTAL;
		starTimePropertyComponentConstraint.gridy=2;
		add(starTimeHourPropertyComponent,starTimePropertyComponentConstraint);


		starTimeMinutesPropertyComponent = new JComboBox(minuteStrings);
		starTimeMinutesPropertyComponentConstraint = new GridBagConstraints();
		starTimeMinutesPropertyComponentConstraint.fill=GridBagConstraints.HORIZONTAL;
		starTimeMinutesPropertyComponentConstraint.gridwidth=1;
		starTimeMinutesPropertyComponentConstraint.weightx=0.5;
		starTimeMinutesPropertyComponentConstraint.gridx=2;
		starTimeMinutesPropertyComponentConstraint.gridy=2;;
		add(starTimeMinutesPropertyComponent,starTimeMinutesPropertyComponentConstraint);

		endTimeHourPropertyComponent= new JComboBox(hourStrings);
		endTimeHourPropertyComponentConstraint = new GridBagConstraints();
		endTimeHourPropertyComponentConstraint.gridx=1;
		endTimeHourPropertyComponentConstraint.fill= GridBagConstraints.HORIZONTAL;
		endTimeHourPropertyComponentConstraint.weightx=1;
		endTimeHourPropertyComponentConstraint.gridy=3;
		endTimeHourPropertyComponentConstraint.gridwidth=1;
		add(endTimeHourPropertyComponent,endTimeHourPropertyComponentConstraint);

		endTimeMinutePropertyComponent = new JComboBox(minuteStrings);
		endTimeMinutePropertyComponentConstraint = new GridBagConstraints();
		endTimeMinutePropertyComponentConstraint.gridx=2;
		endTimeMinutePropertyComponentConstraint.fill=GridBagConstraints.HORIZONTAL;
		endTimeMinutePropertyComponentConstraint.gridy=3;
		endTimeMinutePropertyComponentConstraint.weightx=1;
		add(endTimeMinutePropertyComponent,endTimeMinutePropertyComponentConstraint);

		String[] rooms = { "101","102"};
		roomPropertyComponent = new JComboBox(rooms);
		roomPropertyComponentConstraint = new GridBagConstraints();
		roomPropertyComponentConstraint.gridx=1;
		roomPropertyComponentConstraint.gridy=4;
		roomPropertyComponentConstraint.fill=GridBagConstraints.HORIZONTAL;
		roomPropertyComponentConstraint.gridwidth=2;
		add(roomPropertyComponent, roomPropertyComponentConstraint);


		alarmPropertyComponent = new JComboBox(alarms);
		alarmPropertyComponentConstraint = new GridBagConstraints();
		alarmPropertyComponentConstraint.gridx=1;
		alarmPropertyComponentConstraint.gridy=5;
		alarmPropertyComponentConstraint.fill= GridBagConstraints.HORIZONTAL;
		alarmPropertyComponentConstraint.gridwidth=2;
		alarmPropertyComponentConstraint.anchor = GridBagConstraints.NORTH;
		add(alarmPropertyComponent,alarmPropertyComponentConstraint);


		tableModel = new DefaultTableModel(new String[0][2], tableHeaders) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table.setModel(tableModel);
		table.setFocusable(false);
		table.setSelectionModel(new DefaultListSelectionModel() {
			@Override
			public boolean isSelectedIndex(int index) {
				return index != 0 && super.isSelectedIndex(index);
			}
		});
		participantsPane = new JScrollPane(table);
		participantsPane.setPreferredSize(new Dimension(250, 0));
		participantsPaneConstraint = new GridBagConstraints();
		participantsPaneConstraint.gridx=3;
		participantsPaneConstraint.gridy=0;
		participantsPaneConstraint.fill=GridBagConstraints.VERTICAL;
		participantsPaneConstraint.gridwidth=2;
		participantsPaneConstraint.gridheight=4;


		/*
		participantsPaneConstraint.gridwidth=GridBagConstraints.REMAINDER;
		participantsPaneConstraint.gridheight=GridBagConstraints.REMAINDER;
		participantsPaneConstraint.fill=GridBagConstraints.HORIZONTAL;
		participantsPaneConstraint.anchor=GridBagConstraints.NORTHWEST;
		 */
		add(participantsPane,participantsPaneConstraint);



		saveButton = new JButton("Lagre");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DBConnection con = new DBConnection("src/db/props.properties", true);
				if (app != null)
					deleteParticipantsNotOnAttending(con);
				else
					createAppointment(con);
				saveParticipantsOnAttending(con);
				updateParticipantStatus(con);
				con.close();
				dispose();
			}

			private void createAppointment(DBConnection con2) {
				con2.smallUPDATEorINSERT("INSERT INTO appointment(AppointmentName, StartTime, EndTime, RoomNumber, Location) VALUES('" + 
						nameField.getText() + "', '" + new SimpleDateFormat("yyyy-MM-dd").format(dateChooser.getDate()).toString() + " " +
						(String) starTimeHourPropertyComponent.getSelectedItem() + ":" + (String) starTimeMinutesPropertyComponent.getSelectedItem() + ":00', '" +
						new SimpleDateFormat("yyyy-MM-dd").format(dateChooser.getDate()).toString() + " " + (String) endTimeHourPropertyComponent.getSelectedItem() + ":" +
						(String) endTimeMinutePropertyComponent.getSelectedItem() + ":00', " + (String) roomPropertyComponent.getSelectedItem() +
						", '')");
				ResultSet rs = con2.smallSELECT("SELECT LAST_INSERT_ID() FROM appointment");
				try {
					rs.next();
					makeAppointment(rs.getString(1));
				} catch (SQLException e) {
					try {
						rs.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					throw new RuntimeException("LOL");
				}
				con2.smallUPDATEorINSERT("INSERT INTO employeeappointmentalarm(Username, AppointmentNumber,Status)" +
						"VALUES ('"+ currentUser.getUsername() +"', " + app.getId() + ", 'host')");
			}

			private void deleteParticipantsNotOnAttending(DBConnection con2) {
				PreparedStatement prs;
				try {
					prs = con2.prepareStatement("DELETE FROM employeeappointmentalarm WHERE Username = ? AND AppointmentNumber = ?");
					for (String username : oldRows.keySet()) {
						if (!currentRows.contains(username)) { 	// If a person in oldRows isn't in currentRows
							prs.setString(1, username);	// it has been unattended
							prs.setInt(2, app.getId());
							prs.executeUpdate();
						}
					}
				} catch (SQLException e) {
					con2.close();
					e.printStackTrace();
					throw new RuntimeException();
				} 

			}

			private void saveParticipantsOnAttending(DBConnection con2) {
				ArrayList<String> saveList = new ArrayList<String>(currentRows);
				saveList.removeAll(oldRows.keySet());	// Do not insert the ones already in the database
				PreparedStatement prs;
				try {
					if (app != null) {
						prs = con2.prepareStatement("INSERT INTO employeeappointmentalarm(Username, AppointmentNumber,Status)" +
								"VALUES (?,?,?)");
						for (int i = 1; i < tableModel.getRowCount(); i++) {
							if (saveList.contains(tableModel.getValueAt(i, 0))) {
								prs.setString(1, (String) tableModel.getValueAt(i, 0));
								prs.setInt(2, app.getId());
								prs.setString(3, (String) tableModel.getValueAt(i, 1));
								prs.executeUpdate();
							}
						}
					}
					else {
						throw new RuntimeException("FUUUUUUUUCK!");
					}

				} catch (SQLException e) {
					con2.close();
					e.printStackTrace();
					throw new RuntimeException();
				}
			}

			private void updateParticipantStatus(DBConnection con2) {
				ArrayList<String> updateList = new ArrayList<String>(currentRows);
				updateList.retainAll(oldRows.keySet());
				for (int i = 1; i < tableModel.getRowCount(); i++) {
					String username = (String) tableModel.getValueAt(i, 0);
					if (updateList.contains(username) && !tableModel.getValueAt(i, 1).equals(oldRows.get(username))) {
						con2.smallUPDATEorINSERT("UPDATE employeeappointmentalarm SET Status = '" + tableModel.getValueAt(i, 1) +
								"' WHERE (Username = '" + username + "' AND AppointmentNumber = " + app.getId() + ")");
					}
				}
			}

		});

		saveButtonConstraints = new GridBagConstraints();
		saveButtonConstraints.gridx=3;
		saveButtonConstraints.gridy=9;
		add(saveButton,saveButtonConstraints);

		addButton = new JButton("Legg til/fjern");
		addButtonConstraints = new GridBagConstraints();
		addButtonConstraints.gridx=3;
		addButtonConstraints.gridy=4;
		addButtonConstraints.fill=GridBagConstraints.HORIZONTAL;
		addButtonConstraints.gridwidth=3;
		//addButtonConstraints.anchor=GridBagConstraints.WEST;
		//addButtonConstraints.fill= GridBagConstraints.VERTICAL;
		//addButtonConstraints.gridwidth=1;
		add(addButton,addButtonConstraints);

		addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				HashMap<String, String> temp = new HashMap<String, String>();
				for (int i = 1; i < tableModel.getRowCount(); i++) {
					temp.put((String) tableModel.getValueAt(i, 0), (String) tableModel.getValueAt(i, 1));
				}
				new Participants(AppointmentPanel.this, currentUser , temp);
			}
		});

		addExternal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Email email = new SimpleEmail();
					email.setHostName("smtp.googlemail.com");
					email.setSmtpPort(465);
					email.setAuthenticator(new DefaultAuthenticator("ikkesvar.fellesprosjektet", "Fellesprosjekt26"));
					email.setSSLOnConnect(true);
					email.setFrom("ikkesvar.fellesprosjektet@gmail.com");
					email.setSubject("Du har blitt lagt til i en avtale");
					email.setMsg("Heisann!\n\n"
							+ "Du har blitt lagt til som deltager i en avtale "
							+ "opprettet av bruker " + currentUser.getUsername());
					email.addTo(emailField.getText());
					email.send();
				}
				catch (EmailException ee) {
					ee.printStackTrace();
				}
			}
		});

		shallButton = new JButton("Skal");
		shallButtonConstraints = new GridBagConstraints();
		shallButtonConstraints.gridx = 3;
		shallButtonConstraints.gridy=5;
		shallButtonConstraints.weightx=0.5;
		shallButtonConstraints.fill=GridBagConstraints.HORIZONTAL;

		shallButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentUser.equals(host)) {
					ListSelectionModel sel = table.getSelectionModel();
					for (int i = 1; i < tableModel.getRowCount(); i++) {
						if (sel.isSelectedIndex(i)) {
							tableModel.setValueAt("Confirmed", i, 1);
						}
					}
				}
				else {
					for (int i = 1; i < tableModel.getRowCount(); i++) {
						if (tableModel.getValueAt(i, 0).equals(currentUser)) {
							tableModel.setValueAt("Confirmed", i, 1);
						}
					}
				}
			}
		});

		add(shallButton,shallButtonConstraints);

		shallNotButton = new JButton("Skal ikke");
		shallNotButtonConstraints = new GridBagConstraints();
		shallNotButtonConstraints.gridx=4;
		shallNotButtonConstraints.gridy= 5;
		shallNotButtonConstraints.weightx=0.5;
		shallNotButtonConstraints.fill=GridBagConstraints.HORIZONTAL;
		shallNotButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentUser.equals(host)) {
					ListSelectionModel sel = table.getSelectionModel();
					for (int i = 1; i < tableModel.getRowCount(); i++) {
						if (sel.isSelectedIndex(i)) {
							tableModel.setValueAt("Declined", i, 1);
						}
					}
				}
				else {
					for (int i = 1; i < tableModel.getRowCount(); i++) {
						if (tableModel.getValueAt(i, 0).equals(currentUser)) {
							tableModel.setValueAt("Declined", i, 1);
						}
					}
				}
			}
		});
		add(shallNotButton,shallNotButtonConstraints);

		emailLabelConstraint = new GridBagConstraints();
		emailLabelConstraint.gridx=0;
		emailLabelConstraint.gridy=8;
		emailLabelConstraint.fill=GridBagConstraints.HORIZONTAL;
		emailLabelConstraint.gridwidth=2;
		add(emailLabel,emailLabelConstraint);

		emailFieldConstraint = new GridBagConstraints();
		emailFieldConstraint.gridx=2;
		emailFieldConstraint.gridy=8;
		emailFieldConstraint.fill=GridBagConstraints.HORIZONTAL;
		emailFieldConstraint.gridwidth=2;
		add(emailField,emailFieldConstraint);

		addExternalConstraint = new GridBagConstraints();
		addExternalConstraint.gridx=4;
		addExternalConstraint.gridy=8;
		addExternalConstraint.fill=GridBagConstraints.HORIZONTAL;
		addExternalConstraint.gridwidth=3;
		//addExternalConstraint.fill=GridBagConstraints.HORIZONTAL;
		//addExternalConstraint.anchor=GridBagConstraints.SOUTH;
		add(addExternal,addExternalConstraint);

		updateParticipantRows(oldRows);

		setLocationRelativeTo(jf);
	}

	public void getInitialParticipants() {
		oldRows = new HashMap<String, String>();

		// Push employees who's allready attending into oldRows
		DBConnection con = new DBConnection("src/db/props.properties", true);
		try {

			ResultSet rsAtLoad = con.smallSELECT("SELECT Username, Status FROM employeeappointmentalarm WHERE AppointmentNumber = " + app.getId());
			while (rsAtLoad.next()) {
				if (rsAtLoad.getString("Status").equals("host"))
					this.host = new Person(rsAtLoad.getString("Username"));
				else
					oldRows.put(rsAtLoad.getString("Username"), rsAtLoad.getString("Status"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		con.close();
	}
	public void makeAppointment(String id) {
		app = new Appointment(Integer.parseInt(id));
	}

}
