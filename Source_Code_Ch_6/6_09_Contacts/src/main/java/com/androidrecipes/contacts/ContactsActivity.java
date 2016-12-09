package com.androidrecipes.contacts;

import android.app.AlertDialog;
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

public class ContactsActivity extends FragmentActivity {

    private static final int ROOT_ID = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout rootView = new FrameLayout(this);
        rootView.setId(ROOT_ID);

        setContentView(rootView);

        //Create and add a new list fragment
        getSupportFragmentManager().beginTransaction()
                .add(ROOT_ID, ContactsFragment.newInstance())
                .commit();
    }

    public static class ContactsFragment extends ListFragment
            implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

        public static ContactsFragment newInstance() {
            return new ContactsFragment();
        }

        private SimpleCursorAdapter mAdapter;

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
            String[] projection = new String[]{
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME
            };

            return new CursorLoader(getActivity(),
                    ContactsContract.Contacts.CONTENT_URI,
                    projection, null, null,
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
        public void onItemClick(AdapterView<?> parent, View v,
                                int position, long id) {
            final Cursor contacts = mAdapter.getCursor();
            if (contacts.moveToPosition(position)) {
                int selectedId = contacts.getInt(0); // _ID column
                // Gather email data from email table
                Cursor email = getActivity().getContentResolver()
                        .query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                new String[]{ContactsContract.CommonDataKinds.Email.DATA},
                                ContactsContract.Data.CONTACT_ID
                                        + " = " + selectedId,
                                null, null);
                // Gather phone data from phone table
                Cursor phone = getActivity().getContentResolver()
                        .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                                ContactsContract.Data.CONTACT_ID
                                        + " = " + selectedId,
                                null, null);
                // Gather addresses from address table
                Cursor address = getActivity().getContentResolver()
                        .query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                                new String[]{ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS},
                                ContactsContract.Data.CONTACT_ID
                                        + " = " + selectedId,
                                null, null);

                // Build the dialog message
                StringBuilder sb = new StringBuilder();
                sb.append(email.getCount() + " Emails\n");
                if (email.moveToFirst()) {
                    do {
                        sb.append("Email: " + email.getString(0));
                        sb.append('\n');
                    } while (email.moveToNext());
                    sb.append('\n');
                }
                sb.append(phone.getCount() + " Phone Numbers\n");
                if (phone.moveToFirst()) {
                    do {
                        sb.append("Phone: " + phone.getString(0));
                        sb.append('\n');
                    } while (phone.moveToNext());
                    sb.append('\n');
                }
                sb.append(address.getCount() + " Addresses\n");
                if (address.moveToFirst()) {
                    do {
                        sb.append("Address:\n"
                                + address.getString(0));
                    } while (address.moveToNext());
                    sb.append('\n');
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(contacts.getString(1)); // Display name
                builder.setMessage(sb.toString());
                builder.setPositiveButton("OK", null);
                builder.create().show();

                // Finish temporary cursors
                email.close();
                phone.close();
                address.close();
            }
        }
    }
}
