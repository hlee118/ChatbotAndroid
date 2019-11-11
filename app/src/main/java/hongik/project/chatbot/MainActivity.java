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

    private JSONObject response_object;
    private Thread mThread = null;
    private EditText edit_query;
    private Button btn_confirm;
    private ListView mListView;
    private ListViewAdapter adapter;

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
                setQuery(query);
            }
        });

        mListView = findViewById(R.id.mListView);
        mListView.setAdapter(adapter);
    }

    public void setQuery(final String query){
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                getAnswer(query);
            }
        });
        mThread.start();
    }

    public void getAnswer(String query){
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody req_body = new FormBody.Builder()
                    .add("query", query)
                    .build();

            Request request = new Request.Builder()
                    .url(URL.SERVER)
                    .post(req_body)
                    .build();

            Response response = client.newCall(request).execute();
            String response_string = response.body().string();

            response_object = new JSONObject(response_string);
            handler.sendEmptyMessage(response_object.getInt("result"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case 0:
                    try {
                        JSONObject data = response_object.getJSONObject("data");
                        JSONObject dobby = data.getJSONObject("dobby");
                        JSONObject wiki = data.getJSONObject("wiki");
//                        dobby.getDouble("accuracy");
//                        wiki.getDouble("accuracy");

                        adapter.addItem(dobby.getString("answer"), "Dobby");
                        adapter.addItem(wiki.getString("answer"), "Wiki");
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
}
