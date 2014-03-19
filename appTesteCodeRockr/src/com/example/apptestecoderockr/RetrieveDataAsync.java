/**
 * Classe para recuperar dados do web service herdando a classe AsyncTask
 * 
 * @author  Pedro Vinícius Borges Basseto
 * @version 1.00, 17/03/14
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
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


public class RetrieveDataAsync extends AsyncTask<Void, Void, Void> {
	
	
	/* Os parametros para conexão com o server */
	String feedUrl = "http://soa.coderockr.com/brand";
	String type = "application/x-www-form-urlencoded";
	String token = "85e4a615f62c711d3aac0e7def5b4903";
	
	/* Inicialização da variável 'context' pois o progress dialog será usado em outra activity vinda pelo construtor */
	Context context;
	
	public RetrieveDataAsync(Context contexto){
		context = contexto;
	}
	
	ProgressDialog dialog;
	
	
	/* Primeira função a ser executada: chama o progressDialog */
	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(context);
		dialog.setTitle("Carregando");
		dialog.show();
		super.onPreExecute();
	}
	

	/* função Principal da classe */
	@Override
	protected Void doInBackground(Void... params) {
		
		/* Inicia o cliente http e adiciona os headers necessários */
		HttpClient CodeRClient = new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(feedUrl);
		getRequest.addHeader("Content-type", type);
		getRequest.addHeader("Authorization", token);
		
		try {
			
			HttpResponse response = CodeRClient.execute(getRequest);
			StatusLine statusServidor = response.getStatusLine();
			int StatusCode = statusServidor.getStatusCode();
			
			if(StatusCode != 200){
				return null;
			}
			
			InputStream jsonStream = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(jsonStream));
			StringBuilder builder = new StringBuilder();
			String line;
			while((line = reader.readLine()) != null){
				builder.append(line);
			}
			
			String jsonData = builder.toString();
			JSONArray array = new JSONArray(jsonData);
			
			for (int i = 0; i < array.length(); i++) {
				
				JSONObject objInner = array.getJSONObject(i);
				String id = objInner.getString("id");
				String created = objInner.getString("created");
				String image = objInner.getString("image");
				String name = objInner.getString("name");
				String description = objInner.getString("description");
				
				
				Log.v("Saida", id + " " + created + " " + image);
				
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
		
		return null;
	}
	
	/* Ultima função a ser executada: fecha o progressDialog e retorna algum feedback*/
	@Override
	protected void onPostExecute(Void result) {
		dialog.dismiss();
		super.onPostExecute(result);
	}


}
