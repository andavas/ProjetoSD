package projeto;

import java.util.*;
import java.net.*;
import java.io.*;

class Connection implements Runnable {

	private Socket connectionSocket;
	private String caminho;
	private String nomeArquivo;

	private void setCaminhoNomeArquivo(String request) {

		this.caminho = request.substring(0, request.indexOf("&"));
		this.nomeArquivo = request.substring(request.indexOf("&") + 1);
	}

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
			InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
			byte [] sendData = nomeArquivo.getBytes();
			byte [] receiveData = new byte [1024];
			
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			
			// envia broadcast para todos os servidores de arquivo da rede
			requestSocket.send(sendPacket);
			
			String [] infoFileServers = new String[1000];
			try {
				int i = 0;
				while (true) {
					 // receber solicitações de todos os servidores de arquivo da rede que possuem o arquivo do cliente
					 requestSocket.receive(receivePacket);
					 String receive = new String(receivePacket.getData(), 0, receivePacket.getLength());
					 infoFileServers[i] = receive;
					 i++;
				}
			
			} catch (SocketTimeoutException e) { // ao final de 10 segundos
				
				for (String s : infoFileServers) {
					DataOutputStream response = new DataOutputStream(connectionSocket.getOutputStream());
					response.write(s.getBytes());
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
