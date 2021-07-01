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
		
		try {
			
			// conectar ao servidor central
			Socket clientSocket = new Socket(ipServidor, portaServidor);
			DataOutputStream request = new DataOutputStream(clientSocket.getOutputStream());
			request.writeBytes(nomeArquivo + '\n');
			
			BufferedReader response = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String respostaServidor = response.readLine(); // recebe a quantidade de servidores de arquivo disponíveis
			
			respostaServidor = respostaServidor.substring(0,respostaServidor.indexOf("&")); // gambiarra para limpar lixo
			
			int fileServersCount = Integer.parseInt(respostaServidor);
			String [] fileServersList = new String[fileServersCount];
			
			System.out.println("Servidores encontrados "+fileServersCount);
			//criar um for para receber os servidores de arquivo disponíveis
			for (int i = 0; i < fileServersCount; i++) {
				response = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				fileServersList[i] = response.readLine();
				System.out.println(fileServersList[i]); // descobrir por que isso aqui está retornando nulo
			}
			
			clientSocket.close();
			
			/*
			// estabelece conexão com servidores de arquivo 
			Socket fileSocket = new Socket(ipDestino, portaDestino);
			DataOutputStream fileRequest = new DataOutputStream(clientSocket.getOutputStream());
			request.writeBytes(nomeArquivo + '\n');
			// aguardando mensagem de retorno do servidor de arquivos
			InputStream fileResponse = clientSocket.getInputStream();
			byte[] rawArq = response.readAllBytes();
			FileOutputStream fos = new FileOutputStream(caminhoDestino+"/"+nomeArquivo); // substitua "/" por "\\" no Windows
			fos.write(rawArq);
			fos.close();
			clientSocket.close();
			*/
		}
		catch(OutOfMemoryError e) {
			System.err.println("Eita, o arquivo é muito grande!");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
