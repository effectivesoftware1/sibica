package com.javpoblano.alcaldia.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.javpoblano.alcaldia.R;
import com.javpoblano.alcaldia.interfaces.ListInterface;
import com.javpoblano.alcaldia.models.BusquedaItem;

import java.util.List;

/**
 * Created by javpoblano on 05/01/2017.
 */

public class SearchAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<BusquedaItem> movieItems;
    private ListInterface listener;
    private int option;

    public SearchAdapter (Activity activity, List<BusquedaItem> movieItems,ListInterface listener) {
        this.activity = activity;
        this.movieItems = movieItems;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return movieItems.size();
    }

    @Override
    public Object getItem(int location) {
        return movieItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.search_row, parent,false);


        TextView direccion = (TextView) convertView.findViewById(R.id.direccion);
        TextView comuna = (TextView) convertView.findViewById(R.id.comuna);
        TextView barrio = (TextView) convertView.findViewById(R.id.barrio);
        TextView matricula = (TextView) convertView.findViewById(R.id.matricula);
        TextView predial = (TextView) convertView.findViewById(R.id.predial);
        TextView tipo = (TextView) convertView.findViewById(R.id.tipo);

        // getting movie data for the row
        final BusquedaItem m = movieItems.get(position);

        //user.setText(m.name);
        direccion.setText("Dirección : \n"+m.getDireccion());
        comuna.setText("Comuna : \n"+m.getComuna());
        barrio.setText("Barrio : \n"+m.getBarrio());
        matricula.setText("Matrícula : \n"+m.getMatricula());
        predial.setText("Predial : \n"+m.getPredial());
        tipo.setText("Tipo : \n"+m.getTipo());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onListItemClick(m);
            }
        });

        return convertView;
    }

}
