package netdb.course.softwarestudio.askapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import netdb.course.softwarestudio.askapp.model.Definition;
import netdb.course.softwarestudio.askapp.service.rest.RestManager;


public class MainActivity extends ActionBarActivity {

    private RestManager restMgr;

    private EditText keywordEdt;
    private Button searchBtn;
    private Button morecommentBtn;
    private TextView titleTxt;
    private TextView descriptionTxt;
    private ProgressBar progressBar;
    private ListView listView;
    private ArrayAdapter<String> listAdapter;
    private Definition def;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        restMgr = RestManager.getInstance(this);

        keywordEdt = (EditText) findViewById(R.id.edt_keyword);
        searchBtn = (Button) findViewById(R.id.btn_search);
        morecommentBtn = (Button) findViewById(R.id.moreComment);
        titleTxt = (TextView) findViewById(R.id.txt_title);
        descriptionTxt = (TextView) findViewById(R.id.txt_description);
        progressBar = (ProgressBar) findViewById(R.id.pgsb_loading);
        listView = (ListView)findViewById(R.id.commentView);
        searchBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = keywordEdt.getText().toString().replaceAll("\\s", "");
                if (keyword.length() != 0) {
                    // send a search request
                    progressBar.setVisibility(View.VISIBLE);
                    searchKeyword(keyword);

                } else {
                    // clear results
                    titleTxt.setText("");
                    descriptionTxt.setText("");
                }
            }
        });

        if(def!=null){
            listAdapter = new ArrayAdapter(this ,android.R.layout.simple_list_item_1,def.getComment());
            listView.setAdapter(listAdapter);

        }

        morecommentBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                goComment();
            }
        });

    }
    private void goComment(){
        Intent intent = new Intent(this ,CommentActivity.class);
        //startActivity(intent);
        startActivity(intent);
    }

    private void searchKeyword(String keyword) {

        // Set header
        Map<String, String> header = new HashMap<>();
        header.put("Accept", "application/json");

        try {
            keyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8.name());
            Log.d("TAG", keyword);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        restMgr.getResource(Definition.class, keyword, null, header, new RestManager.GetResourceListener<Definition>() {
            @Override
            public void onResponse(int code, Map<String, String> headers, Definition resource) {
                def = resource;
                progressBar.setVisibility(View.INVISIBLE);

                titleTxt.setText(resource.getTitle());
                descriptionTxt.setText(resource.getDescription());


            }

            @Override
            public void onRedirect(int code, Map<String, String> headers, String url) {

            }

            @Override
            public void onError(String message, Throwable cause, int code, Map<String, String> headers) {

                progressBar.setVisibility(View.INVISIBLE);

                if (code == 404)
                    Toast.makeText(MainActivity.this, getString(R.string.info_not_found),
                            Toast.LENGTH_SHORT).show();
            }
        }, null);
    }
}
