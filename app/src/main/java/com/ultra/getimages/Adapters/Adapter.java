package com.ultra.getimages.Adapters;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.ultra.getimages.R;
import com.ultra.getimages.Units.ListElement;

import java.util.ArrayList;

/**
 * <p></p>
 * <p><sub>(16.07.2017)</sub></p>
 *
 * @author CC-Ultra
 */

public class Adapter extends RecyclerView.Adapter<Adapter.Holder>
	{
	ArrayList<ListElement> elements;

	class Holder extends RecyclerView.ViewHolder
		{
		TextView pathTxt;
		ImageView img;

		public Holder(View itemView)
			{
			super(itemView);
			pathTxt= (TextView)itemView.findViewById(R.id.path);
			img= (ImageView)itemView.findViewById(R.id.img);
			}
		}

	public Adapter(ArrayList<ListElement> _elements)
		{
		elements=_elements;
		}
	public void initElement(int position,String path)
		{
		ListElement element= elements.get(position);
		element.path=path;
		element.downloaded=true;
		notifyItemChanged(position);
		}
	@Override
	public Holder onCreateViewHolder(ViewGroup parent,int viewType)
		{
		View mainView= LayoutInflater.from(parent.getContext() ).inflate(R.layout.list_element,parent,false);
		return new Holder(mainView);
		}
	@Override
	public void onBindViewHolder(Holder holder,int position)
		{
		ListElement element= elements.get(position);
		if(element.downloaded)
			holder.img.setImageURI(Uri.parse(element.path) );
		else
			holder.img.setImageResource(R.drawable.placeholder);
		holder.pathTxt.setText(element.path);
		}
	@Override
	public int getItemCount()
		{
		return elements.size();
		}
	}
