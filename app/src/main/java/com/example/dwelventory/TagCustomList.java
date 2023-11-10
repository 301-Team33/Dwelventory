package com.example.dwelventory;

import static androidx.core.content.res.ResourcesCompat.getColor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

/***
 * A Custom ListView adapter to create a List view of Tags in the TagFragment.
 * Tags that are already associated to the current viewing Item will have their background set to a
 * dark gray color to represent that it is currently selected, otherwise it will be set to a light
 * gray to depict the contrary.
 * @Author
 *      Ethan Keys
 * @see
 *      TagFragment,Tag
 */
public class TagCustomList extends ArrayAdapter<Tag> {

    private ArrayList<Tag> tags;
    private ArrayList<Tag> currentlyApplied; // used for items that already have specified tags// were created already.
    private Context context;

    /***
     * Constructor for the current adapter instance
     * @param context
     *      The context for the activity.
     * @param tags
     *      The set of all user defined tags stored in an ArrayList of Tags
     * @param currentlyApplied
     *      The set of currently applied Tags for an Item (if applicable) associated in an ArrayList
     *      of Tags. This is used to help depict the tags in the view to be either selected or
     *      deselected
     */
    public TagCustomList(Context context, ArrayList<Tag> tags,ArrayList<Tag> currentlyApplied){
        super(context,0,tags);
        this.tags = tags;
        this.context = context;
        this.currentlyApplied = currentlyApplied;
    }

    /***
     * The overriden getView method for the ArrayAdapter. The changes in this method include the
     * specified selection and deselection of current Tags in the Listview by checking if they are both in
     * the set of all user defined Tags (tags) and currently defined Tags (currentTags)
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return
     *      The created view (View)
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Log.d("", "getView: the tagdatalist" + tags);
        Log.d("", "getView: currently applied" + currentlyApplied);
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.tag_content,parent,false);
        }

        Tag tag = tags.get(position);
        TextView tagName = view.findViewById(R.id.tag_text);
        tagName.setText(tag.getTagName());
        if (currentlyApplied != null) {
            for (Tag appliedTagName : currentlyApplied) {
                if (appliedTagName.getTagName().equals(tag.getTagName())) {
                    // This is the case where the item already has tags specified to it that are
                    // in the database, we set it's background to the selected option.
                    view.setBackgroundColor(ContextCompat.getColor(context,R.color.selected));
                    Log.d("", "getView: SET VIEW TO SELECTED ");
                    return view;
                } else {
                    // else the item doesnt have this tag so it is not implied to be selected through the UI.
                    view.setBackgroundColor(ContextCompat.getColor(context,R.color.gray));
                    Log.d("", "getView: SET VIEW TO UNSELECTED ");
                }
            }
        }
        return view;
    }
}
