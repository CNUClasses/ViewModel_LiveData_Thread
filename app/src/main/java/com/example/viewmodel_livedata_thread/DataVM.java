package com.example.viewmodel_livedata_thread;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DataVM extends ViewModel {
    MyThread mythread;
    static int NUMBER_TICKS=100;
//   Integer datavm_cntr=0; //use LiveData instead

    public DataVM() {
        super();

        //initialize as appropriate
        cntr = new MutableLiveData<Integer>();
        cntr.setValue(0);    //initialize

        //a tough one is the thread running or not
        isThreadRunning = new MutableLiveData<Boolean>();
        isThreadRunning.setValue(false);
    }

    //lets add some livedata
    private MutableLiveData<Integer> cntr ;
    public MutableLiveData<Integer> getCurrentProgress() {
        return cntr;
    }

    private MutableLiveData<Boolean> isThreadRunning;
    public MutableLiveData<Boolean> getThreadState() {
        return isThreadRunning;
    }

    public class MyThread extends Thread {
        public boolean stopRequested=false;

        public void run() {

            for (int i=0;i<NUMBER_TICKS;i++){
                if(stopRequested)
                    break;

                //some live data here
                //can only postValue from background thread not setValue
                cntr.postValue(i);
                //sleep for 1/2 sec
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

             //if we are done then say so
             isThreadRunning.postValue(false);
        }
    }
    public void start_thread(){
        //only start 1 thread at a time
        if(mythread == null || !mythread.isAlive()){
            mythread = new MyThread();  //if there is an old thread it is GCed
            mythread.start();
            isThreadRunning.setValue(true);
        }
    }

    public void stop_thread(){
        if(mythread == null)
            return;

        //ask for it to stop
        mythread.stopRequested=true;

        try {
            //wait until its done
            mythread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mythread=null;  //GC this thread
    }

}
