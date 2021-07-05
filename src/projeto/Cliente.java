package projeto;

import java.io.*;
import java.net.*;

public class Cliente {

	public static void main(String args[]) {
		// seleção do arquivo
		//String caminhoDestino = "/home/andavas/prog/2020.2/SD"; // destino do arquivo
		String nomeArquivo = "Date_A_Live_3_01_Boteco-Anbient.mkv"; // nome do arquivo com extensão
		String ipServidor = "127.0.0.1";
		int portaServidor = 8888; 
		int portaTCPServidorArquivo = 9999;
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

			String [][] fileServersList = new String[fileServersCount][2];
			
			
			System.out.println("Servidores encontrados "+fileServersCount);
			//criar um for para receber os servidores de arquivo disponíveis
			
			if (fileServersCount > 0)
			{
				String temp;
				for (int i = 0; i < fileServersCount; i++) {
					temp = response.readLine();
					fileServersList[i][0] = temp.substring(0,temp.indexOf("&"));
					fileServersList[i][1] = temp.substring(temp.indexOf("&")+1);
				}
				System.out.println("Lista de servidores pronta!");
				if (fileServersCount == 1) {
					receberArquivo(fileServersList[0],portaTCPServidorArquivo,nomeArquivo);
				} else { //usuário clica no servidor de arquivos que ele quer
					System.out.println("Else");
					receberArquivo(fileServersList[0],portaTCPServidorArquivo,nomeArquivo); 
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

	public static void receberArquivo(String [] ServidorArquivo, int portaServidorArquivo, String nomeArquivo) {
		// estabelece conexão com servidores de arquivo 
		try {
			
			System.out.println("Solicitando arquivo ao servidor...");
			System.out.println("Nome:  "+ServidorArquivo[0]);
			System.out.println("IP:    "+ServidorArquivo[1]);
			System.out.println("Porta: "+portaServidorArquivo);
			Socket fileSocket = new Socket(ServidorArquivo[1], portaServidorArquivo);
			DataOutputStream fileRequest = new DataOutputStream(fileSocket.getOutputStream());
			fileRequest.writeBytes(nomeArquivo + '\n');
			// aguardando mensagem de retorno do servidor de arquivos
			InputStream fileResponse = fileSocket.getInputStream();
			byte[] rawArq = fileResponse.readAllBytes();
			FileOutputStream fos = new FileOutputStream(nomeArquivo);
			fos.write(rawArq); // escreve arquivo no disco
			System.out.println("Recebido!");
			fos.close();
			fileSocket.close();
		} catch (IOException e) {
			System.err.println("Não foi possível criar o arquivo");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
