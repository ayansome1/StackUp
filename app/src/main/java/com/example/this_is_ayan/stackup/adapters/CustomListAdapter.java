package com.example.this_is_ayan.stackup.adapters;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.this_is_ayan.stackup.DBHandler;
import com.example.this_is_ayan.stackup.Question;
import com.example.this_is_ayan.stackup.R;
import com.example.this_is_ayan.stackup.app.AppController;

import java.util.ArrayList;
import java.util.List;

public class CustomListAdapter extends BaseAdapter
{
    private Activity activity;
    private LayoutInflater inflater;
    private List<Question> questionItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    DBHandler handler;

    public CustomListAdapter(Activity activity, List<Question> questionItems) {
        this.activity = activity;
        this.questionItems = questionItems;
        handler=new DBHandler(activity);
    }

   /* @Override
    public int getViewTypeCount() {
        //Count=Size of ArrayList.
        return questionItems.size();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }*/

    @Override
    public int getCount() {
        return questionItems.size();
    }

    @Override
    public Object getItem(int location) {
        return questionItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {

        ViewHolder viewHolder = null;
        if(convertView==null)
        {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item, null);
             viewHolder=new ViewHolder();
            viewHolder.question = (TextView) convertView.findViewById(R.id.question);
            viewHolder.tags = (TextView) convertView.findViewById(R.id.tags);
            viewHolder.user = (TextView) convertView.findViewById(R.id.user);
            viewHolder.vote = (TextView) convertView.findViewById(R.id.vote);
            viewHolder.lastUpdate = (TextView) convertView.findViewById(R.id.last_update);
            viewHolder.link=(ImageView) convertView.findViewById(R.id.link);


            if (imageLoader == null)
                imageLoader = AppController.getInstance().getImageLoader();
            viewHolder.thumbNail = (NetworkImageView) convertView.findViewById(R.id.image);


            viewHolder.like=(ToggleButton) convertView.findViewById(R.id.likeToggle);

           // viewHolder.like.setText(questionItems.get(position).getQuestionID());
           // String  qw=viewHolder.like.getText().toString();

            viewHolder.like.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                    questionItems.get(getPosition).setLiked(buttonView.isChecked()); // Set the value of checkbox to maintain its state.



                    //   questionItems.get(getPosition).setQuestionID("5");
                    if (buttonView.isChecked())
                    {

                        //   question.setQuestionID(obj.getString("question_id"));
                        handler.addQuestion(questionItems.get(getPosition).getQuestionID());
                       System.out.println("%%%%%" + questionItems.get(getPosition).getQuestionID());


                       // questionItems.get(getPosition).getLiked().

                    }
                    else
                    {
                        handler.deleteQuestion(questionItems.get(getPosition).getQuestionID());

                    }
                    ArrayList<String> h=new ArrayList<String>();
                    h=handler.getAllQuestions();
                    int yy=0;
                    System.out.println("all question id are :\n");

                    while(yy<h.size())
                    {
                        System.out.println("qid " + h.get(yy));


                        yy++;
                    }


                }
            });






            convertView.setTag(viewHolder);
            convertView.setTag(R.id.question, viewHolder.question);
            convertView.setTag(R.id.tags, viewHolder.tags);
            convertView.setTag(R.id.user, viewHolder.user);
            convertView.setTag(R.id.likeToggle, viewHolder.like);
            convertView.setTag(R.id.vote, viewHolder.vote);
            convertView.setTag(R.id.last_update, viewHolder.lastUpdate);


        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }





        viewHolder.like.setTag(position);


        viewHolder.like.setChecked(questionItems.get(position).getLiked());

        //setChecked(questionItems.get(position).getLiked());







        final Question m = questionItems.get(position);
        viewHolder.thumbNail.setImageUrl(m.getImage(), imageLoader);
        viewHolder.question.setText(m.getTitle());

        String tag="";
        List<String> tag1;//=new ArrayList<>();
        tag1=m.getTags();
        for(int i=0;i<tag1.size();i++)
            tag=tag+" "+tag1.get(i);
        viewHolder.tags.setText(tag);

        viewHolder.user.setText("by " + m.getUser());

        viewHolder.vote.setText(String.valueOf(m.getScore()));

        viewHolder.lastUpdate.setText(m.getLastUpdate());

       // ImageView link=(ImageView) convertView.findViewById(R.id.link);
        viewHolder.link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(m.getLink()));
                activity.startActivity(browserIntent);
                //  System.out.println("#########link is "+m.getLink());

            }
        });



      /*  like.setBackgroundResource(R.drawable.likeselector);
        like.setText(null);
        like.setTextOn(null);
        like.setTextOff(null);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like.setBackgroundResource(R.drawable.likeselector);

            }
        });*/


        return convertView;
    }


    static class ViewHolder
    {
        protected TextView question,tags,user,vote,lastUpdate;
        protected ToggleButton like;
        NetworkImageView thumbNail;
        ImageView link;
      //  String qid;
    }

}