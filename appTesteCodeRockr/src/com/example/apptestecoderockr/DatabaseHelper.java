/**
 * Classe helper para o Databse
 * 
 * @author  Pedro Vin�cius Borges Basseto
 * @version 1.00, 19/03/14
 * 
 */
package com.example.apptestecoderockr;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper{
	
	private static final String DATABASE_NAME = "produtos";
	private static final int DATABASE_VERSION = 1;
	public Context contexto;

	public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.contexto = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		this.CreateTables(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.v("Banco Vers�o: " + String.valueOf(DATABASE_VERSION), "Mudando da vers�o " + String.valueOf(oldVersion) + " para " + String.valueOf(newVersion));
		this.DeleteTables(db);
		this.CreateTables(db);		
	}
	
	/* Fun��o para cria��o das tabelas do SQLite */
	private void CreateTables(SQLiteDatabase db){
		
		Log.v("Banco : ","criando o banco de dados pela classe createTables");
		
		String tableMarca = "create table marcas(id 		 INTEGER NOT NULL, " + 
												"created	 TEXT 	 NULL, " + 
												"image		 TEXT	 NULL, " +
												"name		 TEXT 	 NULL, " +
												"description TEXT    NULL) ";
		
		String tableProdutos = "create table produtos(id 		 INTEGER NOT NULL, " + 
													"idMarca	 INTEGER NOT NULL, " +
													"created	 TEXT 	 NULL, " + 
													"description TEXT    NULL, " +
													"featured 	 INTEGER DEFAULT 0, " +
													"price		 REAL 	 NULL, " +
													"status 	 INTEGER DEFAULT 0, " +
													"snapshot  	 TEXT	 NULL) ";
		
		/* Execu��o dos SQLs para cria��o das tabelas */
		try {
			db.execSQL(tableMarca);
			db.execSQL(tableProdutos);
		} catch (Exception e) {
			Log.e("Erro Banco", "Erro na cria��o do Banco" + e.getMessage());
		}
		
	}
	
	private void DeleteTables(SQLiteDatabase db){
		Log.v("Banco : ","Deletando Tabelas do database");
		db.execSQL("DROP TABLE IF EXISTS marcas");
		db.execSQL("DROP TABLE IF EXISTS produtos");
	}

}
