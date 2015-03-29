package com.lomo.shutdowntool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.regex.*;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private Socket socket;
	EditText ipText;
	EditText poText;
	View connectButton;
	View disconnectButton;
	View shutdownButton;
	View restartButton;
	View actionLabel;
    Handler handler;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();
        ipText = (EditText) findViewById(R.id.ip_message);
    	poText = (EditText) findViewById(R.id.port_message);
    	
    	//Get last server
        loadServerDetails();
    	
        connectButton = findViewById(R.id.connect_button);
		disconnectButton = findViewById(R.id.disconnect_button);
		shutdownButton = findViewById(R.id.shutdown_button);
		restartButton = findViewById(R.id.restart_button);
		actionLabel = findViewById(R.id.action_text);
    	
        //ipText.setText("lololol", TextView.BufferType.EDITABLE);
       
    }
    
    public void loadServerDetails()
    {
    	String ip;
    	String port;
    	try
    	{
    		InputStream inputStream = openFileInput("details.txt");
    		if(inputStream != null)
    		{
    			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
    			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
    			ip = bufferedReader.readLine();
    			port = bufferedReader.readLine();
    			
    			ipText.setText(ip);
    			poText.setText(port);
    		}
    	}catch(FileNotFoundException e)
    	{
    		toastPost("No server file found");
    	}
    	catch(IOException e)
    	{
    		toastPost("Cannot read file");
    	}
    }
    
    public void serverDisconnect(View view) throws IOException
    {
    	socket.close();
    	String ip = ipText.getText().toString();
		connectButton.setVisibility(View.VISIBLE);
		disconnectButton.setVisibility(View.GONE);
		actionLabel.setVisibility(View.GONE);
		shutdownButton.setVisibility(View.GONE);
		restartButton.setVisibility(View.GONE);
		ipText.setEnabled(true);
		poText.setEnabled(true);
		toastPost("Disconnected from server: "+ip);
    }
    
    public void serverConnect(View view)
    {
    	String ip = ipText.getText().toString();
    	String po = poText.getText().toString();
    	
    	if(!validateIP(ip))
    	{
    		toastPost(ip + " is an invalid IP Address.");
    	}
    	else if(!validatePort(po))
    	{
    		toastPost("Invalid port number.");
    	}
    	else
    	{
            progress = new ProgressDialog(this);
            connectButton.setEnabled(false);
            progress.setTitle("Connecting");
            progress.setMessage("Attempting to connect to server...");

    		int port = Integer.parseInt(po);
    		try
    		{
                progress.show();
    			new Thread(new ClientThread(ip,port)).start();
    		}
    		catch(Exception e)
    		{
    		    System.err.println("Failed to connect to server");
                toastPost("Unable to connect to Server");
    		}

            saveServer(ip, po);

    	}
    }
    
    public void saveServer(String ip, String port)
    {
    	try
    	{
    		String separator = System.getProperty("line.separator");
    		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("details.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(ip);
            outputStreamWriter.append(separator);
            outputStreamWriter.append(port);
            outputStreamWriter.close();
    	}
    	catch(IOException e)
    	{
    		toastPost("File write failed.");
    	}
    }
    
    public void sendShutdown(View view) throws IOException
    {
        try {
            sendMessage("shutdown");
            toastPost("Sent");
        }
        catch (IOException ie)
        {
            toastPost("ERROR: Lost connection to server.");
            socket.close();
            connectButton.setVisibility(View.VISIBLE);
            disconnectButton.setVisibility(View.GONE);
            actionLabel.setVisibility(View.GONE);
            shutdownButton.setVisibility(View.GONE);
            restartButton.setVisibility(View.GONE);
            ipText.setEnabled(true);
            poText.setEnabled(true);
        }
    }
    
    public boolean validateIP(String ip)
    {
    	Pattern pat;
    	Matcher match;
    	
    	final String IPADDRESS_PATTERN = 
    			"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
  				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
    			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
    			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    	
    	pat = Pattern.compile(IPADDRESS_PATTERN);
    	match = pat.matcher(ip);
    	return match.matches();
    	
    }
    
    public boolean validatePort(String po)
    {
    	try
    	{
    		Integer.parseInt(po);
    	}catch(NumberFormatException e)
    	{
    		return false;
    	}

        int port = Integer.parseInt(po);
        if(port > 65535 || port < 0)
        {
            return false;
        }
        else {
            return true;
        }
    		
    }
    
    public void sendRestart(View view) throws IOException
    {
        try {
            sendMessage("restart");
            toastPost("Restart command sent.");
        }
        catch (IOException ie)
        {
            toastPost("ERROR: Lost connection to server.");
            socket.close();
            connectButton.setVisibility(View.VISIBLE);
            disconnectButton.setVisibility(View.GONE);
            actionLabel.setVisibility(View.GONE);
            shutdownButton.setVisibility(View.GONE);
            restartButton.setVisibility(View.GONE);
            ipText.setEnabled(true);
            poText.setEnabled(true);
        }
    }
    
    public void toastPost(String message)
    {
    	Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context,  text,  duration);
        toast.show();
    }
    
    public void sendMessage(String message) throws IOException
    {
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        out.println(message);
        if (out.checkError()) {
            throw new IOException();
        }
    }
    class ClientThread implements Runnable
    {
    	private int port_number;
    	private String ip_address;
    	BufferedReader in;
        PrintWriter out;

    	public ClientThread(String ip, int port)
    	{
    		this.port_number = port;
    		this.ip_address = ip;
    	}

    	public void run()
    	{
    		try
        	{
        		InetAddress server = InetAddress.getByName(this.ip_address);
        		socket = new Socket();
                socket.connect(new InetSocketAddress(server, this.port_number),10000);
        		if(socket.isConnected())
        		{
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                    String servermessage;
                    if((servermessage = in.readLine()) != null)
                    {
                        if(servermessage.equals("signal"))
                        {
                            out.println("ack");

                        }
                        else
                        {
                            socket.close();
                            throw new IOException();
                        }
                    }
                    handler.post(new Runnable()
                    {
                        String ip = ipText.getText().toString();

                        @Override
                        public void run()
                        {
                            progress.dismiss();
                            connectButton.setEnabled(true);
                            ipText.setEnabled(false);
                            poText.setEnabled(false);
                            connectButton.setVisibility(View.GONE);
                            disconnectButton.setVisibility(View.VISIBLE);
                            actionLabel.setVisibility(View.VISIBLE);
                            shutdownButton.setVisibility(View.VISIBLE);
                            restartButton.setVisibility(View.VISIBLE);
                            toastPost("Connected to server: " + ip);
                    }
                    });
                }
                else
                {
                    System.out.println("No response from server");
                    socket.close();
                }

        	}
        	catch (IOException io)
        	{
        		io.printStackTrace();
        		System.out.println("IO Exception from Thread");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        toastPost("Unable to connect to server.");
                        progress.dismiss();
                        connectButton.setEnabled(true);
                }
                });
        	}
    	}
    }
}
