package co.joyatwork.droidz;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;


/**
 * The main activity class.
 * <p>
 * The main layout lists the demonstrated features, with buttons to launch them.
 */
public final class MainActivity extends ListActivity {
	
	private static final String TAG = ListActivity.class.getSimpleName(); 

    /**
     * A simple POJO that holds the details about the game demo that are used by the List Adapter.
     */
    private static class GameDetails {
        /**
         * The resource id of the title of the demo.
         */
        private final int titleId;

        /**
         * The resources id of the description of the demo.
         */
        private final int descriptionId;

        /**
         * The game type.
         */
        private final MainGamePanel.Game game;

        public GameDetails(int titleId, int descriptionId,
        		MainGamePanel.Game game) {
            this.titleId = titleId;
            this.descriptionId = descriptionId;
            this.game = game;
        }
    }

    /**
     * A custom array adapter that shows a {@link FeatureView} containing details about the demo.
     */
    private static class CustomArrayAdapter extends ArrayAdapter<GameDetails> {

        /**
         * @param demos An array containing the details of the demos to be displayed.
         */
        public CustomArrayAdapter(Context context, GameDetails[] demos) {
            super(context, R.layout.feature, R.id.title, demos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FeatureView featureView;
            if (convertView instanceof FeatureView) {
                featureView = (FeatureView) convertView;
            } else {
                featureView = new FeatureView(getContext());
            }

            GameDetails demo = getItem(position);

            featureView.setTitleId(demo.titleId);
            featureView.setDescriptionId(demo.descriptionId);

            return featureView;
        }
    }

    private static final GameDetails[] demos = {
    		new GameDetails(R.string.flying_droid, R.string.flying_droid_description, 
    				MainGamePanel.Game.DROID),
	   		new GameDetails(R.string.walking_elaine, R.string.walking_elaine_description, 
    				MainGamePanel.Game.ELAINE),
	   		new GameDetails(R.string.explosion, R.string.explosion_description, 
    				MainGamePanel.Game.EXPLOSION),
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Log.d(TAG, "onCreate");
        ListAdapter adapter = new CustomArrayAdapter(this, demos);

        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        GameDetails demo = (GameDetails) getListAdapter().getItem(position);
        Intent intent = new Intent(this, DroidzActivity.class);
        intent.putExtra("game", demo.game.toString());
        startActivity(intent);
    }
}

