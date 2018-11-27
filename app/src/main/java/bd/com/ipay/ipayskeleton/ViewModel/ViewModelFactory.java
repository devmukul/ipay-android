package bd.com.ipay.ipayskeleton.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import java.lang.reflect.InvocationTargetException;

import bd.com.ipay.android.utility.TransactionHistoryType;

public class ViewModelFactory {

	public static TransactionHistoryViewModelFactory
	getTransactionHistoryViewModelFactory(TransactionHistoryType transactionHistoryType,
	                                      @NonNull Application application) {
		return new TransactionHistoryViewModelFactory(transactionHistoryType, application);
	}

	static class TransactionHistoryViewModelFactory extends
			ViewModelProvider.AndroidViewModelFactory {

		@NonNull
		private final TransactionHistoryType transactionHistoryType;

		@NonNull
		private final Application application;

		TransactionHistoryViewModelFactory(
				@NonNull TransactionHistoryType transactionHistoryType,
				@NonNull Application application) {
			super(application);
			this.transactionHistoryType = transactionHistoryType;
			this.application = application;
		}

		@NonNull
		@Override
		public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
			if (AndroidViewModel.class.isAssignableFrom(modelClass)) {
				//noinspection TryWithIdenticalCatches
				try {
					return modelClass
							.getConstructor(TransactionHistoryType.class, Application.class)
							.newInstance(transactionHistoryType, application);
				} catch (NoSuchMethodException e) {
					throw new RuntimeException("Cannot create an instance of " + modelClass, e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException("Cannot create an instance of " + modelClass, e);
				} catch (InstantiationException e) {
					throw new RuntimeException("Cannot create an instance of " + modelClass, e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException("Cannot create an instance of " + modelClass, e);
				}
			}
			return super.create(modelClass);
		}
	}
}
