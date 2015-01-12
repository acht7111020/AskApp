package netdb.course.softwarestudio.askapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.StatementEvent;

import netdb.course.softwarestudio.askapp.R;
import netdb.course.softwarestudio.askapp.model.Definition;
import netdb.course.softwarestudio.askapp.service.rest.RestManager;

public class CommentActivity extends ActionBarActivity {
    private ListView listView;
    private RestManager mRestMgr;
    private Button mPostBtn;
    private EditText mWrite;
    private Definition def;
    private ArrayAdapter<String> listAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        mRestMgr = RestManager.getInstance(getApplication());
        listView = (ListView)findViewById(R.id.listViewall);
        mPostBtn = (Button)findViewById(R.id.postcomment);
        mWrite = (EditText)findViewById(R.id.comment);
        mPostBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                String com = mWrite.getText().toString();
                postComment(com);
            }
        });

    }

    private void postComment(String comment) {

        def = new Definition();
        def.setComment(comment);
        mRestMgr.postResource(Definition.class, def, new RestManager.PostResourceListener<Definition>() {
            @Override
            public void onResponse(int code, Map<String, String> headers) {

                setResult(RESULT_OK);


                finish();
            }

            @Override
            public void onRedirect(int code, Map<String, String> headers, String url) {
                onError(null, null, code, headers);
            }

            @Override
            public void onError(String message, Throwable cause, int code, Map<String, String> headers) {
                Log.d(this.getClass().getSimpleName(), "" + code + ": " + message);

                setResult(RESULT_CANCELED);
                finish();
            }
        }, null);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
