package netdb.course.softwarestudio.askapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import netdb.course.softwarestudio.askapp.model.Definition;
import netdb.course.softwarestudio.askapp.service.rest.RestManager;
import netdb.course.softwarestudio.askapp.service.rest.model.Resource;


public class MainActivity extends ActionBarActivity {
    //http://fluent-crossbar-822.appspot.com/
    //http://enhanced-rite-819.appspot.com/
    private RestManager restMgr;
    private EditText mWrite;
    private EditText keywordEdt;
    private Button searchBtn;
    private Button sendBtn;
    private TextView titleTxt;
    private TextView descriptionTxt;
    private ProgressBar progressBar;
    private ListView listView;
    private List<String> empty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        restMgr = RestManager.getInstance(this);

        mWrite = (EditText)findViewById(R.id.WriteComment);
        keywordEdt = (EditText) findViewById(R.id.edt_keyword);
        searchBtn = (Button) findViewById(R.id.btn_search);
        sendBtn = (Button) findViewById(R.id.sendComment);
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
                    sendBtn.setVisibility(View.INVISIBLE);
                    sendBtn.setEnabled(false);
                    mWrite.setVisibility(View.INVISIBLE);

                    empty = new ArrayList<String>();
                    ArrayAdapter  listAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, empty);
                    listView.setAdapter(listAdapter);
                    searchKeyword(keyword);

                } else {
                    // clear results

                    sendBtn.setVisibility(View.INVISIBLE);
                    sendBtn.setEnabled(false);
                    mWrite.setVisibility(View.INVISIBLE);

                    titleTxt.setText("");
                    descriptionTxt.setText("");
                }
            }
        });


    }

    private void sendComment(String comment,int action,Definition resource){

        if(comment!=null) {
            if (action == 200) {
                if(resource.getComment()==null){
                    resource.setComment(new ArrayList<String>());
                }
                resource.addComment(comment);
                //Toast.makeText(MainActivity.this, "in the action 200",Toast.LENGTH_SHORT).show();
            } else if (action == 404) {
                resource.setDescription(comment);
            }
            //else
                //.makeText(MainActivity.this, getString(R.string.WTF),Toast.LENGTH_SHORT).show();
        }
       // else
            //Toast.makeText(this, "comment is null",Toast.LENGTH_SHORT).show();

        // Set header
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");

        restMgr.postResource(Definition.class, resource, new RestManager.PostResourceListener<Definition>() {
            @Override
            public void onResponse(int code, Map<String, String> headers) {
                progressBar.setVisibility(View.INVISIBLE);
              //  if (code == 201)
                    Toast.makeText(MainActivity.this, getString(R.string.success),Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onRedirect(int code, Map<String, String> headers, String url) {

            }

            @Override
            public void onError(String message, Throwable cause, int code, Map<String, String> headers) {
                progressBar.setVisibility(View.INVISIBLE);
                Log.d(this.getClass().getSimpleName(), "" + code + ": " + message);
                Toast.makeText(MainActivity.this, getString(R.string.failure),Toast.LENGTH_SHORT).show();

            }
        }, null);
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
            public void onResponse(int code, Map<String, String> headers, final Definition resource) {

                if(resource.getComment()!=null) {
                    ArrayAdapter listAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, resource.getComment());
                    listView.setAdapter(listAdapter);
                }
                else{
                    String[] s = {"EMPTY","OHOHOH"};
                    ArrayAdapter listAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, s);
                    listView.setAdapter(listAdapter);
                }

                titleTxt.setText(resource.getTitle());
                descriptionTxt.setText(resource.getDescription());
                progressBar.setVisibility(View.INVISIBLE);
                descriptionTxt.setOnClickListener(new TextView.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendBtn.setVisibility(View.VISIBLE);
                        sendBtn.setEnabled(true);
                        mWrite.setVisibility(View.VISIBLE);
                        mWrite.setEnabled(true);
                    }
                });

                sendBtn.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        String comment = mWrite.getText().toString();
                        progressBar.setVisibility(View.VISIBLE);
                        sendComment(comment,200,resource);

                    }
                });


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
                titleTxt.setText(keywordEdt.getText().toString());
                descriptionTxt.setText("nothing to show ~~~~ QQ");
                descriptionTxt.setOnClickListener(new TextView.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendBtn.setVisibility(View.VISIBLE);
                        sendBtn.setEnabled(true);
                        mWrite.setVisibility(View.VISIBLE);
                        mWrite.setEnabled(true);
                    }
                });

                sendBtn.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        String title =   keywordEdt.getText().toString();
                        Definition resource = new Definition(title,null,null);
                        String comment = mWrite.getText().toString();
                        progressBar.setVisibility(View.VISIBLE);
                        sendComment(comment,404,resource);
                    }
                });
            }
        }, null);
    }
}
