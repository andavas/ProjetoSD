package projeto;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;

import javax.swing.*;

class Cliente extends JFrame{
	
    private JButton btnSolicitar;
    private JPanel janPrincipal;
    private JLabel lblNomeArquivo;
    private JLabel lblTitulo;
    private JTextField txtNomeArquivo;
    private BackCliente cliente;
    
    public Cliente () {
    	super("Cliente");
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    setMinimumSize(new Dimension(320, 200));
	    setLocationRelativeTo(null);
	    run();
	    setVisible(true);
    }
    
    public void run() {
    	
    	
    	/// INTERFACE - INÍCIO
    	
    	janPrincipal = new JPanel();
        lblNomeArquivo = new JLabel();
        txtNomeArquivo = new JTextField();
        btnSolicitar = new JButton();
        lblTitulo = new JLabel();
    	
        lblNomeArquivo.setText("Insira o nome do arquivo:");
        
        btnSolicitar.setText("Solicitar");
        
        lblTitulo.setBackground(new Color(204, 0, 204));
        lblTitulo.setFont(new Font("Tahoma", 0, 18));
        lblTitulo.setText("Espaço do Cliente");
    	
        GroupLayout janPrincipalLayout = new GroupLayout(janPrincipal);
        janPrincipal.setLayout(janPrincipalLayout);
        janPrincipalLayout.setHorizontalGroup(
            janPrincipalLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(janPrincipalLayout.createSequentialGroup()
                .addGroup(janPrincipalLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(janPrincipalLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblNomeArquivo))
                    .addGroup(janPrincipalLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(txtNomeArquivo, GroupLayout.PREFERRED_SIZE, 369, GroupLayout.PREFERRED_SIZE))
                    .addGroup(janPrincipalLayout.createSequentialGroup()
                        .addGap(164, 164, 164)
                        .addComponent(btnSolicitar)))
                .addContainerGap(65, Short.MAX_VALUE))
            .addGroup(GroupLayout.Alignment.TRAILING, janPrincipalLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lblTitulo)
                .addGap(149, 149, 149))
        );
        janPrincipalLayout.setVerticalGroup(
            janPrincipalLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(janPrincipalLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(lblTitulo)
                .addGap(62, 62, 62)
                .addComponent(lblNomeArquivo)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNomeArquivo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(btnSolicitar)
                .addContainerGap(83, Short.MAX_VALUE))
        );
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(janPrincipal, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(janPrincipal, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pack();
        /// INTERFACE - FIM
        
        final SwingWorker organizarServidores = new SwingWorker() 
        {

            @Override
            protected Object doInBackground() throws Exception {

                return 0;
            }

        };
        
		
        
        
    	
        cliente = new BackCliente();  
        
    	btnSolicitar.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent event)
            {
            	try {
                	cliente.setNomeArquivo(txtNomeArquivo.getText());
                	cliente.checkFileServers();
                	
                	if (cliente.getFileServersCount() == 0) {
                		JOptionPane.showMessageDialog(janPrincipal, "Não há servidores disponíveis ");
                	}
                	else {
                		
                		String [][] listaServidores = cliente.getFileServersList();
                	
                		if (cliente.getFileServersCount() == 1) {
                			cliente.receberArquivo(listaServidores[0]);
    	            	}
    	            	else {
    	            		
    	            		int numServidor = 0;
    	            		// transforar listaServidores (matriz de strings) em uma lista de strings
    	            		Object[] itens = { "Servidor X - IP: 821 - Porta:122", "Servidor Y - IP: 123 - Porta:187", "Servidor Z - IP: 568 - Porta:916" };
    	            		Object selectedValue = JOptionPane.showInputDialog(
    	            				null,
    	            				"Por favor, selecione um servidor:",
    	            				"Servidores disponíveis",
    	            				JOptionPane.INFORMATION_MESSAGE,
    	            				null,
    	            				itens,
    	            				itens [0]); //
    	            		
    	            		// chamar janela para selecionar um servidor disponível
    	            		cliente.receberArquivo(listaServidores[numServidor]);
    	            	}
                	}
            	} catch (Exception ex) {
            		ex.printStackTrace();
            	}

            }
  
        });
    }
	
}




class BackCliente {
	
	private String nomeArquivo = "linuxmint-20-cinnamon-64bit (1).iso";//"Date_A_Live_3_01_Boteco-Anbient.mkv"; // nome do arquivo com extensão
	private String ipServidor = "127.0.0.1";
	private int portaServidor = 8888; 
	private int portaTCPServidorArquivo = 9999;
	private int fileServersCount;
	private String [][] fileServersList;
	
	private Socket clientSocket;
	
	
	public BackCliente(){
	}
	
	public BackCliente(String nomeArquivo){
		this.nomeArquivo = nomeArquivo;
	}
	
	public int getFileServersCount() {
		return fileServersCount;
	}

	public void setFileServersCount(int fileServersCount) {
		this.fileServersCount = fileServersCount;
	}

	public Socket getSocket() {
		return clientSocket;
	}

	public void setSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public BackCliente(String nomeArquivo, String ipServidor, int portaServidor, int portaTCPServidorArquivo) {
		this.nomeArquivo = nomeArquivo;
		this.ipServidor = ipServidor;
		this.portaServidor = portaServidor;
		this.portaTCPServidorArquivo = portaTCPServidorArquivo;
	}
	

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public String getIpServidor() {
		return ipServidor;
	}

	public void setIpServidor(String ipServidor) {
		this.ipServidor = ipServidor;
	}

	public int getPortaServidor() {
		return portaServidor;
	}

	public void setPortaServidor(int portaServidor) {
		this.portaServidor = portaServidor;
	}

	public int getPortaTCPServidorArquivo() {
		return portaTCPServidorArquivo;
	}

	public void setPortaTCPServidorArquivo(int portaTCPServidorArquivo) {
		this.portaTCPServidorArquivo = portaTCPServidorArquivo;
	}

	public String[][] getFileServersList() throws IOException {
		
    	BufferedReader response = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    	fileServersList = new String[fileServersCount][2];
		String temp;
		for (int i = 0; i < fileServersCount; i++) {
			temp = response.readLine();
			fileServersList[i][0] = temp.substring(0,temp.indexOf("&"));
			fileServersList[i][1] = temp.substring(temp.indexOf("&")+1);
		}
		return fileServersList;
	}

	// parte 1 do cliente
	// verificar quantidade dos servidores disponíveis
	public void checkFileServers() {
		// seleção do arquivo
		//String caminhoDestino = "/home/andavas/prog/2020.2/SD"; // destino do arquivo
		try {
			
			// conectar ao servidor central
			clientSocket = new Socket(ipServidor, portaServidor);
			DataOutputStream request = new DataOutputStream(clientSocket.getOutputStream());
			System.out.println("Solicitando arquivo...");
			request.writeBytes(nomeArquivo + '\n');
			
			// recebendo os servidores de arquivos disponíveis do servidor principal
			System.out.println("Aguardando servidor principal...");
			
			DataInputStream sdis = new DataInputStream(clientSocket.getInputStream());
			fileServersCount = sdis.readInt();
			
		} catch(Exception e) {
				e.printStackTrace();
		}
			
	}

	public void run() {
		
		// seleção do arquivo
		//String caminhoDestino = "/home/andavas/prog/2020.2/SD"; // destino do arquivo
		try {
			
			// conectar ao servidor central
			
			
			
			if (fileServersCount > 0)
			{

				System.out.println("Lista de servidores pronta!");
				if (fileServersCount == 1) {
					receberArquivo(fileServersList[0]);
				} else { //usuário clica no servidor de arquivos que ele quer
					System.out.println("Else");
					receberArquivo(fileServersList[0]); 
				}
			}
			clientSocket.close();
		}
		catch(OutOfMemoryError e) {
			System.err.println("Eita, o arquivo é muito grande!");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	
	}

	public void receberArquivo(String [] ServidorArquivo) {
		// estabelece conexão com servidores de arquivo 
		try {
			
			System.out.println("Solicitando arquivo ao servidor...");
			System.out.println("Nome:  "+ServidorArquivo[0]);
			System.out.println("IP:    "+ServidorArquivo[1]);
			System.out.println("Porta: "+portaTCPServidorArquivo);
			Socket fileSocket = new Socket(ServidorArquivo[1], portaTCPServidorArquivo);
			DataOutputStream fileRequest = new DataOutputStream(fileSocket.getOutputStream());
			fileRequest.writeBytes(nomeArquivo + '\n');
			// aguardando mensagem de retorno do servidor de arquivos
			InputStream chunk = fileSocket.getInputStream();
			final int bufferLength = 1024;
			byte[] arqBytes = new byte[bufferLength];
			FileOutputStream fos = new FileOutputStream(nomeArquivo);
			System.out.println("Recebendo...");
			int totalBytesRead = 0;
			while (true) {  // lendo o arquivo
				int bytesRead = chunk.read(arqBytes);
				
				if (bytesRead != -1) { // revebe o perdaço do arquivo
					fos.write(arqBytes, 0, bytesRead);
					totalBytesRead += bytesRead;
					//System.out.println("Recebendo: "+totalBytesRead/1024+ "KB");
				} else { // fim do arquivo
					System.out.println("Recebido!");
					break;
				}
			}
			fos.close();
			fileSocket.close();
		} catch (IOException e) {
			System.err.println("Não foi possível criar o arquivo");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
