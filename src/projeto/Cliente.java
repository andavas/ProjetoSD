package projeto;

import java.io.*;
import java.net.*;

public class Cliente {

	public static void main(String args[]) {
		// seleção do arquivo
		
		//String caminho = "/media/Dados/Imagens"; // caminho do arquivo de origem
		String nomeArquivo = "9ee56ad0e6057182457847a2855d2e7e2209d434v2_hq.jpg"; // nome do arquivo com extensão
		String caminhoDestino = "/home/andavas/prog/2020.2/SD"; // destino do arquivo
		String ipServidor = "127.0.0.1";
		int portaServidor = 8888; // substituir pela porta do servidor de arquivo
		int portaTCPServidorArquivo = 9999;
		try {
			
			// conectar ao servidor central
			Socket clientSocket = new Socket(ipServidor, portaServidor);
			DataOutputStream request = new DataOutputStream(clientSocket.getOutputStream());
			request.writeBytes(nomeArquivo + '\n');

			BufferedReader response = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			DataInputStream sdis = new DataInputStream(clientSocket.getInputStream());
			int fileServersCount = sdis.readInt();

			String [] fileServersList = new String[fileServersCount];
			
			
			System.out.println("Servidores encontrados "+fileServersCount);
			//criar um for para receber os servidores de arquivo disponíveis
			
			if (fileServersCount > 0)
			{

				for (int i = 0; i < fileServersCount; i++) {
					fileServersList[i] = response.readLine();
					System.out.println(fileServersList[i]); // descobrir por que isso aqui está retornando nulo
				}
				
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

	public static void receberArquivo(String IPServidorArquivo, int portaServidorArquivo, String nomeArquivo) {
		// estabelece conexão com servidores de arquivo 
		try {
			System.out.println("Solicitando arquivo ao servidor...");
			System.out.println("IP: "+IPServidorArquivo);
			System.out.println("Porta: "+portaServidorArquivo);
			Socket fileSocket = new Socket(IPServidorArquivo, portaServidorArquivo);
			DataOutputStream fileRequest = new DataOutputStream(fileSocket.getOutputStream());
			fileRequest.writeBytes(nomeArquivo + '\n');
			// aguardando mensagem de retorno do servidor de arquivos
			InputStream fileResponse = fileSocket.getInputStream();
			byte[] rawArq = fileResponse.readAllBytes();
			FileOutputStream fos = new FileOutputStream(nomeArquivo); // substitua "/" por "\\" no Windows
			fos.write(rawArq);
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
