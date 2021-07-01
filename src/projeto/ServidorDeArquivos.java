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

			File ptrArquivo = new File(nomeArquivo);
			FileInputStream fis = new FileInputStream(ptrArquivo);
			DataOutputStream response = new DataOutputStream(socket.getOutputStream());
			byte[] arqBytes = new byte[(int) ptrArquivo.length()];
			System.out.println("Lendo arquivo");
			fis.read(arqBytes);
			fis.close();
			System.out.println("Enviando...");
			response.write(arqBytes);
			System.out.println("Enviado!");
			socket.close();
			System.out.println("Conexão Finalizada");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

class ListenConnection implements Runnable {
	/* 
	 * Vai escutar as solicitações UDP broadcast
	 * que vierem do servidor principal
	 */
	private int porta;
	private String caminho; 
	
	public ListenConnection(int porta, String caminho) {
		this.porta = porta;
		this.caminho = caminho;
	}
	
	@Override
	public void run() {

		try (DatagramSocket serverSocket = new DatagramSocket(porta)) {
			byte[] receiveData = new byte[1024];
			byte[] sendData = new byte[1024];
			 
			while (true) {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				System.out.println("Aguardando solicitação");
				serverSocket.receive(receivePacket); // recebe broadcast UDP do servidor principal 
				String filename = new String(receivePacket.getData(), 0, receivePacket.getLength());
				System.out.println("Verificando se arquivo existe no disco");
				System.out.println(caminho+"/"+filename);
				if (new File(caminho+"/"+filename).isFile()) // se o ponteiro do arquivo apontar pra um arquivo existente no disco
				{
					InetAddress IPAddress = receivePacket.getAddress();   // pega o ip do servidor principal
					int port = receivePacket.getPort();					  // pega a porta do servidor principal
					String identification = IPAddress.getHostName();      // pega o nome do servidor de arquivos
					sendData = identification.getBytes(); 
					System.out.println("Reenviando!");
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
					serverSocket.send(sendPacket);      // devolve para o servidor principal via UDP
				}
				else {
					System.out.println("Arquivo não encontrado");
				}
			}
		} catch (Exception e) {
			e.printStackTrace(); 
		}
	}
}




public class ServidorDeArquivos {

	public static void main(String[] args) {
		int timeout = 120 * 1000; // em milissegundos
		int portaArq = 9999;
		int portaComunicacao = 9876;
		String caminho = "/media/Dados/Imagens"; // caminho padrão do servidor de arquivos
		System.out.println("Servidor de arquivos");
		
		Thread listener = new Thread(new ListenConnection(portaComunicacao,caminho));
		listener.start();
		
		try (ServerSocket arqSocket = new ServerSocket(portaArq)) {
			arqSocket.setSoTimeout(timeout);
			while (true) {
				System.out.println("Aguardando conexão de transferência de arquivos");
				Thread c = new Thread(new FileConnection(arqSocket.accept(),caminho));
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
