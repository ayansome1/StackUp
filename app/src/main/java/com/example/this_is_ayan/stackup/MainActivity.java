package com.example.this_is_ayan.stackup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.this_is_ayan.stackup.adapters.CustomListAdapter;
import com.example.this_is_ayan.stackup.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity
{
    private static final String TAG = MainActivity.class.getSimpleName();
    private static String url = "https://api.stackexchange.com/2.2/search/advanced?order=desc&sort=activity&accepted=False&answers=0&site=stackoverflow";
    private ProgressDialog pDialog;
    private List<Question> questionList = new ArrayList<Question>();
    private ListView listView;
    private CustomListAdapter adapter;
    private int quotaMax,quotaRemaining;
    TextView apiQuota,creation,votes,likeHistory;
    String searchText;
    int qid;
    String qqid;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        searchText=null;
        setContentView(R.layout.activity_main);

        likeHistory=(TextView)findViewById(R.id.like_history);

        likeHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,likeActivity.class);
                startActivity(intent);
            }
        });


        listView = (ListView) findViewById(R.id.list_view);
        adapter = new CustomListAdapter(this, questionList);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();
        apiQuota=(TextView)findViewById(R.id.apiquota);





        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject responseObj)
                    {

                        Log.d(TAG, responseObj.toString());
                        JSONArray response;


                        try
                        {
                            response=responseObj.getJSONArray("items");

                            System.out.println("****"+response.toString());
                            // Parsing json
                            for (int i = 0; i < response.length(); i++)
                            {
                                try
                                {

                                    JSONObject obj = response.getJSONObject(i);
                                    Question question = new Question();

                                    question.setTitle(obj.getString("title"));
                                    question.setLink(obj.getString("link"));


                                    qid=obj.getInt("question_id");
                                    String qqid=Integer.toString(qid);
                                    question.setQuestionID(qqid);

                                    question.setImage(obj.getJSONObject("owner").getString("profile_image"));


                                    List<String> t=new ArrayList<String>();
                                    int j=0;
                                    JSONArray tagArray=obj.getJSONArray("tags");
                                    for(j=0;j<tagArray.length();j++)
                                    {
                                        t.add(tagArray.getString(j));
                                    }
                                    question.setTags(t);



                                    question.setUser(obj.getJSONObject("owner").getString("display_name"));

                                   // question.setLink(obj.getJSONObject("owner").getString("link"));

                                    question.setScore(obj.getInt("score"));

                                    question.setLastUpdate(obj.getLong("last_activity_date"));
                                    questionList.add(question);
                                }
                                catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }

                            }

                            // notifying list adapter about data changes
                            // so that it renders the list view with updated data
                            quotaMax=responseObj.getInt("quota_max");
                            quotaRemaining=responseObj.getInt("quota_remaining");
                            System.out.println("*****" + quotaRemaining + " " + quotaMax);
                            float quota;

                            quota=(float)quotaRemaining/(float)quotaMax;
                            apiQuota.setText("API Quota: " + (int)(quota*100) + "%");
                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                        hidePDialog();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                error.printStackTrace();
                // hide the progress dialog
                hidePDialog();
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);


        final TextView atv=(TextView)findViewById(R.id.tags);
        atv.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (atv.getText().length() > 0) {
                    atv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_search, 0, R.drawable.ic_navigation_cancel, 0);

                    atv.setOnTouchListener(new View.OnTouchListener() {

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            atv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_search, 0, R.drawable.ic_navigation_cancel, 0);
                            final int DRAWABLE_RIGHT = 2;
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                if (event.getRawX() >= (atv.getRight() - atv.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                                    atv.setText("");
                                    return true;
                                }
                            }
                            return false;
                        }
                    });

                } else {
                    atv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_search, 0, 0, 0);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        Button bt=(Button)findViewById(R.id.search);
        bt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

                questionList.clear();
                searchText=atv.getText().toString();
                url="https://api.stackexchange.com/2.2/search/advanced?order=desc&sort=activity&tagged="+searchText+"&accepted=False&answers=0&site=stackoverflow";
                pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setMessage("Loading...");
                pDialog.show();


                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                        url, null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject responseObj)
                            {

                                Log.d(TAG, responseObj.toString());
                                JSONArray response;


                                try
                                {
                                    response=responseObj.getJSONArray("items");
                                    quotaMax=responseObj.getInt("quota_max");
                                    quotaRemaining=responseObj.getInt("quota_remaining");
                                    apiQuota.setText("API Quota: "+(quotaRemaining/quotaMax)*100+"%");
                                    System.out.println("****"+response.toString());
                                    // Parsing json
                                    for (int i = 0; i < response.length(); i++)
                                    {
                                        try
                                        {

                                            JSONObject obj = response.getJSONObject(i);
                                            Question question = new Question();
                                            question.setLink(obj.getString("link"));

                                            qid=obj.getInt("question_id");
                                            String qqid=Integer.toString(qid);
                                            question.setQuestionID(qqid);

                                           // question.setQuestionID(obj.getString("question_id"));


                                            question.setTitle(obj.getString("title"));
                                            question.setImage(obj.getJSONObject("owner").getString("profile_image"));


                                            List<String> t=new ArrayList<String>();
                                            int j=0;
                                            JSONArray tagArray=obj.getJSONArray("tags");
                                            for(j=0;j<tagArray.length();j++)
                                            {
                                                t.add(tagArray.getString(j));
                                            }
                                            question.setTags(t);




                                            question.setUser(obj.getJSONObject("owner").getString("display_name"));

                                          //  question.setLink(obj.getJSONObject("owner").getString("link"));

                                            question.setScore(obj.getInt("score"));

                                            question.setLastUpdate(obj.getLong("last_activity_date"));
                                            questionList.add(question);
                                        }
                                        catch (JSONException e)
                                        {
                                            e.printStackTrace();
                                        }

                                    }

                                    // notifying list adapter about data changes
                                    // so that it renders the list view with updated data
                                    quotaMax=responseObj.getInt("quota_max");
                                    quotaRemaining=responseObj.getInt("quota_remaining");
                                    System.out.println("*****"+quotaRemaining+" "+quotaMax);
                                    float quota;
                                    quota=(float)quotaRemaining/(float)quotaMax;
                                    apiQuota.setText("API Quota: " + (int)(quota*100) + "%");
                                    adapter.notifyDataSetChanged();
                                }
                                catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }

                                hidePDialog();

                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        error.printStackTrace();
                        // hide the progress dialog
                        hidePDialog();
                    }
                });

                AppController.getInstance().addToRequestQueue(jsonObjReq);



            }
        });



        creation=(TextView)findViewById(R.id.creation);

        creation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

                questionList.clear();

                //String searchText=atv.getText().toString();

                if(searchText!=null)
                    url="https://api.stackexchange.com/2.2/search/advanced?order=desc&sort=creation&tagged="+searchText+"&accepted=False&answers=0&site=stackoverflow";
                else
                    url="https://api.stackexchange.com/2.2/search/advanced?order=desc&sort=creation&accepted=False&answers=0&site=stackoverflow";

                pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setMessage("Loading...");
                pDialog.show();


                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                        url, null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject responseObj)
                            {

                                Log.d(TAG, responseObj.toString());
                                JSONArray response;


                                try
                                {
                                    response=responseObj.getJSONArray("items");
                                    quotaMax=responseObj.getInt("quota_max");
                                    quotaRemaining=responseObj.getInt("quota_remaining");
                                    apiQuota.setText("API Quota: "+(quotaRemaining/quotaMax)*100+"%");
                                    System.out.println("****"+response.toString());
                                    // Parsing json
                                    for (int i = 0; i < response.length(); i++)
                                    {
                                        try
                                        {

                                            JSONObject obj = response.getJSONObject(i);
                                            Question question = new Question();
                                            question.setLink(obj.getString("link"));

                                            qid=obj.getInt("question_id");
                                            String qqid=Integer.toString(qid);
                                            question.setQuestionID(qqid);
                                           // question.setQuestionID(obj.getString("question_id"));


                                            question.setTitle(obj.getString("title"));
                                            question.setImage(obj.getJSONObject("owner").getString("profile_image"));


                                            List<String> t=new ArrayList<String>();
                                            int j=0;
                                            JSONArray tagArray=obj.getJSONArray("tags");
                                            for(j=0;j<tagArray.length();j++)
                                            {
                                                t.add(tagArray.getString(j));
                                            }
                                            question.setTags(t);




                                            question.setUser(obj.getJSONObject("owner").getString("display_name"));

                                            //  question.setLink(obj.getJSONObject("owner").getString("link"));

                                            question.setScore(obj.getInt("score"));

                                            question.setLastUpdate(obj.getLong("last_activity_date"));
                                            questionList.add(question);
                                        }
                                        catch (JSONException e)
                                        {
                                            e.printStackTrace();
                                        }

                                    }

                                    // notifying list adapter about data changes
                                    // so that it renders the list view with updated data
                                    quotaMax=responseObj.getInt("quota_max");
                                    quotaRemaining=responseObj.getInt("quota_remaining");
                                    System.out.println("*****"+quotaRemaining+" "+quotaMax);
                                    float quota;
                                    quota=(float)quotaRemaining/(float)quotaMax;
                                    apiQuota.setText("API Quota: " + (int)(quota*100) + "%");
                                    adapter.notifyDataSetChanged();
                                }
                                catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }

                                hidePDialog();

                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        error.printStackTrace();
                        // hide the progress dialog
                        hidePDialog();
                    }
                });

                AppController.getInstance().addToRequestQueue(jsonObjReq);



            }
        });


        votes=(TextView)findViewById(R.id.votes);

        votes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

                questionList.clear();
              //  String searchText=atv.getText().toString();
                if(searchText!=null)
                    url="https://api.stackexchange.com/2.2/search/advanced?order=desc&sort=votes&tagged="+searchText+"&accepted=False&answers=0&site=stackoverflow";
                else
                    url="https://api.stackexchange.com/2.2/search/advanced?order=desc&sort=votes&accepted=False&answers=0&site=stackoverflow";


              //  url="https://api.stackexchange.com/2.2/search/advanced?order=desc&sort=votes&tagged="+searchText+"&accepted=False&answers=0&site=stackoverflow";
                pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setMessage("Loading...");
                pDialog.show();


                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                        url, null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject responseObj)
                            {

                                Log.d(TAG, responseObj.toString());
                                JSONArray response;


                                try
                                {
                                    response=responseObj.getJSONArray("items");
                                    quotaMax=responseObj.getInt("quota_max");
                                    quotaRemaining=responseObj.getInt("quota_remaining");
                                    apiQuota.setText("API Quota: "+(quotaRemaining/quotaMax)*100+"%");
                                    System.out.println("****"+response.toString());
                                    // Parsing json
                                    for (int i = 0; i < response.length(); i++)
                                    {
                                        try
                                        {

                                            JSONObject obj = response.getJSONObject(i);
                                            Question question = new Question();
                                            question.setLink(obj.getString("link"));

                                            qid=obj.getInt("question_id");
                                            String qqid=Integer.toString(qid);
                                            question.setQuestionID(qqid);
                                           // question.setQuestionID(obj.getString("question_id"));


                                            question.setTitle(obj.getString("title"));
                                            question.setImage(obj.getJSONObject("owner").getString("profile_image"));


                                            List<String> t=new ArrayList<String>();
                                            int j=0;
                                            JSONArray tagArray=obj.getJSONArray("tags");
                                            for(j=0;j<tagArray.length();j++)
                                            {
                                                t.add(tagArray.getString(j));
                                            }
                                            question.setTags(t);




                                            question.setUser(obj.getJSONObject("owner").getString("display_name"));

                                            //  question.setLink(obj.getJSONObject("owner").getString("link"));

                                            question.setScore(obj.getInt("score"));

                                            question.setLastUpdate(obj.getLong("last_activity_date"));
                                            questionList.add(question);
                                        }
                                        catch (JSONException e)
                                        {
                                            e.printStackTrace();
                                        }

                                    }

                                    // notifying list adapter about data changes
                                    // so that it renders the list view with updated data
                                    quotaMax=responseObj.getInt("quota_max");
                                    quotaRemaining=responseObj.getInt("quota_remaining");
                                    System.out.println("*****"+quotaRemaining+" "+quotaMax);
                                    float quota;
                                    quota=(float)quotaRemaining/(float)quotaMax;
                                    apiQuota.setText("API Quota: " + (int)(quota*100) + "%");
                                    adapter.notifyDataSetChanged();
                                }
                                catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }

                                hidePDialog();

                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        error.printStackTrace();
                        // hide the progress dialog
                        hidePDialog();
                    }
                });

                AppController.getInstance().addToRequestQueue(jsonObjReq);



            }
        });










        // Creating volley request obj
       /* JsonObjectRequest questionReq = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject responseObject)
                    {
                        Log.d(TAG, responseObject.toString());
                        hidePDialog();
                        JSONArray response;


                        try
                        {
                             response=responseObject.getJSONArray("items");



                        }
                        catch (JSONException e)
                        {

                        }





                        for (int i = 0; i < response.length(); i++)
                        {
                            try
                            {

                                JSONObject obj = response.getJSONObject(i);
                                Question question = new Question();

                                question.setTitle(obj.getString("title"));
                                question.setImage(obj.getJSONObject("owner").getString("profile_image"));


                                List<String> t=new ArrayList<String>();
                                int j=0;
                                JSONArray tagArray=obj.getJSONArray("tags");
                                for(j=0;j<tagArray.length();j++)
                                {
                                    t.add(tagArray.getString(j));
                                }
                                question.setTags(t);

                                question.setLink(obj.getJSONObject("owner").getString("link"));

                                question.setScore(obj.getInt("score"));

                                question.setLastUpdate(obj.getJSONObject("owner").getLong("last_activity_date"));
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            }

                        }


                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();

            }
        });*/

        // Adding request to request queue
     //   AppController.getInstance().addToRequestQueue(questionReq);






    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }


}
