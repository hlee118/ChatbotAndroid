package hongik.project.chatbot;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import hongik.project.chatbot.utility.URL;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText edit_query;
    private Button btn_confirm;
    private ListView mListView;
    private ListViewAdapter adapter;
    private Thread mThread;

    private final int DOBBY = 0;
    private final int WIKI = 1;
    private final int SEQ2SEQ = 2;
    private final String[] NAMES = {"Dobby", "Wiki", "Seq2Seq"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit_query = findViewById(R.id.edit_query);
        btn_confirm = findViewById(R.id.btn_confirm);
        adapter = new ListViewAdapter();
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = edit_query.getText().toString();
                edit_query.setText("");
                adapter.addItem(query, "me");
                adapter.notifyDataSetChanged();
                getAnswers(query);
            }
        });

        mListView = findViewById(R.id.mListView);
        mListView.setAdapter(adapter);
    }

    public void getAnswers(final String query){
        // dobby
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                requestAnswer(query, URL.DOBBY, DOBBY);
            }
        });
        mThread.start();

        // wiki
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                requestAnswer(query, URL.WIKI, WIKI);
            }
        });
        mThread.start();

        // seq2seq
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                requestAnswer(query, URL.SEQ2SEQ, SEQ2SEQ);
            }
        });
        mThread.start();
    }

    public void requestAnswer(String query, String url, int type){
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody req_body = new FormBody.Builder()
                    .add("query", query)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(req_body)
                    .build();

            Response response = client.newCall(request).execute();
            String response_string = response.body().string();

            JSONObject response_object = new JSONObject(response_string);
            handler.setObj(response_object);
            handler.sendEmptyMessage(type);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static class CustomHandler extends Handler {
        private JSONObject obj = null;
        void setObj(JSONObject obj){
            this.obj = obj;
        }

        JSONObject getObj(){
            return this.obj;
        }
    }

    @SuppressLint("HandlerLeak")
    CustomHandler handler = new CustomHandler() {
        @Override
        public void handleMessage(final Message msg) {
            try {
                adapter.addItem(this.getObj().getString("answer"), NAMES[msg.what]);
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


}
