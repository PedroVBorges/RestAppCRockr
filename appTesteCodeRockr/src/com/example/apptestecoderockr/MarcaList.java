package com.example.apptestecoderockr;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class MarcaList extends ListFragment {
    boolean mDualPane;
    int mCurCheckPosition = 1;
    

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DatabaseHelper db = new DatabaseHelper(getActivity());
		SQLiteDatabase BancoProdutos = db.getReadableDatabase();
		Cursor cursor = BancoProdutos.query("marcas", new String[] {"_id", "name", "description"}, null, null, null, null, null);
		
		
		
		@SuppressWarnings("deprecation")
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_2, cursor,  new String[] {"name", "description"}, new int[] {android.R.id.text1, android.R.id.text2});
		
        
        /* Popula os dados do cursor com o listview */
        setListAdapter(adapter);
       

        /* Checa se a tela tem inicializado o fragment 2, para assim saber se é tablet ou smartphone */
        View detailsFrame = getActivity().findViewById(R.id.details);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (savedInstanceState != null) {
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }

        if (mDualPane) {
            // em modo landscape no tablet, o item da lista fica checkado
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            showDetails(mCurCheckPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	Cursor _IDCursor = (Cursor) l.getItemAtPosition(position);
    	Integer _IDCollum = _IDCursor.getInt(_IDCursor.getColumnIndexOrThrow("_id"));
        showDetails(_IDCollum);
    }

    /**
     * função Helper para mostrar os detalhes da marca selecionada
     * ele irá exibir o fragmento caso esteja em um tablet ou 
     * chamar uma nova activity caso esteja utilizando uma tela small.
     */
    void showDetails(int index) {
        mCurCheckPosition = index;

        if (mDualPane) {
        	Log.v("Entrou no activity tablet", "Entrou no activity tablet");
          
            getListView().setItemChecked(index, true);

            // checa qual fragment esta sendo exibido, ele da um replace se for necessário            
            ProdutoDetails details = (ProdutoDetails) getFragmentManager().findFragmentById(R.id.details);
            if (details == null || details.getShownIndex() != index) {
                // Make new fragment to show this selection.
                details = ProdutoDetails.newInstance(index);
               
                // Executa o transaction, dando replace em qualquer fragment
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.details, details);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

        } else {
           
        	Log.v("Entrou no activity single", "Entrou no activity single " + index);
            Intent intent = new Intent();
            intent.setClass(getActivity(), ProdutoActivity.class);
           
            intent.putExtra("index", index);
            startActivity(intent);
        }
    }
}