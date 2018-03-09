package kr.photox.android.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import kr.photox.android.R;

public class PlaceActivity extends FragmentActivity {

    /*

     */
    public int place_id = -1;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_activity);

        /*

         */
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceFragment())
                    .commit();
        }

        /*

         */
        Bundle bundle = getIntent().getExtras();
        place_id = bundle.getInt("id");
        String title = bundle.getString("title");

        /*

         */
        TextView mTitleTv = (TextView) findViewById(R.id.title_tv);
        /*

         */
        mTitleTv.setText(title);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * @param id
     * @param title
     */
    public void startCheckinActivity(final int id, final String title) {

    }

    /**
     * @param id
     * @param title
     */
    public void startMissionActivity(final int id, final String title) {
        Intent intent = new Intent(PlaceActivity.this, MissionActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("title", title);
        startActivity(intent);
    }
}
