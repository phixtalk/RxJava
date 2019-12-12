package com.rxjava;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.rxjava.models.Task;
import com.rxjava.util.DataSource;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Observable<Task> taskObservable = Observable
                .fromIterable(DataSource.createTasksList())//creates an observable from a list of objects
                .subscribeOn(Schedulers.io())//specify where the work will be done (background thread)
                //we can decide a filter on the background thread
                .filter(new Predicate<Task>() {
                    @Override
                    public boolean test(Task task) throws Exception {
                        Log.d(TAG, "test: " + Thread.currentThread().getName());
                        //note we are sleeping the thread on a background thread, so the ui is not affected
                        Thread.sleep(1000);
                        return task.isComplete();//filters out incomplete tasks
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());//specify where to observe the results from


        //next create an observer that subscribes to the observable
        taskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposables.add(d);
                //this method will be called as soon as the observable is subscribed to
                Log.d(TAG, "fromIterable onSubscribe: called");
            }

            @Override
            public void onNext(Task task) {
                //this method will be called as the observable iterates through the observables
                //we specified this to be observed on the main thread: observeOn(AndroidSchedulers.mainThread()
                Log.d(TAG, "fromIterable onNext: " + Thread.currentThread().getName());
                Log.d(TAG, "fromIteraable onNext: " + task.getDescription());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e );
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: Called");
            }
        });



        // Instantiate the object to become an Observable
        final Task task = new Task("Walk the dog", false, 4);


        // Create the Observable
        Observable<Task> singleTaskObservable = Observable
                .create(new ObservableOnSubscribe<Task>() {
                    @Override
                    public void subscribe(ObservableEmitter<Task> emitter) throws Exception {
                        if(!emitter.isDisposed()){
                            emitter.onNext(task);
                            emitter.onComplete();
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        // Subscribe to the Observable and get the emitted object
        singleTaskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Task task) {
                Log.d(TAG, "createOperator onNext: single task: " + task.getDescription());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });


//        Observable
//                .fromIterable(DataSource.createTasksList())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<Task>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(Task task) {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });


//        Example Using the Map transformation operator
//        The Map operator Applies a function to each emitted item. It transforms each emitted item by applying a function to it.
//        For example, we will map a Task object to a string.
//        The map object will take a function an input, and apply that function to every data been emitted

        //here is the function
        Function<Task, String> extractDescriptionFunction = new Function<Task, String>() {
            @Override
            public String apply(Task task) throws Exception {
                Log.d(TAG, "apply: doing work on thread: " + Thread.currentThread().getName());
                return task.getDescription();
            }
        };

        Observable<String> mapOperator = Observable
                .fromIterable(DataSource.createTasksList())
                .map(extractDescriptionFunction)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());


        mapOperator.subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "mapOperator onNext: single task: " + s);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }
}
