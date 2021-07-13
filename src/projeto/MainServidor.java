package projeto;
public class MainServidor {

	public static void main(String[] args){
		new Servidor();
		
	}

}
/*package projeto;

import java.awt.*;
import java.io.*;
import java.net.*;

import javax.swing.*;

class Servidor extends JFrame {

    private JButton btnLigaDesliga;
    private JPanel janPrincipal;
    private JLabel lblPorta;
    private JLabel lblTextoServidorAberto;
    private JLabel lblTitulo;
    private JTextField txtPortaServidor;
    private BackServidor servidor;

	public Servidor() {
		super("Servidor");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(320, 200));
		setLocationRelativeTo(null);
		run();
		setVisible(true);
	}
	public void run() {
		janPrincipal = new JPanel();
        lblTitulo = new JLabel();
        lblPorta = new JLabel();
        btnLigaDesliga = new JButton();
        txtPortaServidor = new JTextField();
        lblTextoServidorAberto = new JLabel();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        lblTitulo.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        lblTitulo.setText("Espaço do Servidor");

        lblPorta.setFont(new Font("Tahoma", 0, 14)); // NOI18N
        lblPorta.setText("Insira a porta (Padrão: 8888)");

        btnLigaDesliga.setFont(new Font("Tahoma", 0, 14)); // NOI18N
        btnLigaDesliga.setText("Fechar");

        lblTextoServidorAberto.setFont(new Font("Tahoma", 0, 14)); // NOI18N
        lblTextoServidorAberto.setForeground(new Color(51, 204, 0));
        lblTextoServidorAberto.setText("Servidor Aberto!");

        GroupLayout jPanel1Layout = new GroupLayout(janPrincipal);
        janPrincipal.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(lblPorta)
                    .addComponent(txtPortaServidor, GroupLayout.PREFERRED_SIZE, 327, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(53, Short.MAX_VALUE))
            .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(lblTitulo)
                        .addGap(122, 122, 122))
                    .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(btnLigaDesliga)
                        .addGap(162, 162, 162))))
            .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblTextoServidorAberto)
                .addGap(139, 139, 139))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(lblTitulo)
                .addGap(36, 36, 36)
                .addComponent(lblPorta)
                .addGap(18, 18, 18)
                .addComponent(txtPortaServidor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(lblTextoServidorAberto)
                .addGap(18, 18, 18)
                .addComponent(btnLigaDesliga)
                .addContainerGap(67, Short.MAX_VALUE))
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
	}
}
class Connection implements Runnable {

	private Socket connectionSocket;
	private String nomeArquivo;

	public Connection(Socket newSocket) {
		connectionSocket = newSocket;

	}

	@Override
	public void run() {
		try {
			System.out.println("Nova conexão recebida");
			System.out.println("Info do cliente:");
			System.out.println("IP: " + connectionSocket.getInetAddress());
			System.out.println("Porta: " + connectionSocket.getPort());

			BufferedReader request = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			nomeArquivo = request.readLine();

			System.out.println("Solicitando arquivo \"" + nomeArquivo + "\"");
			System.out.println("Verificando nos servidores de arquivo");

			// solicitação UDP para todos os servidores de arquivo
			int timeout = 10 * 1000;
			DatagramSocket requestSocket = new DatagramSocket();
			requestSocket.setSoTimeout(timeout);
			InetAddress IPAddress = InetAddress.getByName("239.0.0.1");
			
			// sendData contém a string com o nome do arquivo em binário
			byte[] sendData = nomeArquivo.getBytes();
			// receiveData contém o nome e o ip do servidor de arquivos, separados por '&'
			byte[] receiveData = new byte[1024];
			
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

			// envia broadcast para todos os servidores de arquivo da rede
			requestSocket.send(sendPacket);

			String[] infoFileServers = new String[1000]; // guarda a lista crua de servidores de arquivos
			int fileServersCount  = 0;
			try {
				while (true) {
					// recebe solicitações de todos os servidores de arquivo da rede que possuem o arquivo do cliente
					requestSocket.receive(receivePacket); 
					requestSocket.setSoTimeout(timeout); // reinicia o tempo
					String receive = new String(receivePacket.getData(), 0, receivePacket.getLength());
					infoFileServers[fileServersCount] = receive; // salva o nome do servidor de arquivos num array
					fileServersCount++;
					
				}

			} catch (SocketTimeoutException e) { // ao final de 10 segundos
				System.out.println("Servidores encontrados: "+fileServersCount);
				// envia a quantidade de servidores de arquivos encontrados
				DataOutputStream response = new DataOutputStream(connectionSocket.getOutputStream());
				response.writeInt(fileServersCount);
				
				// envia os nomes dos servidores encontrados
				for (int i = 0; i < fileServersCount; i++) {
					response.write(infoFileServers[i].getBytes());
				}
				// fecha conexões
				requestSocket.close();
				connectionSocket.close();
				System.out.println("Pronto!");

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

class BackServidor {
	
	private int timeout = 120 * 1000; // em milissegundos
	private int porta = 8888;
	
	
	public BackServidor() {
		run();
	}
	
	public BackServidor(int timeout, int porta) {
		this.timeout = timeout;
		this.porta = porta;
		run();
	}




	private void run() {


		try (ServerSocket arqSocket = new ServerSocket(porta)) {
			arqSocket.setSoTimeout(timeout);

			while (true) {
				System.out.println("Aguardando conexão");
				Thread c = new Thread(new Connection(arqSocket.accept()));
				c.start();
			}
		} catch (FileNotFoundException e) {
			System.err.println("Não foi possível encontrar o arquivo");
		} catch (SocketTimeoutException e) {
			System.err.println("Tempo máximo de espera atingido!");
			System.err.println("Serviço encerrado");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
*/