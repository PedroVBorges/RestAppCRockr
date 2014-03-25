/**
 * Classe helper para o Databse
 * 
 * @author  Pedro Vinícius Borges Basseto
 * @version 1.00, 19/03/14
 * 
 */
package com.example.apptestecoderockr;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper{
	
	private static final String DATABASE_NAME = "produtos";
	private static final int DATABASE_VERSION = 2;
	public Context contexto;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.contexto = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		this.CreateTables(db);
		
		//Insere dados dummy no banco para inicializar os componentes
		insertDummy(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.v("Banco Versão: " + String.valueOf(DATABASE_VERSION), "Mudando da versão " + String.valueOf(oldVersion) + " para " + String.valueOf(newVersion));
		this.DeleteTables(db);
		this.CreateTables(db);		
	}
	
	/* Função para criação das tabelas do SQLite */
	private void CreateTables(SQLiteDatabase db){
		
		Log.v("Banco : ","criando o banco de dados pela classe createTables");
		
		String tableMarca = "create table marcas(_id 		 INTEGER NOT NULL, " + 
												"created	 TEXT 	 NULL, " + 
												"image		 TEXT	 NULL, " +
												"name		 TEXT 	 NULL, " +
												"description TEXT    NULL) ";
		
		String tableProdutos = "create table produtos(_id 		 INTEGER NOT NULL, " + 
													"idMarca	 INTEGER NOT NULL, " +
													"created	 TEXT 	 NULL, " + 
													"description TEXT    NULL, " +
													"featured 	 INTEGER DEFAULT 0, " +
													"price		 REAL 	 NULL, " +
													"status 	 INTEGER DEFAULT 0, " +
													"snapshot  	 TEXT	 NULL) ";
		
		/* Execução dos SQLs para criação das tabelas */
		try {
			db.execSQL(tableMarca);
			db.execSQL(tableProdutos);
		} catch (Exception e) {
			Log.e("Erro Banco", "Erro na criação do Banco" + e.getMessage());
		}
		
	}
	
	private void DeleteTables(SQLiteDatabase db){
		Log.v("Banco : ","Deletando Tabelas do database");
		db.execSQL("DROP TABLE IF EXISTS marcas");
		db.execSQL("DROP TABLE IF EXISTS produtos");
	}
	
	/* Classe para Limpar a tabela */
	public void ClearTables(SQLiteDatabase db){
		Log.v("Banco : ","Limpando Tabelas do database");
		db.execSQL("DELETE FROM marcas");
		db.execSQL("DELETE FROM produtos");		
	}
	
	/* Adiciona um Dummy no banco para inicializar os componentes */
	public void insertDummy(SQLiteDatabase db){
		ContentValues dummyMarcas = new ContentValues(); 
		ContentValues dummyProdutos = new ContentValues(); 
		
		dummyMarcas.put("_id", 1);
		db.insert("marcas", null, dummyMarcas);
		
		dummyProdutos.put("_id", 1);
		dummyProdutos.put("idMarca", 1);
		db.insert("produtos", null, dummyProdutos);		
	}
	
}
