package com.ultra.getimages.Activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.ultra.getimages.Adapters.Adapter;
import com.ultra.getimages.App;
import com.ultra.getimages.R;
import com.ultra.getimages.Units.ListElement;
import com.ultra.getimages.Utils.BackgroundTask;
import com.ultra.getimages.Utils.O;
import rx.Subscriber;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p></p>
 * <p><sub>(16.07.2017)</sub></p>
 *
 * @author CC-Ultra
 */

public class ImgListActivity extends AppCompatActivity
	{
	private RecyclerView recyclerList;
	private ArrayList<File> filelist;
	private Adapter adapter;
	private Receiver receiver;

	private class Receiver extends BroadcastReceiver
		{
		@Override
		public void onReceive(Context context,Intent intent)
			{
			int position= intent.getIntExtra(O.extra.POSITION,0);
			String path= intent.getStringExtra(O.extra.PATH);
			adapter.initElement(position,path);
			Toast.makeText(context,"Фото "+ position +" загружено",Toast.LENGTH_SHORT).show();
			}
		}
	private class DownloadFileTask implements Runnable
		{
		private Context context;
		private int index;
		private String filename;

		DownloadFileTask(Context _context,int _index,String _filename)
			{
			context=_context;
			filename=_filename;
			index=_index;
			}

		@Override
		public void run()
			{
			File dir= getFileDir();
			if(dir==null)
				return;
			File file= new File(dir.getAbsolutePath() +"/"+ filename);
			try
				{
				FileOutputStream fileOut= new FileOutputStream(file);
				String dBoxPath= O.SRC_DIR +"/"+ filename;
				App.dBoxApi.getFile(dBoxPath,null,fileOut,null);
				fileOut.close();
				}
			catch(IOException ioErr)
				{
				Log.e(O.TAG,"run: ", ioErr);
				}
			catch(DropboxException dbErr)
				{
				Log.e(O.TAG,"run: ", dbErr);
				}
			Intent data= new Intent(O.ACTION_SET_PIC);
			data.putExtra(O.extra.PATH,file.getAbsolutePath() );
			data.putExtra(O.extra.POSITION,index);
			context.sendBroadcast(data);
			}
		}
	private class FileListCallable implements Callable<Boolean>
		{
		@Override
		public Boolean call() throws Exception
			{
			filelist=getFileList();
			return true;
			}
		}
	private class FileListSubScriber extends Subscriber<Boolean>
		{
		ProgressDialog dialog;

		FileListSubScriber(ProgressDialog _dialog)
			{
			dialog=_dialog;
			}

		@Override
		public void onCompleted()
			{
			Log.d(O.TAG,"onCompleted: ");
			}
		@Override
		public void onError(Throwable e)
			{
			Log.e(O.TAG,"onError: ",e);
			}
		@Override
		public void onNext(Boolean aBoolean)
			{
			Toast.makeText(ImgListActivity.this,"Список файлов получен",Toast.LENGTH_SHORT).show();
			dialog.dismiss();
			initAdapter();
			runPhotoDownloads();
			}
		}

	private void runPhotoDownloads()
		{
		ExecutorService es= Executors.newFixedThreadPool(3);
		for(int i=0; i<filelist.size(); i++)
			{
			DownloadFileTask task= new DownloadFileTask(this,i,filelist.get(i).getName() );
			es.execute(task);
			}
		}
	private ArrayList<File> getFileList()
		{
		ArrayList<File> result= new ArrayList<>();
		try
			{
			DropboxAPI.Entry metadata=App.dBoxApi.metadata(O.SRC_DIR,1000,null,true,null);
			for(DropboxAPI.Entry content : metadata.contents)
				{
				File file= new File(content.fileName() );
				result.add(file);
				}
			}
		catch(DropboxException dboxErr)
			{
			Log.e(O.TAG,"getFileList: ",dboxErr);
			}
		return result;
		}
	private File getFileDir()
		{
		File dir;
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) )
			dir= getExternalCacheDir();
		else
			dir= getCacheDir();
		if(dir==null)
			{
			Log.d(O.TAG,"getStoredPicURI: путь к папке кэша не найден");
			return null;
			}
		return dir;
		}
	private void initAdapter()
		{
		ArrayList<ListElement> elements= new ArrayList<>();
		for(File file : filelist)
			elements.add(new ListElement() );
		adapter= new Adapter(elements);
		recyclerList.setAdapter(adapter);
		recyclerList.setLayoutManager(new LinearLayoutManager(this) );
		}
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
		{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.img_list_layout);

		recyclerList= (RecyclerView)findViewById(R.id.list);
		BackgroundTask<Boolean> backgroundTask= new BackgroundTask<>(this,new FileListCallable() );
		backgroundTask.setSubscriber(new FileListSubScriber(backgroundTask.getDialog() ) );
		backgroundTask.start();

		receiver= new Receiver();
		IntentFilter filter= new IntentFilter(O.ACTION_SET_PIC);
		registerReceiver(receiver,filter);
		}

	@Override
	protected void onDestroy()
		{
		super.onDestroy();
		unregisterReceiver(receiver);
		}
	}
