package pet.clever.volleyfragment;

import android.app.*;
import android.content.*;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.base.MoreObjects;
import com.google.common.base.Throwables;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

class EventBusSingleton {
    public static final EventBus eventBus = new EventBus();
}

class OkEvent {

}

class RetryEvent {

}

class CancelEvent {

}

// success
class HelloWorldResponse {
    public final String result;
    public HelloWorldResponse(String result) {
        this.result = result;
    }
}

// failure
class HelloWorldException {
    public final Exception e;
    public HelloWorldException(Exception e) {
        this.e = e;
    }
}

class MyProgressDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Working..");
        progressDialog.setIndeterminate(true);
        return progressDialog;
    }
    public static void open(AppCompatActivity activity) {
        MyProgressDialogFragment f = MyProgressDialogFragment.class.cast(activity.getSupportFragmentManager().findFragmentByTag(MyProgressDialogFragment.class.getName()));
        if (f==null)
            new MyProgressDialogFragment().show(activity.getSupportFragmentManager(), MyProgressDialogFragment.class.getName());

    }
    public static void close(AppCompatActivity activity) {
        MyProgressDialogFragment f = MyProgressDialogFragment.class.cast(activity.getSupportFragmentManager().findFragmentByTag(MyProgressDialogFragment.class.getName()));
        if (f!=null)
            f.dismiss();
    }
}

class MyAlertDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String title = MoreObjects.firstNonNull(args.getString("title"), "title");
        String message = MoreObjects.firstNonNull(args.getString("message"), "message");
        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EventBusSingleton.eventBus.post(new OkEvent());
                    }
                })
                .create();
    }
    public static void open(AppCompatActivity activity, String title, String message) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);

        DialogFragment df = new MyAlertDialogFragment();
        df.setArguments(args);
        df.show(activity.getSupportFragmentManager(), MyAlertDialogFragment.class.getName());
    }
}

class MyRetryCancelDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String title = MoreObjects.firstNonNull(args.getString("title"), "title");
        String message = MoreObjects.firstNonNull(args.getString("message"), "message");
        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("retry", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EventBusSingleton.eventBus.post(new RetryEvent());
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EventBusSingleton.eventBus.post(new CancelEvent());
                    }
                })
                .create();
    }
    public static void open(AppCompatActivity activity, String title, String message) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);

        DialogFragment df = new MyRetryCancelDialogFragment();
        df.setArguments(args);
        df.show(activity.getSupportFragmentManager(), MyRetryCancelDialogFragment.class.getName());
    }
}

//09-02 11:00:14.209 24983-24983/pet.clever.volleyfragment D/pet.clever.volleyfragment.RefreshFragment: onPause
//        09-02 11:00:14.209 24983-24983/pet.clever.volleyfragment D/Main: onPause
//        09-02 11:00:14.209 24983-24983/pet.clever.volleyfragment D/pet.clever.volleyfragment.RefreshFragment: onStop
//        09-02 11:00:14.209 24983-24983/pet.clever.volleyfragment D/Main: onStop
//        09-02 11:00:14.210 24983-24983/pet.clever.volleyfragment D/pet.clever.volleyfragment.RefreshFragment: onDetach
//        09-02 11:00:14.210 24983-24983/pet.clever.volleyfragment D/Main: onDestroy
//        09-02 11:00:14.242 24983-24983/pet.clever.volleyfragment D/pet.clever.volleyfragment.RefreshFragment: onAttach
//        09-02 11:00:14.243 24983-24983/pet.clever.volleyfragment W/FragmentManager: moveToState: Fragment state for RefreshFragment{9a16666 #0 pet.clever.volleyfragment.RefreshFragment} not updated inline; expected state 1 found 0
//        09-02 11:00:14.272 24983-24983/pet.clever.volleyfragment D/Main: onCreate
//        09-02 11:00:14.273 24983-24983/pet.clever.volleyfragment D/pet.clever.volleyfragment.RefreshFragment: onStart
//        09-02 11:00:14.273 24983-24983/pet.clever.volleyfragment D/Main: onStart
//        09-02 11:00:14.277 24983-24983/pet.clever.volleyfragment D/Main: onResume
//        09-02 11:00:14.277 24983-24983/pet.clever.volleyfragment D/pet.clever.volleyfragment.RefreshFragment: onResume

///**
// * onAttach -> onStart -> onResume ||||| onPause -> onStop -> onDetach
// */
//class RefreshFragment extends Fragment {
//
//    static String TAG = RefreshFragment.class.getName();
//
//    private boolean refreshing;
//
//    public void refresh() {
//        String url = "http://httpbin.org/delay/10";
////        if (new Random().nextInt(10)<5)
////            url += "/asdf"; // cause 404
//
//        // Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        EventBusSingleton.eventBus.post(new HelloWorldResponse(response));
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        EventBusSingleton.eventBus.post(new HelloWorldException(error));
//                    }
//                });
//
//        RetryPolicy retryPolicy = new DefaultRetryPolicy(
//                25000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        stringRequest.setRetryPolicy(retryPolicy);
//        queue.add(stringRequest);
//    }
//
//    // called 1st
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        Log.d(TAG, "onAttach");
//
//        if (queue==null)
//            queue = Volley.newRequestQueue(context.getApplicationContext());
//    }
//
//    // called once
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Log.d(TAG, "onCreate");
//        setRetainInstance(true);
//
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        Log.d(TAG, "onStart");
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        Log.d(TAG, "onResume");
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        Log.d(TAG, "onPause");
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        Log.d(TAG, "onStop");
//    }
//
//    // called once
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Log.d(TAG, "onDestroy");
//        queue.cancelAll(getActivity().getApplicationContext());
//    }
//
//    // called last
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        Log.d(TAG, "onDetach");
//    }
//}

public class MainActivity extends AppCompatActivity {

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Main", "onCreate");

//        refreshFragment = RefreshFragment.class.cast(getSupportFragmentManager().findFragmentByTag(RefreshFragment.TAG));
//        if (refreshFragment==null)
//            getSupportFragmentManager().beginTransaction().add(refreshFragment=new RefreshFragment(), RefreshFragment.TAG).commit();

    }

    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
        Log.d("Main", "onStart");
    }

    // onRestoreInstanceState?!?

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Main", "onResume");
        queue = Volley.newRequestQueue(this);
        EventBusSingleton.eventBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Main", "onPause");
        MyProgressDialogFragment.close(this);
        EventBusSingleton.eventBus.unregister(this);
//        queue.cancelAll(this);
        if (stringRequest!=null)
            stringRequest.cancel();
    }

    // onSaveInstanceState

    @Override
    protected void onStop() {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
        Log.d("Main", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Main", "onDestroy");
    }

    public void onClick(View view) {
        Log.d("Main", "onClick");
        helloWorldRequest();
    }

    public void onClickOtherActivity(View view) {
        Log.d("Main", "onClickOtherActivity");
        startActivity(new Intent(this, OtherActivity.class));
    }

    private StringRequest stringRequest;

    private void helloWorldRequest() {
        MyProgressDialogFragment.open(this);

        String url = "http://httpbin.org/drip?duration=5&numbytes=5&code=400";
//        if (new Random().nextInt(10)<5)
//            url += "/asdf"; // cause 404

        // Request a string response from the provided URL.
        stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        EventBusSingleton.eventBus.post(new HelloWorldResponse(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        EventBusSingleton.eventBus.post(new HelloWorldException(error));
                    }
                });

        RetryPolicy retryPolicy = new DefaultRetryPolicy(
                25000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        queue.add(stringRequest);

//        new AsyncTask<Object, Void, Object>() {
//            @Override
//            protected Object doInBackground(Object... params) {
//                try {
//                    Thread.sleep(1000);
//                    if (new Random().nextInt(10)<10)
//                        throw new Exception("Oof!!");
//
//                    return new HelloWorldResponse("Hello, world!!");
//
//                } catch (Exception e) {
//                    return new HelloWorldException(e);
//                }
//            }
//
//            @Override
//            protected void onPostExecute(Object result) {
//
//                EventBusSingleton.eventBus.post(result);
////                progressDialog.dismiss();
//
////                ((MyRetryCancelDialogFragment)getSupportFragmentManager().findFragmentByTag("tag")).dismiss(); //## DOES NOT WORK.. NPE
//                //##  java.lang.NullPointerException: Attempt to invoke virtual method 'void pet.clever.volleyfragment.MyRetryCancelDialogFragment.dismiss()' on a null object reference
//
//
//            }
//        }.execute();

    }

    @Subscribe
    private void handleHelloWorldResponse(HelloWorldResponse response) throws Exception {

        Log.d("tag", ""+response);

        MyProgressDialogFragment.close(this);

        MyAlertDialogFragment.open(this, "success", response.result);
    }

    @Subscribe
    private void handleHelloWorldException(HelloWorldException e) {

        Log.e("tag", "handleException:", e.e);

        MyProgressDialogFragment.close(this);

        // retry-cancel
        MyRetryCancelDialogFragment.open(this, e.e.getMessage(),Throwables.getStackTraceAsString(e.e)+Throwables.getStackTraceAsString(e.e));
    }

    @Subscribe
    private void handleRetry(RetryEvent retryRequest) {
//        Toast.makeText(this, "retrying!!", Toast.LENGTH_LONG).show();
        helloWorldRequest();
    }
    @Subscribe
    private void handleCancel(CancelEvent cancelRequest) {
//        Toast.makeText(this, "canceled!!", Toast.LENGTH_LONG).show();
    }
}
