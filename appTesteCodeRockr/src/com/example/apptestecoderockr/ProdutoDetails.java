package com.example.apptestecoderockr;

import android.os.Bundle;
import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ProdutoDetails extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.produto_details, container, false);
	}

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
		return getArguments().getInt("index", 1); // Definido 1; pois é a primeira row da tabela
	}

	// Função principal onActivityCreated; chama o loadProdutos para popular os dados
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		loadProdutos(getShownIndex());
	}

	// Função para popular os dados dos produtos no listFragment
	public void loadProdutos(Integer id) {
		DatabaseHelper db = new DatabaseHelper(getActivity());
		SQLiteDatabase BancoProdutos = db.getReadableDatabase();

		// Populando o cabeçalho da lista de produtos através de uma classe(util.JustifiedTextView) que justifica o texto
		Cursor cursorMarca = BancoProdutos.query("marcas", new String[] { "image", "name", "description" }, "_id = " + String.valueOf(id), null, null, null, null);
		cursorMarca.moveToFirst();
		TextView marca_description = (TextView) getActivity().findViewById(R.id.marca_Description);
		marca_description.setText(cursorMarca.getString(cursorMarca.getColumnIndex("description")));

		// Populando a imagem se ela existir; senão existir exibe somente o Nome
		String pathImagem = cursorMarca.getString(cursorMarca.getColumnIndex("image"));
		if (pathImagem != null) {
			ImageView imageMarca = (ImageView) getActivity().findViewById(R.id.imagem_Marca);
			Bitmap bmp = BitmapFactory.decodeFile(cursorMarca.getString(cursorMarca.getColumnIndex("image")));
			Bitmap cropped = Bitmap.createBitmap(bmp, 70, 100, bmp.getWidth()-100, bmp.getHeight()-160);
			imageMarca.setImageBitmap(cropped);
		} else {
			TextView marcaNome = (TextView) getActivity().findViewById(R.id.marca_Nome);
			marcaNome.setText(cursorMarca.getString(cursorMarca.getColumnIndex("name")));
		}

		// Populando os dados no listview customizado
		Cursor cursorProduto = BancoProdutos.query("produtos", new String[] { "_id", "price", "description", "snapshot" }, "idMarca = " + String.valueOf(id), null, null, null, null);
		ProdutoCursorAdapter adapter = new ProdutoCursorAdapter(getActivity(), R.layout.produto_details_listview, cursorProduto, new String[] { "description", "price", "snapshot" }, new int[] { R.id.description, R.id.price });
		ListView list = (ListView) getActivity().findViewById(R.id.produtos_listview);
		list.setAdapter(adapter);

	}

}
