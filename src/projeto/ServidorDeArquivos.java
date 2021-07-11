package projeto;

import java.io.*;
import java.net.*;

class FileConnection implements Runnable {

	private Socket socket;
	private String nomeArquivo;
	private String caminho;

	public FileConnection(Socket newSocket, String caminho) {
		socket = newSocket;
		this.caminho = caminho;
	}

	public void run() {
		try {
			BufferedReader request = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			nomeArquivo = request.readLine();
			System.out.println("Solicitando arquivo \"" + nomeArquivo + "\"");

			File ptrArquivo = new File(caminho + "/" + nomeArquivo);
			FileInputStream fis = new FileInputStream(ptrArquivo);
			OutputStream chunk = socket.getOutputStream();
			final int bufferLength = 1024;
			byte[] arqBytes = new byte[bufferLength];
			System.out.println("Enviando...");
			int totalBytesRead = 0;
			while (true) { // lendo o arquivo
				int bytesRead = fis.read(arqBytes);

				if (bytesRead != -1) { // envia o perdaço do arquivo
					chunk.write(arqBytes, 0, bytesRead);
					chunk.flush();
					totalBytesRead += bytesRead;
					System.out.println(
							"Enviando: " + totalBytesRead / 1024 + "KB / " + ptrArquivo.length() / 1024 + " KB");
				} else { // fim do arquivo
					System.out.println("Enviado!");
					break;

				}
			}
			fis.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

class ListenConnection implements Runnable {
	/*
	 * Vai escutar as solicitações UDP broadcast que vierem do servidor principal
	 */
	private int portaComunicacao;
	private int portaArq;
	private String caminho;

	public ListenConnection(int porta, String caminho) {
		this.portaComunicacao = porta;
		this.caminho = caminho;
	}

	public ListenConnection(int porta, int portaArq, String caminho) {
		this.portaComunicacao = porta;
		this.portaArq = portaArq;
		this.caminho = caminho;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run() {

		try (MulticastSocket serverSocket = new MulticastSocket(portaComunicacao)) {

			serverSocket.joinGroup(InetAddress.getByName("239.0.0.1"));
			byte[] receiveData = new byte[1024];
			byte[] sendData = new byte[1024];

			while (true) {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				System.out.println("Aguardando solicitação");
				serverSocket.receive(receivePacket); // recebe broadcast UDP do servidor principal
				String filename = new String(receivePacket.getData(), 0, receivePacket.getLength());
				System.out.println("Verificando se arquivo existe no disco");
				System.out.println(caminho + "/" + filename);
				if (new File(caminho + "/" + filename).isFile()) // se o ponteiro do arquivo apontar pra um arquivo
																	// existente no disco
				{
					InetAddress IPAddress = receivePacket.getAddress(); // pega o ip do servidor principal
					int port = receivePacket.getPort(); // pega a porta do servidor principal
					String identification = InetAddress.getLocalHost().getHostName() + "-" + IPAddress.getHostName()
							+ ":" + portaArq + "\n"; // pega o nome do servidor de

					sendData = identification.getBytes();
					System.out.println("Reenviando!");
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
					serverSocket.send(sendPacket); // devolve para o servidor principal via UDP
					System.out.println("Enviado!");
				} else {
					System.out.println("Arquivo não encontrado");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class ServidorDeArquivos {

	private int timeout = 120 * 1000; // em milissegundos
	private int portaArq = 9999;
	private int portaComunicacao = 9876;
	private String caminho = "/media/Dados/Imagens de Sistema (.iso)";// "/media/Dados/Vídeos/Date A Live 3"; // caminho
																		// padrão do servidor de arquivos

	public ServidorDeArquivos() {
		run();
	}

	public ServidorDeArquivos(int timeout, int portaArq, int portaComunicacao, String caminho) {
		super();
		this.timeout = timeout;
		this.portaArq = portaArq;
		this.portaComunicacao = portaComunicacao;
		this.caminho = caminho;
		run();
	}

	private void run() {

		System.out.println("Servidor de arquivos");

		while (true) {

			try (ServerSocket arqSocket = new ServerSocket(portaArq)) { // cria socket tcp
				Thread listener = new Thread(new ListenConnection(portaComunicacao, portaArq, caminho)); // cria socket
																											// udp
				listener.start();
				arqSocket.setSoTimeout(timeout);

				System.out.println("Aguardando conexão de transferência de arquivos");
				Thread c = new Thread(new FileConnection(arqSocket.accept(), caminho));
				c.start();
			} catch (BindException e) { // endereços em uso > tenta uma nova porta
				portaArq++;
			} catch (FileNotFoundException e) {
				System.err.println("Não foi possível encontrar o arquivo");
				break;
			} catch (SocketTimeoutException e) {
				System.err.println("Tempo máximo de espera atingido!");
				System.err.println("Serviço encerrado");
				break;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}

		}
	}
}
