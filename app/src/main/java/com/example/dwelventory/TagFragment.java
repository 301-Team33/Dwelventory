package com.example.dwelventory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/***
 * A fragment class utilized for adding and deleting user defined tags alongside applying such user
 * defined tags for an Item. This fragment works with a firestore database to store all specified
 * user defined tags for a User
 * @author
 *      Ethan Keys CMPUT 301 FALL 2023 LECTURE A1
 * @see
 *      AddEditActivity MainActivity
 *
 */
public class TagFragment extends DialogFragment{
    // Get the needed firebase stuff.
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference tagsRef;

    private String userId;

    private OnFragmentInteractionListener listener;
    private MaterialButton tagApplyButton;
//    private Button deleteCancelButton;
    private MaterialButton deleteCancelButton;
//    private Button deleteConfirmButton;
    private MaterialButton deleteConfirmButton;
    private TextView deletePrompt;
    private LinearLayout deleteLayout;
    private ImageButton tagBackButton;
    private ImageButton tagCreateButton;
    private EditText tagEditText;
    private ListView tagListView;
    private ArrayAdapter<Tag> tagArrayAdapter;
    private ArrayList<Tag> tagDataList;
    private ArrayList<Tag> tagsToApply;
    private ArrayList<Tag> tagsCurrentApply;
    private HashMap<String,String> currentTagNames = new HashMap<String,String>();
    private int itemIndex;


    /***
     * Creates a listener interface for the Fragment. Collaborators with this interface include,
     * TagFragment, AddEditActivity, MainActivity
     * @author
     *      Ethan Keys Fall 2023 CMPUT 301 LECTURE A1
     * @see
     *      TagFragment as it is the listener the fragment class.
     *
     */
    public interface OnFragmentInteractionListener{
        void onCloseAction();
        void onTagApplyAction(ArrayList<Tag> applyTags);
        void onTagDeletion(Tag deletedTag); // Should only have an implementation in the Main Activity
        // Only able to delete tags in the main activity.
    }

    /***
     * Overriden onAttach method for attaching the context to the fragment. Creates a listener to be
     * Used by other activities using the interface.
     * @param context
     *      The Context to create a listener for
     * @throws
     *      RuntimeException if listener was not implemented.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener){
            listener = (OnFragmentInteractionListener) context;
        }else{
            throw new RuntimeException();
        }
    }

    /***
     * Create a new dialog fragment to readily make the UI for the applying and adding / deleting tags
     * to items!
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return
     *      AlertDialog.Builder for the built new TagFragment
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.tag_edit_fragment_layout,null);
        tagBackButton = view.findViewById(R.id.tag_back_button);
        tagCreateButton = view.findViewById(R.id.tag_create_button);
        tagApplyButton = view.findViewById(R.id.tag_apply_button);

        deleteLayout = view.findViewById(R.id.tag_deletion_views);
        deleteCancelButton = view.findViewById(R.id.no_action_button);
        deleteConfirmButton = view.findViewById(R.id.delete_button);
        deletePrompt = view.findViewById(R.id.delete_prompt_text);

        deleteLayout.setVisibility(View.GONE);
        deletePrompt.setVisibility(View.GONE);


        Bundle bundle = getArguments();
        userId = bundle.getString("user_id"); // Get the UserID to load the appropriate
        tagsCurrentApply = new ArrayList<>(); // Current Tags associated with an Item if the item is being editted
        tagsToApply = new ArrayList<>();

        // check if the bundle contains an Item in it, if so, we are updating the Items Tags
        if (bundle.containsKey("current_item")){
            tagsCurrentApply = bundle.getParcelableArrayList("current_item"); // retrieve the item
            // check if the item is there but never had tags initialized.
            Log.d("", "onCreate: IN THE FRAGMENT!!" + tagsCurrentApply);
            if (tagsCurrentApply == null) {
                tagsCurrentApply = new ArrayList<>();
                Log.d("", "onCreateDialog: SAID WAS NULL" + tagsCurrentApply);
            }
        }
        // get the collection for the current user defined tag names
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        Log.d("database Tag", "/users/"+userId+"/tags");
        tagsRef = db.collection("users").document(userId).collection("tags");

        tagEditText = view.findViewById(R.id.tag_edittext);

        tagListView = view.findViewById(R.id.tag_listview);

        tagDataList = new ArrayList<>();

        // set the list view
        tagArrayAdapter = new TagCustomList(this.getContext(), tagDataList,tagsCurrentApply);
        tagListView.setAdapter(tagArrayAdapter);

        // begin creating the fragment utilizing the alert dialog builder.
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);


        // create the events for closing the fragment and not applying anything. (top left corner)
        tagBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCloseAction(); // close fragment.
            }
        });

        // Only for main activity. Tags can not be deleted from editing or creating a singular item
        // However tags can be deleted from the tag list fragment on the main activity.
        tagListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                itemIndex = position; // get the index of the item we want to delete
                view.setBackgroundColor(getResources().getColor(R.color.selected,null));
                deletePrompt.setText("Delete: " + tagDataList.get(position).getTagName() + "?" );
                // if the bundle contains the deletion mode, then we will allow the deletion UI
                // to be visible, allowing for possible deletion.
                if (bundle.containsKey("mode")) {
                    deleteLayout.setVisibility(View.VISIBLE);
                    deletePrompt.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        // Utilized for selecting a tag to be applied to the specified Item.
        tagListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(tagsToApply == null || tagsToApply.contains(tagDataList.get(position)) == false){
                    // This is the case where the tag was not already selected so we add it to the
                    // Potential selected tags.
                    tagApplyButton.setBackgroundColor(getResources().getColor(R.color.green, null));
                    view.setBackgroundColor(getResources().getColor(R.color.selected,null));
                    tagsToApply.add(tagDataList.get(position));
                }else{
                    // This is the case where the tag was already selected and now they want to
                    // deselect it, removing it from the set of tags that need to be applied!
                    view.setBackgroundColor(getResources().getColor(R.color.gray,null));
                    tagsToApply.remove(tagDataList.get(position));
                    if ( tagsToApply.isEmpty()  ){
                        tagApplyButton.setBackgroundColor(getResources().getColor(R.color.selected, null));
                    }
                }
            }
        });

        // Create a snapshot listener to retrieve all the documents from the tags collection to
        // fill it into the list view.
        tagsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Log.e("Firestore",error.toString());
                    return;
                }
                if (value != null){
                    tagDataList.clear();
                    for(QueryDocumentSnapshot doc: value){
                        String storedTagName = doc.getId();
                        Log.d("Firestore", String.format("Tag(%s) fetched", storedTagName));
                        currentTagNames.put(storedTagName.toLowerCase(),"1"); // add to the current tags name to allow for easy access to check for existance.
                        Tag storedTag = new Tag(storedTagName);
                        tagDataList.add(storedTag);
                        for (Tag appliedTag: tagsCurrentApply){
                            // loop through to see if the tag being added to the lsitview then we
                            // can add it to the currently applied tags.
                            if (storedTagName.equals(appliedTag.getTagName())){
                                tagsToApply.add(storedTag);
                                break;
                            }
                        }
                    }
                    tagArrayAdapter.notifyDataSetChanged(); // notify the adapter of the changed dataset.
                }
            }

        });

        // Used when deleting a Tag and the mind was changed. makes the tag not deleted and hides
        // The deletion UI
        deleteCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLayout.setVisibility(View.GONE);
                deletePrompt.setVisibility(View.GONE);
            }
        });
        // Used in the main activity for deleting a tag, if pressed the tag is deleted not only from
        // The user defined tags and from every other item in the list.
        deleteConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tag toDelete = tagDataList.get(itemIndex);
                deleteLayout.setVisibility(View.GONE);
                deletePrompt.setVisibility(View.GONE);
                currentTagNames.remove(toDelete.getTagName().toLowerCase());
                tagsRef.document(toDelete.getTagName()).delete();
                tagDataList.remove(itemIndex);
                tagArrayAdapter.notifyDataSetChanged();
                listener.onTagDeletion(toDelete); // call the deletion action in main activity to remove tag from every item
            }
        });


        // Will apply the tag name in the current edit text if not already defined and valid (non null / empty)
        tagCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if the editText is null or just a space.
                String tagName = tagEditText.getText().toString();
                if (tagName == null || tagName.trim().length() == 0){
                    produceTagToast(R.string.invalid_tag_toast);
                    tagEditText.setText(null);
                }
                // check if tag already exists in list with same name (case insensitive)
                else if (currentTagNames.containsKey(tagName.toLowerCase())) {
                    tagEditText.setText(null);
                    produceTagToast(R.string.tag_already_exists_toast);
                }
                else {
                    // else we define the tag to be created and add it to the database, and the list
                    // of current user defined tags.
                    currentTagNames.put(tagName.toLowerCase(), null);
                    tagDataList.add(new Tag(tagName));
                    tagEditText.setText(null);
                    tagsRef.document(tagName).set(new HashMap<Object,Object>());
                    tagArrayAdapter.notifyDataSetChanged();
                    produceTagToast(R.string.tag_create_toast);
                }
            }
        });

        // Used to apply all selected tags to the current item // items.
        tagApplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tagsToApply.size() == 0){
                    produceTagToast(R.string.no_tags_selected);
                }else {
                    listener.onTagApplyAction(tagsToApply);
                }
            }
        });




       return builder.create(); // return the created alert dialog fragment builder.
    }

    // Used if an item is being created for the first time.

    /***
     * Overloaded Method for creating a new instance of a TagFragment. This overloaded method version
     * allows for a New item to be created by sending the userID stored in the firebase to nab all
     * the user defined tags
     * @param userId
     *      A String taken from the database representing the User Id of the user.
     * @return
     *      TagFragment - the instance of the Tag Fragment to be shown in the UI
     */
    static TagFragment newInstance(String userId){
        // load in the user ID to get the query path for storing and retrieving current user defined
        // tags
        Bundle args = new Bundle();
        args.putString("user_id",userId);

        TagFragment tagFragment = new TagFragment();
        tagFragment.setArguments(args);
        return tagFragment;
    }
    // used for opening the tag fragment in the main activity and allowing for deletion

    /***
     * Overloaded method for creating a new instance of a TagFragment. This overloaded method version
     * Allows for the tags to be deleted in the MainActivity.
     * @param userID
     *       A String taken from the database representing the User Id of the user.
     * @param mode
     *      A String representing the mode of the TagFragment. If this is specified then the Tags have
     *      the ability to be deleted from the database and from all specified items.
     * @return
     *      TagFragment - the instance of the Tag Fragment to be shown in the UI
     */
    static TagFragment newInstance(String userID, String mode){
        Bundle args = new Bundle();
        args.putString("user_id",userID);
        // used for specifying that tag deletion from the database is possible!!
        args.putString("mode",mode);

        TagFragment tagFragment = new TagFragment();
        tagFragment.setArguments(args);

        return tagFragment;
    }
    // Used for editing an item for updating // applying tags to its already made existance!

    /***
     * Overloaded method for creating a new instance of a TagFragment. This overloaded method version
     * Allows for the editing of currently existing tags for an Item
     * @param userId
     *      A String taken from the database representing the User Id of the user.
     * @param item
     *      the specified item to edit the tags for (add or delete them)
     * @return
     *      TagFragment - the instance of the Tag Fragment to be shown in the UI
     */
    static TagFragment newInstance(String userId,ArrayList<Tag> item){
        Bundle args = new Bundle();
        args.putString("user_id",userId);
        // place the item to edit into the bundle to retrieve in the fragment.
        args.putParcelableArrayList("current_item",item);

        TagFragment tagFragment = new TagFragment();
        tagFragment.setArguments(args);

        return tagFragment;
    }


    /***
     * Produce a Tag Toast when adding, deleting, or applying Tags to an item. Also will produce toasts
     * depicting an error has occurred (such as invalid tags trying to be added)
     * @param stringResource
     *      The string resource ID stored in strings.xml to be shown in the toast produced.
     */
    private void produceTagToast(int stringResource){
        // create a toast with the specified string resource on the appropiate action.
        Toast toast = Toast.makeText(getActivity(),stringResource,Toast.LENGTH_SHORT);
        toast.show();
    }

}




