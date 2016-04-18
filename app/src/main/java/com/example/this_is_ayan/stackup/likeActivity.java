package com.example.this_is_ayan.stackup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.this_is_ayan.stackup.adapters.CustomLikedListAdapter;

import com.example.this_is_ayan.stackup.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class likeActivity extends Activity
{
    DBHandler handler;
    private static String url;// = "https://api.stackexchange.com/2.2/search/advanced?order=desc&sort=activity&accepted=False&answers=0&site=stackoverflow";
    String qID;

    ArrayList<String> q;

    private ProgressDialog pDialog;



    private List<Question> questionList = new ArrayList<Question>();
    private ListView listView;
    private CustomLikedListAdapter adapter;
  //  private int quotaMax,quotaRemaining;
 //   TextView apiQuota,creation,votes,likeHistory;
  //  String searchText;
    int qid;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);
        handler=new DBHandler(getApplicationContext());
        q=new ArrayList<String>();
        q=handler.getAllQuestions();

        int yy=0;
        System.out.println("all question id are :\n");

        listView = (ListView) findViewById(R.id.list_view);
        adapter = new CustomLikedListAdapter(this, questionList);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        while(yy<q.size())
        {
            System.out.println("qid " + q.get(yy));

            qID=q.get(yy);
            url="https://api.stackexchange.com/2.2/questions/"+qID+"?site=stackoverflow";






            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    url, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject responseObj)
                        {

                           // Log.d(TAG, responseObj.toString());
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
                               // quotaMax=responseObj.getInt("quota_max");
                              //  quotaRemaining=responseObj.getInt("quota_remaining");
                               // System.out.println("*****" + quotaRemaining + " " + quotaMax);
                              //  float quota;

                              //  quota=(float)quotaRemaining/(float)quotaMax;
                               // apiQuota.setText("API Quota: " + (int)(quota*100) + "%");
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
                public void onErrorResponse(VolleyError error)
                {
                   // VolleyLog.d(TAG, "Error: " + error.getMessage());
                    error.printStackTrace();
                    // hide the progress dialog
                    hidePDialog();
                }
            });

            AppController.getInstance().addToRequestQueue(jsonObjReq);






            yy++;

        }






    }
    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }


}
