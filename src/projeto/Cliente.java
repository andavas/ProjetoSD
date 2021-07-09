package projeto;

import java.io.*;
import java.net.*;

class Cliente {
	
	private String nomeArquivo = "linuxmint-20-cinnamon-64bit (1).iso";//"Date_A_Live_3_01_Boteco-Anbient.mkv"; // nome do arquivo com extensão
	private String ipServidor = "127.0.0.1";
	private int portaServidor = 8888; 
	private int portaTCPServidorArquivo = 9999;
	
	public Cliente(){
		run();
	}
	
	
	public Cliente(String nomeArquivo, String ipServidor, int portaServidor, int portaTCPServidorArquivo) {
		this.nomeArquivo = nomeArquivo;
		this.ipServidor = ipServidor;
		this.portaServidor = portaServidor;
		this.portaTCPServidorArquivo = portaTCPServidorArquivo;
		run();
	}




	private void run() {
		
		// seleção do arquivo
		//String caminhoDestino = "/home/andavas/prog/2020.2/SD"; // destino do arquivo
		try {
			
			// conectar ao servidor central
			Socket clientSocket = new Socket(ipServidor, portaServidor);
			DataOutputStream request = new DataOutputStream(clientSocket.getOutputStream());
			System.out.println("Solicitando arquivo...");
			request.writeBytes(nomeArquivo + '\n');
			
			// recebendo os servidores de arquivos disponíveis do servidor principal
			System.out.println("Aguardando servidor principal...");
			BufferedReader response = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			DataInputStream sdis = new DataInputStream(clientSocket.getInputStream());
			int fileServersCount = sdis.readInt();

			
			System.out.println("Servidores encontrados "+fileServersCount);
			//criar um for para receber os servidores de arquivo disponíveis
			
			if (fileServersCount > 0)
			{
				String [][] fileServersList = new String[fileServersCount][2];
				String temp;
				for (int i = 0; i < fileServersCount; i++) {
					temp = response.readLine();
					fileServersList[i][0] = temp.substring(0,temp.indexOf("&"));
					fileServersList[i][1] = temp.substring(temp.indexOf("&")+1);
				}
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

	private void receberArquivo(String [] ServidorArquivo) {
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
