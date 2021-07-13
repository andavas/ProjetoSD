package projeto;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

import javax.swing.*;

class Cliente extends JFrame {

	/**
	 * Frontend do Cliente
	 */
	private static final long serialVersionUID = 1L;
	private JButton btnSolicitar;
	private JPanel janPrincipal;
	private JLabel lblNomeArquivo;
	private JLabel lblTitulo;
	private JLabel lblAguarde;
	private JTextField txtNomeArquivo;
	private BackCliente cliente;

	public Cliente() {
		super("Cliente");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(320, 200));
		setLocationRelativeTo(null);
		run();
		setVisible(true);
	}

	public void run() {

		/// INTERFACE - INÍCIO - Gerado pelo NETBEANS

		janPrincipal = new JPanel();
		lblNomeArquivo = new JLabel();
		txtNomeArquivo = new JTextField();
		btnSolicitar = new JButton();
		lblTitulo = new JLabel();
		lblAguarde = new JLabel();
		
		lblAguarde.setText("");
		lblNomeArquivo.setText("Insira o nome do arquivo:");
		lblNomeArquivo.setText("Insira o nome do arquivo:");
		btnSolicitar.setText("Solicitar");

		lblTitulo.setBackground(new Color(204, 0, 204));
		lblTitulo.setFont(new Font("Tahoma", 0, 18));
		lblTitulo.setText("Espaço do Cliente");

		GroupLayout janPrincipalLayout = new GroupLayout(janPrincipal);
		janPrincipal.setLayout(janPrincipalLayout);
		janPrincipalLayout.setHorizontalGroup(janPrincipalLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(janPrincipalLayout.createSequentialGroup()
						.addGroup(janPrincipalLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addGroup(janPrincipalLayout.createSequentialGroup().addContainerGap()
										.addComponent(lblNomeArquivo))
								.addGroup(janPrincipalLayout.createSequentialGroup().addContainerGap().addComponent(
										txtNomeArquivo, GroupLayout.PREFERRED_SIZE, 369, GroupLayout.PREFERRED_SIZE))
								.addGroup(janPrincipalLayout.createSequentialGroup().addGap(164, 164, 164)
										.addComponent(btnSolicitar)))
						.addContainerGap(65, Short.MAX_VALUE))
				.addGroup(janPrincipalLayout.createSequentialGroup().addGap(100, 100, 100)
						.addComponent(lblAguarde))
				.addGroup(GroupLayout.Alignment.TRAILING, janPrincipalLayout.createSequentialGroup()
						.addGap(0, 0, Short.MAX_VALUE).addComponent(lblTitulo).addGap(149, 149, 149)));
		janPrincipalLayout.setVerticalGroup(janPrincipalLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(janPrincipalLayout.createSequentialGroup().addGap(26, 26, 26).addComponent(lblTitulo)
						.addGap(62, 62, 62).addComponent(lblNomeArquivo)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(txtNomeArquivo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addGap(41, 41, 41).addComponent(btnSolicitar)
						.addGap(10, 10, 10).addComponent(lblAguarde)
						.addContainerGap(83, Short.MAX_VALUE)));
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(janPrincipal,
				GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(janPrincipal,
				GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		pack();
		/// INTERFACE - FIM

		
		cliente = new BackCliente();
		
		//lblAguarde.setText("Aguardando resposta do servidor...");
		
		btnSolicitar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					cliente.setNomeArquivo(txtNomeArquivo.getText());  // recebe o nome do arquivo da caixa de texto
					cliente.checkFileServers(); // verifica servidores disponíveis
					
					int qtdServidores = cliente.getFileServersCount();

					if (qtdServidores == 0) {
						JOptionPane.showMessageDialog(janPrincipal,
								"Nenhum servidor de arquivo tem o arquivo " + cliente.getNomeArquivo(), "Erro!",
								JOptionPane.ERROR_MESSAGE);
						cliente.getSocket().close();

					} else { // qtdServidores > 0
						String[][] listaServidores = cliente.getFileServersList();

						if (qtdServidores == 1) {
							cliente.getSocket().close(); // fecha a conexão com o servidor principal
							receberArquivo(listaServidores[0]); // função para receber o arquivo do servidor de arquivos

						} else { // qtdServidores >= 2 - Constrói lista de servidores

							Object[] itens = new String[qtdServidores];
							String temp;
							for (int i = 0; i < qtdServidores; i++) {
								temp = "Nome: ";
								temp += listaServidores[i][0];
								temp += " - ";
								temp += "IP: ";
								temp += listaServidores[i][1];
								temp += " - ";
								temp += "Porta: ";
								temp += listaServidores[i][2];
								itens[i] = temp;
								temp = "";
							}

							// chamar janela para selecionar um servidor disponível

							// itens = { "Servidor X - IP: 821 - Porta:122", "Servidor Y - IP: 123 -
							// Porta:187", "Servidor Z - IP: 568 - Porta:916" };
							Object selectedValue = JOptionPane.showInputDialog(null,
									"Por favor, selecione um servidor:", "Servidores disponíveis",
									JOptionPane.QUESTION_MESSAGE, null, itens, itens[0]);

							int numServidor = 0;
							if (selectedValue != null) {
								for (int i = 0; i < itens.length; i++) {
									if (itens[i].equals(selectedValue))
										numServidor = i;
								}
								cliente.getSocket().close(); // fecha a conexão com o servidor principal
								receberArquivo(listaServidores[numServidor]); // função para receber o arquivo do servidor de arquivos
								
							}

						}
						listaServidores = null;
						cliente.getSocket().close(); // fecha a conexão com o servidor de arquivos
					}
					lblAguarde.setText("");

				} catch (ConnectException ex) {
					JOptionPane.showMessageDialog(janPrincipal,
							"O servidor principal não está disponível \n" + "Por favor, inicie o servidor principal",
							"Erro!", JOptionPane.ERROR_MESSAGE);
				} catch (OutOfMemoryError ex) {
					System.err.println("Eita, o arquivo é muito grande!");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	public void receberArquivo(String[] ServidorArquivo) {

		// INÍCIO INTERFACE RECEBIMENTO
		JFrame frame = new JFrame("MessageDialog");
		JOptionPane pane = new JOptionPane();
		JDialog dialog = pane.createDialog(frame, "Recebendo");
		// JProgressBar jProgressBar = new JProgressBar(1, 100);

		final SwingWorker receberArquivo = new SwingWorker() {

			@Override
			protected Object doInBackground() throws Exception {
				String ip = ServidorArquivo[1];
				int porta = Integer.parseInt(ServidorArquivo[2]);
				long tamanhoArquivo;

				cliente.setSocket(new Socket(ip, porta)); // abre uma nova conexão com o servidor de arquivos

				DataOutputStream fileRequest = new DataOutputStream(cliente.getSocket().getOutputStream());
				fileRequest.writeBytes(cliente.getNomeArquivo() + '\n');

				DataInputStream response = new DataInputStream(cliente.getSocket().getInputStream());
				tamanhoArquivo = response.readLong();
				System.out.println(tamanhoArquivo);

				InputStream chunk = cliente.getSocket().getInputStream();
				final int bufferLength = 1024;
				byte[] arqBytes = new byte[bufferLength];
				FileOutputStream fos = new FileOutputStream(cliente.getNomeArquivo());
				System.out.println("Recebendo...");
				int totalBytesRead = 0;
				while (true) { // lendo o arquivo
					int bytesRead = chunk.read(arqBytes);

					if (bytesRead != -1) { // revebe o perdaço do arquivo
						fos.write(arqBytes, 0, bytesRead);
						totalBytesRead += bytesRead;

						pane.setMessage(
								"Recebendo " + totalBytesRead / 1024 + "KB de " + tamanhoArquivo / 1024 + " KB");
						// jProgressBar.setValue((int) ((totalBytesRead / tamanhoArquivo) * 100));
					} else { // fim do arquivo
						fos.close();
						cliente.getSocket().close();
						pane.setMessage("Recebido!");
						break;
					}
				}
				return 0;
			}
		};
		pane.setMessage("Recebendo");
		// jProgressBar.setValue(0);
		// receberArquivo.execute();
		// pane.add(jProgressBar, 1);
		receberArquivo.execute();
		dialog.setVisible(true);
		dialog.dispose();

	}
}

class BackCliente {

	private String nomeArquivo; // 06 - The Sun Is Dead.mp3 // nome do arquivo com extensão
	private String ipServidor = "127.0.0.1";
	private int portaServidor = 8888;
	private int portaTCPServidorArquivo;
	private int fileServersCount;
	private String[][] fileServersList;

	private Socket clientSocket;

	public BackCliente() {
	}

	public BackCliente(String nomeArquivo) {
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

		BufferedReader response = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
		this.fileServersList = new String[this.fileServersCount][3];
		String temp;
		for (int i = 0; i < fileServersCount; i++) {
			temp = response.readLine();
			this.fileServersList[i][0] = temp.substring(0, temp.indexOf("-"));
			this.fileServersList[i][1] = temp.substring(temp.indexOf("-") + 1, temp.indexOf(":"));
			this.fileServersList[i][2] = temp.substring(temp.indexOf(":") + 1);
		}
		return this.fileServersList;
	}

	// parte 1 do cliente
	// verificar quantidade dos servidores disponíveis
	public void checkFileServers() throws IOException, SocketException {
		// seleção do arquivo
		// String caminhoDestino = "/home/andavas/prog/2020.2/SD"; // destino do arquivo

		// conectar ao servidor central
		clientSocket = new Socket(ipServidor, portaServidor);
		DataOutputStream request = new DataOutputStream(clientSocket.getOutputStream());
		System.out.println("Solicitando arquivo...");
		request.writeBytes(nomeArquivo + '\n');

		// recebendo os servidores de arquivos disponíveis do servidor principal
		System.out.println("Aguardando servidor principal...");

		DataInputStream sdis = new DataInputStream(clientSocket.getInputStream());
		fileServersCount = sdis.readInt();
	}

	public void receberArquivo(String[] ServidorArquivo) {
		// estabelece conexão com servidores de arquivo
		try {

			System.out.println("Solicitando arquivo ao servidor...");
			System.out.println("Nome:  " + ServidorArquivo[0]);
			System.out.println("IP:    " + ServidorArquivo[1]);
			System.out.println("Porta: " + portaTCPServidorArquivo);
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
			while (true) { // lendo o arquivo
				int bytesRead = chunk.read(arqBytes);

				if (bytesRead != -1) { // revebe o perdaço do arquivo
					fos.write(arqBytes, 0, bytesRead);
					totalBytesRead += bytesRead;
					// System.out.println("Recebendo: "+totalBytesRead/1024+ "KB");
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
