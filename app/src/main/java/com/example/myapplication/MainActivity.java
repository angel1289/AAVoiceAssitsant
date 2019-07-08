package com.example.myapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Build;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Locale;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements
        RecognitionListener {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_RECORD_PERMISSION = 100;
    private TextView returnedText;
    private ToggleButton toggleButton;
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";

    boolean myMessage = true;
    private List<ChatBubble> ChatBubbles;
    private ArrayAdapter<ChatBubble> adapter;
    private TextToSpeech t1;
    private String txvResult = "";
    private String txvResult2;
    private String idd;
    ApiInterface apiInterface;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);
        progressBar.setVisibility(View.INVISIBLE);
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(true);
                    ActivityCompat.requestPermissions
                            (MainActivity.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    REQUEST_RECORD_PERMISSION);
                } else {
                    progressBar.setIndeterminate(false);
                    progressBar.setVisibility(View.INVISIBLE);
                    speech.stopListening();
                }
            }
        });

        Uri uri = getIntent().getData();
        if(uri != null){
            idd = uri.getPath();
            idd = idd.replace("/", "");
        }
        ChatBubbles = new ArrayList<>();
        listView = (ListView) findViewById(R.id.list_msg);
        adapter = new MessageAdapter(this, R.layout.left_chat_bubble, ChatBubbles);
        listView.setAdapter(adapter);
        myMessage = false;

        Incident incident = new Incident(txvResult);
        Call<Incident> incidentCall = apiInterface.getIncident(idd);
        incidentCall.enqueue(new Callback<Incident>() {
            @Override
            public void onResponse(Call<Incident> call, Response<Incident> response) {
                Log.e(TAG, "onResponse: " + response.body());

                txvResult2 = response.body().getResponse();
                System.out.println(txvResult2);
                ChatBubble ChatBubble = new ChatBubble(response.body().getResponse(), myMessage);
                ChatBubbles.add(ChatBubble);
                adapter.notifyDataSetChanged();

                t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            t1.setLanguage(Locale.US);
                            speak();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<Incident> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    speech.startListening(recognizerIntent);
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast
                            .LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {

        if(t1!=null){
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (speech != null) {
            speech.destroy();
            Log.i(LOG_TAG, "destroy");
        }
    }


    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
        progressBar.setIndeterminate(true);
        toggleButton.setChecked(false);
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
        toggleButton.setChecked(false);
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(LOG_TAG, "onPartialResults");
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        System.out.println(matches.get(0));
        //add message to list
        myMessage = true;
        txvResult = matches.get(0);
        ChatBubble ChatBubble = new ChatBubble(txvResult, myMessage);
        ChatBubbles.add(ChatBubble);
        adapter.notifyDataSetChanged();
        txvResult = txvResult.replace(" ", "");

        if (myMessage) {
            myMessage = false;
            Incident incident = new Incident(txvResult);
            System.out.println(txvResult);
            Call<Incident> incidentPostCall = apiInterface.postIncident(incident);
            incidentPostCall.enqueue(new Callback<Incident>() {
                @Override
                public void onResponse(Call<Incident> call, Response<Incident> response) {
                    Log.e(TAG, "onResponse: " + response.body().getResponse() );

                    txvResult2 = response.body().getResponse();
                    System.out.println(txvResult2);
                    ChatBubble ChatBubble = new ChatBubble(txvResult2, myMessage);
                    ChatBubbles.add(ChatBubble);
                    adapter.notifyDataSetChanged();

                    t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if(status!=TextToSpeech.ERROR)
                            {
                                t1.setLanguage(Locale.US);
                                speak();
                            }
                        }
                    });
                }

                @Override
                public void onFailure(Call<Incident> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getLocalizedMessage() );
                }
            });
        } else {
            myMessage = true;
        }
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        progressBar.setProgress((int) rmsdB);
    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }
    public void speak()
    {
        System.out.println(txvResult2);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            t1.speak(txvResult2,TextToSpeech.QUEUE_FLUSH,null,null);
        else
            t1.speak(txvResult2,TextToSpeech.QUEUE_FLUSH,null);
    }
}