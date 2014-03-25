package com.example.apptestecoderockr;

import android.os.Bundle;
import android.app.ListFragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.SimpleCursorAdapter;

public class ProdutoDetails extends ListFragment {

	// cria uma nova instância do ProdutoDetails.
	public static ProdutoDetails newInstance(int index) {
		ProdutoDetails f = new ProdutoDetails();
		Bundle args = new Bundle();
		args.putInt("index", index);
		f.setArguments(args);
		return f;
	}

	// Retorna o index que foi passado para ListFragment pelo putExtra
	public int getShownIndex() {
		return getArguments().getInt("index", 1); //Definido 1; pois é a primeira row da tabela
	}

	//Função principal onActivityCreated; chama o loadProdutos para popular os dados
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		loadProdutos(getShownIndex());
	}

	//Função para popular os dados dos produtos no listFragment
	public void loadProdutos(Integer id) {
		DatabaseHelper db = new DatabaseHelper(getActivity());
		SQLiteDatabase BancoProdutos = db.getReadableDatabase();
		Cursor cursor = BancoProdutos.query("produtos", new String[] { "_id", "price", "description" }, "idMarca = " + String.valueOf(id), null, null, null, null);

		@SuppressWarnings("deprecation")
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_2, cursor, new String[] { "description", "price" }, new int[] { android.R.id.text1, android.R.id.text2 });
		
		setListAdapter(adapter);
	}

}
