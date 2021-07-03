package projeto;

import java.net.*;
import java.util.concurrent.TimeUnit;
import java.io.*;

class Connection implements Runnable {

	private Socket connectionSocket;
	private String caminho;
	private String nomeArquivo;

	public String getCaminho() {
		return caminho;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

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
			InetAddress IPAddress = InetAddress.getByName("192.168.1.255");
			byte[] sendData = nomeArquivo.getBytes();
			byte[] receiveData = new byte[1024];

			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

			// envia broadcast para todos os servidores de arquivo da rede
			// receivePacket.getAddress(); // ip da máquina
			requestSocket.send(sendPacket);

			String[] infoFileServers = new String[1000];
			int fileServersCount  = 0;
			try {
				while (true) {
					// receber solicitações de todos os servidores de arquivo da rede que possuem o
					// arquivo do cliente
					requestSocket.receive(receivePacket);
					requestSocket.setSoTimeout(timeout); // reinicia o tempo
					String receive = new String(receivePacket.getData(), 0, receivePacket.getLength());
					System.out.println(receive);
					infoFileServers[fileServersCount] = receive; // salva o nome do servidor de arquivos num array
					fileServersCount++;
				}

			} catch (SocketTimeoutException e) { // ao final de 10 segundos
				/*// conta os servidores de arquivos encontrados
				int fileServersCount = 0;
				for (int i = 0; i < infoFileServers.length; i++) {
					if (infoFileServers[i] != null) 
						fileServersCount++;
					else 
						break;
				}*/
				System.out.println("Servidores encontrados: "+fileServersCount);
				// envia a quantidade de servidores de arquivos encontrados
				DataOutputStream response = new DataOutputStream(connectionSocket.getOutputStream());
				response.writeInt(fileServersCount);
				//response.writeBytes(Integer.toString(fileServersCount)+"&");
				// envia os nomes dos servidores encontrados
				TimeUnit.MILLISECONDS.sleep(2000);
				for (int i = 0; i < fileServersCount; i++) {
					response.write(infoFileServers[i].getBytes());
					System.out.println(infoFileServers[i]);
				}
				// fecha conexões
				requestSocket.close();
				connectionSocket.close();
				System.out.println("Conexão Finalizada");

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

public class Servidor {

	public static void main(String[] args) {
		int timeout = 120 * 1000; // em milissegundos
		int porta = 8888;

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
