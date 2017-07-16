package com.ultra.getimages.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.concurrent.Callable;

/**
 * <p></p>
 * <p><sub>(16.07.2017)</sub></p>
 *
 * @author CC-Ultra
 */

public class BackgroundTask<T>
	{
	private ProgressDialog dialog;
	private Subscriber<T> subscriber;
	private Callable<T> task;
	private Subscription subscription;

	private class TaskInterruptor implements DialogInterface.OnDismissListener
		{
		@Override
		public void onDismiss(DialogInterface dialog)
			{
			subscription.unsubscribe();
			Log.d(O.TAG,"onDismiss: allSusubscriptions.isUnsubscribed(): "+ subscription.isUnsubscribed() );
			}
		}

	public BackgroundTask(Context context,Callable<T> _task)
		{
		task=_task;
		dialog= new ProgressDialog(context);
		dialog.setMessage("Работаю...");
		dialog.setCancelable(false);
		dialog.setIndeterminate(true);
		dialog.setOnDismissListener(new TaskInterruptor() );
		}
	public ProgressDialog getDialog()
		{
		return dialog;
		}
	public void setSubscriber(Subscriber<T> _subscriber)
		{
		subscriber=_subscriber;
		}

	public void start()
		{
		dialog.show();

		Observable<T> obs= Observable.fromCallable(task);
		subscription= obs.
				subscribeOn(Schedulers.io() ).
				observeOn(AndroidSchedulers.mainThread() ).
				subscribe(subscriber);
		}
	}