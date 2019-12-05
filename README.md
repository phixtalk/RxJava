# RxJava

In general, RxJava is all about Observables and Observers, emitting data and viewing data

Its basically an evolution of AsynTasks, that allows us to perform tasks on the background thread, without
affecting or freeze the UI thread.

An Observable is component that produces some data (an example is a newspaper company that publishes newspaper articles daily)

An Observer is some function that subscribes to an observable to get the data it emits (an example is a person that is subscribing
to the newsletter which the newspaper company produces)

RXJava Operators follows the steps below

1.Create an Observable
2.Apply an Operator to the observable
3.Designate what thread to do the work on.
4.Designate what thread to emit the results to
5.Subscribe an Observer to the Observable and view the results



fromIterable operation takes a list of objects and turns them into an observable






A simple example is seen below:

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
                //this method will be called as soon as the observable is subscribed to
                Log.d(TAG, "onSubscribe: called");
            }

            @Override
            public void onNext(Task task) {
                //this method will be called as the observable iterates through the observables
                //we specified this to be observed on the main thread: observeOn(AndroidSchedulers.mainThread()
                Log.d(TAG, "onNext: " + Thread.currentThread().getName());
                Log.d(TAG, "onNext: " + task.getDescription());
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


So in summary, the rxjava transaction is quite simple and straightforward:
   -Create an observable using one of the operators: (create,just,fromiterable...)
   -Specify the thread to do the work on: subscribeOn(Schedulers.io())
   -Specify the thread to observe the results on: observeOn(AndroidSchedulers.mainThread())
   -Finally, subscribe an observer to the observable: taskObservable.subscribe(new Observer<Task>())

Disposables keeps track of all the observers used, and cleans up when they are no longer in use