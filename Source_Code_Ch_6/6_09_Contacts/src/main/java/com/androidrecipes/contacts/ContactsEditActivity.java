package com.androidrecipes.contacts;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;


public class ContactsEditActivity extends FragmentActivity {

    private static final String TEST_EMAIL = "tester@email.com";
    private static final int ROOT_ID = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout rootView = new FrameLayout(this);
        rootView.setId(ROOT_ID);

        setContentView(rootView);

        //Create and add a new list fragment
        getSupportFragmentManager().beginTransaction()
                .add(ROOT_ID, ContactsEditFragment.newInstance())
                .commit();
    }

    public static class ContactsEditFragment extends ListFragment implements
            AdapterView.OnItemClickListener,
            DialogInterface.OnClickListener,
            LoaderManager.LoaderCallbacks<Cursor> {

        public static ContactsEditFragment newInstance() {
            return new ContactsEditFragment();
        }

        private SimpleCursorAdapter mAdapter;
        private Cursor mEmail;
        private int selectedContactId;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            // Display all contacts in a ListView
            mAdapter = new SimpleCursorAdapter(getActivity(),
                    android.R.layout.simple_list_item_1, null,
                    new String[]{ContactsContract.Contacts.DISPLAY_NAME},
                    new int[]{android.R.id.text1},
                    0);
            setListAdapter(mAdapter);
            // Listen for item selections
            getListView().setOnItemClickListener(this);

            getLoaderManager().initLoader(0, null, this);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            // Return all contacts, ordered by name
            String[] projection = new String[]{ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME};
            //List only contacts visible to the user
            return new CursorLoader(getActivity(),
                    ContactsContract.Contacts.CONTENT_URI,
                    projection, ContactsContract.Contacts.IN_VISIBLE_GROUP + " = 1",
                    null,
                    ContactsContract.Contacts.DISPLAY_NAME);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mAdapter.swapCursor(null);
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            final Cursor contacts = mAdapter.getCursor();
            if (contacts.moveToPosition(position)) {
                selectedContactId = contacts.getInt(0); // _ID column
                // Gather email data from email table
                String[] projection = new String[]{
                        ContactsContract.Data._ID,
                        ContactsContract.CommonDataKinds.Email.DATA};
                mEmail = getActivity().getContentResolver().query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        projection,
                        ContactsContract.Data.CONTACT_ID + " = " + selectedContactId,
                        null,
                        null);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Email Addresses");
                builder.setCursor(mEmail, this, ContactsContract.CommonDataKinds.Email.DATA);
                builder.setPositiveButton("Add", this);
                builder.setNegativeButton("Cancel", null);
                builder.create().show();
            }
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            //Data must be associated with a RAW contact, retrieve the first raw ID
            Cursor raw = getActivity().getContentResolver().query(
                    ContactsContract.RawContacts.CONTENT_URI,
                    new String[]{ContactsContract.Contacts._ID},
                    ContactsContract.Data.CONTACT_ID + " = " + selectedContactId, null, null);
            if (!raw.moveToFirst()) {
                return;
            }

            int rawContactId = raw.getInt(0);
            ContentValues values = new ContentValues();
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    //User wants to add a new email
                    values.put(ContactsContract.CommonDataKinds.Email.RAW_CONTACT_ID, rawContactId);
                    values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
                    values.put(ContactsContract.CommonDataKinds.Email.DATA, TEST_EMAIL);
                    values.put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_OTHER);
                    getActivity().getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
                    break;
                default:
                    //User wants to edit selection
                    values.put(ContactsContract.CommonDataKinds.Email.DATA, TEST_EMAIL);
                    values.put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_OTHER);
                    getActivity().getContentResolver().update(ContactsContract.Data.CONTENT_URI, values,
                            ContactsContract.Data._ID + " = " + mEmail.getInt(0), null);
                    break;
            }

            //Don't need the email cursor anymore
            mEmail.close();
        }
    }
}