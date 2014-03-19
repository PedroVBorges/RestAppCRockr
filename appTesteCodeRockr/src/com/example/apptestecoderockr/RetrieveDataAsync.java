/**
 * Classe para recuperar dados do web service herdando a classe AsyncTask
 * 
 * @author  Pedro Vin�cius Borges Basseto
 * @version 2.00, 19/03/14
 * 
 */
package com.example.apptestecoderockr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class RetrieveDataAsync extends AsyncTask<Void, Void, Void> {

	/* Os par�metros para conex�o com o server */
	String feedUrl = "http://soa.coderockr.com/brand";
	String type = "application/x-www-form-urlencoded";
	String token = "85e4a615f62c711d3aac0e7def5b4903";

	/*
	 * Inicializa��o da vari�vel 'context' pois o progress dialog ser� usado em outra activity vinda pelo construtor
	 */
	Context context;
	CharSequence returnMessage = null;
	protected SQLiteDatabase BancoProdutos;
	ProgressDialog dialog;
	Integer error = 0;

	public RetrieveDataAsync(Context contexto) {
		context = contexto;
	}

	/* Primeira fun��o a ser executada: chama o progressDialog */
	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(context);
		dialog.setTitle("Carregando");
		dialog.show();
		super.onPreExecute();
	}

	/* fun��o Principal da classe */
	@Override
	protected Void doInBackground(Void... params) {

		/* Inicializa o Banco de dados */
		DatabaseHelper bancoHelper = new DatabaseHelper(context);
		BancoProdutos = bancoHelper.getReadableDatabase();

		//bancoHelper.ClearTables(BancoProdutos);

		/* Verificando Conex�o com a Internet e se existe dados no database */
		Cursor cursor = BancoProdutos.rawQuery("SELECT * FROM marcas", null);

		if ((!this.isOnline()) && (cursor.getCount() == 0)) {
			/*
			 * N�o possui conex�o e o banco n�o possui registros *Deve ativar a * internet*
			 */
			returnMessage = "Conecte-se ao menos uma vez!";
			Log.v("La�o", (String) returnMessage);

		} else if ((!this.isOnline()) && (cursor.getCount() != 0)) {
			/* N�o possui conex�o e o banco possui registros *Trabalhar OFFLINE* */
			returnMessage = "Trabalhando em modo Off-Line";
			Log.v("La�o", (String) returnMessage);

		} else if ((this.isOnline()) && (cursor.getCount() != 0)) {
			/* Esta Online e tem registros no banco *Atualizar Dados* */
			Log.v("La�o", "Esta Online e tem registros no banco *Atualizar Dados");
			this.JsonToBase();

		} else if ((this.isOnline()) && (cursor.getCount() == 0)) {
			/* Esta Online e n�o tem registros no banco *Realizar INSERT* */
			Log.v("La�o", "Esta Online e n�o tem registros no banco *Realizar INSERT*");
			this.JsonToBase();

		}

		cursor.close();
		BancoProdutos.close();
		return null;

	}

	/*
	 * Ultima fun��o a ser executada: fecha o progressDialog e retorna algum feedback
	 */
	@Override
	protected void onPostExecute(Void result) {
		dialog.dismiss();

		if (returnMessage != null) {
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(context, returnMessage, duration);
			toast.show();
		}

		super.onPostExecute(result);

	}

	/* Fun��o para se conectar por http e retornar o dados em Json */
	private void JsonToBase() {

		JSONArray retorno = null;

		/* Inicia o cliente http e adiciona os headers necess�rios */
		HttpClient CodeRClient = new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(feedUrl);
		getRequest.addHeader("Content-type", type);
		getRequest.addHeader("Authorization", token);

		try {
			HttpResponse response = CodeRClient.execute(getRequest);
			StatusLine statusServidor = response.getStatusLine();
			int StatusCode = statusServidor.getStatusCode();

			/* Se o webservice n�o responder, ele n�o faz os inserts */
			
			if (StatusCode != 200) {
				returnMessage = "O servidor Web n�o responde";
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
				BancoProdutos = bancoHelper.getReadableDatabase();

				for (int i = 0; i < retorno.length(); i++) {

					JSONObject objInner = retorno.getJSONObject(i);
					ContentValues dadosInsert = new ContentValues();
					dadosInsert.put("id", objInner.getString("id"));
					dadosInsert.put("created", objInner.getString("created"));
					dadosInsert.put("image", objInner.getString("image"));
					dadosInsert.put("name", objInner.getString("name"));
					dadosInsert.put("description", objInner.getString("description"));
					BancoProdutos.insert("marcas", null, dadosInsert);
					Log.v("Realizando Insert de Marca:", objInner.getString("name"));

					/* La�o para pegar os produtos dentro das marcas e realizar o insert */
					JSONArray arrayProduto = objInner.getJSONArray("product_collection");
					for (int j = 0; j < arrayProduto.length(); j++) {
						JSONObject objProduto = arrayProduto.getJSONObject(j);
						dadosInsert.clear();
						dadosInsert.put("id", objProduto.getString("id"));
						dadosInsert.put("created", objProduto.getString("created"));
						dadosInsert.put("description", objProduto.getString("description"));
						dadosInsert.put("featured", objProduto.getString("featured"));
						dadosInsert.put("price", objProduto.getString("price"));
						dadosInsert.put("status", objProduto.getString("status"));
						dadosInsert.put("snapshot", objProduto.getString("snapshot"));
						dadosInsert.put("idMarca", objInner.getString("id"));
						BancoProdutos.insert("produtos", null, dadosInsert);
						Log.v("Realizando Insert de Produto:", objProduto.getString("description"));
					}

				}

				BancoProdutos.close();
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/* fun��o que verifica se o dispositivo esta Online */
	@SuppressWarnings("static-access")
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

}
