/*
		Name:			Simon Lomax
		ID:				B00044429
		Date:			28/11/11
		Description:	Program lets user press a button to display
						an option dialog box
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class ServerGUI extends JFrame implements ActionListener
{
	//creates button object
	JButton connectButton;
	JTextField ipaddress;
	JTextField port;
	JScrollPane jScrollPane;
	JTextArea logText;

	public ServerGUI()
	{
		//sets title of JFrame
		super("ShutdownTool Server");
		//creates container object
		Container c = getContentPane();

		jScrollPane = new JScrollPane();
		ipaddress = new JTextField();
		port = new JTextField();
		logText = new JTextArea();
		connectButton = new JButton();

		connectButton.setText("Connect");

		logText.setColumns(40);
		logText.setRows(5);
		logText.setLineWrap(true);
		logText.setWrapStyleWord(true);
		jScrollPane.setViewportView(logText);
		jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);




		JPanel logPanel = new JPanel();
		JPanel connectionPanel = new JPanel();

		logPanel.add(jScrollPane);
		connectionPanel.add(ipaddress);
		connectionPanel.add(port);
		connectionPanel.add(connectButton);


		c.add(logPanel);
		c.add(connectionPanel);
		//set the frame to visible
		setVisible(true);
		//set the size of the frame
		setSize(500,200);
		//set the location of the frame
		setLocation(100,40);

		setResizable(false);
		//set the default close operation of the frame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public static void main(String [] args)
	{
		//creates a new instance of lab91
		new ServerGUI();
	}

	public void actionPerformed(ActionEvent e)
	{

	}

}