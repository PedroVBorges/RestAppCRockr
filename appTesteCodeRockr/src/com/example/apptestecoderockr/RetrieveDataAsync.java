/**
 * Classe para recuperar dados do web service herdando a classe AsyncTask
 * 
 * @author  Pedro Vinícius Borges Basseto
 * @version 2.00, 19/03/14
 * 
 */
package com.example.apptestecoderockr;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class RetrieveDataAsync extends AsyncTask<Void, Void, Void> {

	/* Os parâmetros para conexão com o server */
	String feedUrl = "http://soa.coderockr.com/brand";
	String type = "application/x-www-form-urlencoded";
	String token = "85e4a615f62c711d3aac0e7def5b4903";

	/* Diretório que será criado para salvar as imagens */
	String diretorioImagens = "/CoderockrApp/";

	/*
	 * Inicialização da variável 'context' pois o progress dialog será usado em outra activity vinda pelo construtor
	 */
	Context context;
	CharSequence returnMessage = null;
	protected SQLiteDatabase BancoProdutos;
	ProgressDialog dialog;

	public RetrieveDataAsync(Context contexto) {
		context = contexto;
	}

	/* Primeira função a ser executada: chama o progressDialog */
	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(context);
		dialog.setTitle("Carregando");

		super.onPreExecute();
	}

	/* função Principal da classe */
	@Override
	protected Void doInBackground(Void... params) {

		/* Inicializa o Banco de dados */
		DatabaseHelper bancoHelper = new DatabaseHelper(context);
		BancoProdutos = bancoHelper.getReadableDatabase();

		// bancoHelper.ClearTables(BancoProdutos);

		/* Verificando Conexão com a Internet e se existe dados no database */
		Cursor cursor = BancoProdutos.rawQuery("SELECT * FROM marcas", null);

		if ((!this.isOnline()) && (cursor.getCount() == 0)) {
			/* Não possui conexão e o banco não possui registros *Deve ativar a internet */
			returnMessage = "Conecte-se ao menos uma vez!";

		} else if ((!this.isOnline()) && (cursor.getCount() != 0)) {
			/* Não possui conexão e o banco possui registros *Trabalhar OFFLINE* */
			returnMessage = "Trabalhando em modo Off-Line";

		} else if ((this.isOnline()) && (cursor.getCount() != 0)) {
			/* Esta Online e tem registros no banco *Atualizar Dados* */
			this.JsonToBase();
			returnMessage = "Dados Atualizados";

		} else if ((this.isOnline()) && (cursor.getCount() == 0)) {
			/* Esta Online e não tem registros no banco *Realizar INSERT* */
			// dialog.show();
			this.JsonToBase();
		}

		/*cursor.close();
		BancoProdutos.close();*/
		return null;

	}

	/*
	 * Ultima função a ser executada: fecha o progressDialog e retorna algum feedback
	 */
	@Override
	protected void onPostExecute(Void result) {

	   if (returnMessage != null) {
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(context, returnMessage, duration);
			toast.show();
		}

		super.onPostExecute(result);
	}
	

	/* Função para se conectar por http e retornar o dados em Json */
	private void JsonToBase() {

		JSONArray retorno = null;
		/* Inicia o cliente http e adiciona os headers necessários */
		HttpClient CodeRClient = new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(feedUrl);
		getRequest.addHeader("Content-type", type);
		getRequest.addHeader("Authorization", token);

		try {
			HttpResponse response = CodeRClient.execute(getRequest);
			StatusLine statusServidor = response.getStatusLine();
			int StatusCode = statusServidor.getStatusCode();

			/* Se o webservice não responder, ele não faz os inserts */

			if (StatusCode != 200) {
				returnMessage = "O servidor Web não responde";
			} else {
				InputStream jsonStream = response.getEntity().getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(jsonStream));
				StringBuilder builder = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				String jsonData = builder.toString();
				retorno = new JSONArray(jsonData);

				/* Realiza o INSERT ou UPDATE no banco */
				DatabaseHelper bancoHelper = new DatabaseHelper(context);
				BancoProdutos = bancoHelper.getWritableDatabase();

				/* Limpa as tabelas antes de inserir */
				bancoHelper.ClearTables(BancoProdutos);

				for (int i = 0; i < retorno.length(); i++) {

					JSONObject objInner = retorno.getJSONObject(i);
					ContentValues dadosInsert = new ContentValues();
					dadosInsert.put("_id", objInner.getString("id"));
					dadosInsert.put("created", objInner.getString("created"));
					dadosInsert.put("name", objInner.getString("name"));
					dadosInsert.put("description", objInner.getString("description"));
					/* Realiza o download do logotipo da marca e salva a url interna no banco */
					dadosInsert.put("image", imageToExternalStorage(objInner.getString("image"), diretorioImagens));

					BancoProdutos.insert("marcas", null, dadosInsert);

					/* Laço para pegar os produtos dentro das marcas e realizar o insert */
					JSONArray arrayProduto = objInner.getJSONArray("product_collection");
					for (int j = 0; j < arrayProduto.length(); j++) {
						JSONObject objProduto = arrayProduto.getJSONObject(j);
						dadosInsert.clear();
						dadosInsert.put("_id", objProduto.getString("id"));
						dadosInsert.put("created", objProduto.getString("created"));
						dadosInsert.put("description", objProduto.getString("description"));
						dadosInsert.put("featured", objProduto.getString("featured"));
						dadosInsert.put("price", objProduto.getString("price"));
						dadosInsert.put("status", objProduto.getString("status"));
						dadosInsert.put("idMarca", objInner.getString("id"));
						/* Realiza o download da imagem do produto e salva a url interna no banco */
						dadosInsert.put("snapshot", imageToExternalStorage(objProduto.getString("snapshot"), diretorioImagens));

						BancoProdutos.insert("produtos", null, dadosInsert);
					}
				}

				//BancoProdutos.close();
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/* função que verifica se o dispositivo esta Online */
	@SuppressWarnings("static-access")
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	
	/*
	 * Realiza o download das imagens para o armazenamento Entrada: url (endereço da imagem na web); 
	 * dir (diretório onde a imagem deverá ser salva) 
	 * Retorno: storageImage (Endereço da imagem no armazenamento interno ou vazio caso não consiga salvar a imagem
	 */
	public String imageToExternalStorage(String url, String dir) {

		String pathInterno = null;

		/* Verifica se diretório da imagens existe; senão existir ele cria */
		File file = new File(Environment.getExternalStorageDirectory(), dir);
		if (!file.exists()) {
			if (!file.mkdirs()) {
				Log.v("Erro", "Erro ao criar diretório");
				returnMessage = "não foi possivel criar o diretório";
			}
		}

		/* Realizando o download da imagem */
		try {
			URL objUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) objUrl.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);

			/* obtem o nome para imagem */
			String fileName = url.substring(url.lastIndexOf('/') + 1, url.length());
			String fileNameWithoutExtn = fileName.substring(0, fileName.lastIndexOf('.'));
			pathInterno = file.getPath() + "/" + fileNameWithoutExtn + ".jpg";

			FileOutputStream stream = new FileOutputStream(pathInterno);
			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			myBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outstream);
			byte[] byteArray = outstream.toByteArray();

			/* Grava a imagem no armazenamento externo */
			stream.write(byteArray);
			stream.close();

		} catch (MalformedURLException e) {
			Log.v("ERRO IMAGEM", "Erro MalformedURLException " + url);
			e.printStackTrace();
		} catch (IOException e) {
			Log.v("ERRO IMAGEM", "Erro IOException " + url);
			e.printStackTrace();
		}

		return pathInterno;
	}

}
