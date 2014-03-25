package com.example.apptestecoderockr;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ProdutoCursorAdapter extends SimpleCursorAdapter {

	 private int layout;
     private final LayoutInflater inflater;
	
	@SuppressWarnings("deprecation")
	public ProdutoCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
		super(context, layout, c, from, to);
		 this.layout=layout;
         this.inflater=LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		//return super.newView(context, cursor, parent);
		return inflater.inflate(layout, null);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		super.bindView(view, context, cursor);
	
	
		/*
		 * Dados como o created (data) e a imagem, precisam ser tratados antes de popular; 
		 * já os outros retornam no super.bindview pois podem passar direto
		 */
		if(cursor.getString(cursor.getColumnIndex("price")) != null){
			TextView price = (TextView) view.findViewById(R.id.price);
			price.setText("R$ " + cursor.getString(cursor.getColumnIndex("price")));
		}
	
		
		ImageView snapshot = (ImageView) view.findViewById(R.id.snapshot);
				
		//Corta a imagem centralmente mantendo o aspect ratio
		if(cursor.getString(cursor.getColumnIndex("snapshot")) != null){
			Bitmap bmp = BitmapFactory.decodeFile(cursor.getString(cursor.getColumnIndex("snapshot")));
			Bitmap cropped = Bitmap.createBitmap(bmp, 50, 50, 180, 130);
			snapshot.setImageBitmap(cropped);
		}
			
	}

	

	
	
}
