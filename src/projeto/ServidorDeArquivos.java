package projeto;

import java.io.*;
import java.net.*;

class FileConnection implements Runnable {
	
	private Socket socket;
	private String nomeArquivo;
	
	
	public FileConnection(Socket newSocket) {
		socket = newSocket;

	}
	
	public void run() {
		try {
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
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}


public class ServidorDeArquivos {

	public static void main(String[] args) {
		int timeout = 120 * 1000; // em milissegundos
		int porta = 9999;
		
		try (ServerSocket arqSocket = new ServerSocket(porta)) {
			arqSocket.setSoTimeout(timeout);
			System.out.println("Servidor de arquivos");
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
