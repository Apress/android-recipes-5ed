package com.androidrecipes.sharedocuments;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Point;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract.Document;
import android.provider.DocumentsContract.Root;
import android.provider.DocumentsProvider;
import android.util.ArrayMap;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class ImageProvider extends DocumentsProvider {
    private static final String TAG = "ImageProvider";

    /* Cached recent selection */
    private static String sLastDocumentId;

    private static final String DOCID_ROOT = "root:";
    private static final String DOCID_ICONS_DIR = DOCID_ROOT + "icons:";
    private static final String DOCID_BGS_DIR = DOCID_ROOT + "backgrounds:";

    /* Default projection for a root when none supplied */
    private static final String[] DEFAULT_ROOT_PROJECTION = {
            Root.COLUMN_ROOT_ID, Root.COLUMN_MIME_TYPES,
            Root.COLUMN_FLAGS, Root.COLUMN_ICON, Root.COLUMN_TITLE,
            Root.COLUMN_SUMMARY, Root.COLUMN_DOCUMENT_ID,
            Root.COLUMN_AVAILABLE_BYTES
    };
    /* Default projection for documents when none supplied */
    private static final String[] DEFAULT_DOCUMENT_PROJECTION = {
            Document.COLUMN_DOCUMENT_ID, Document.COLUMN_MIME_TYPE,
            Document.COLUMN_DISPLAY_NAME, Document.COLUMN_LAST_MODIFIED,
            Document.COLUMN_FLAGS, Document.COLUMN_SIZE
    };

    private ArrayMap<String, String> mIcons;
    private ArrayMap<String, String> mBackgrounds;

    @Override
    public boolean onCreate() {
        //Dummy data for our documents
        mIcons = new ArrayMap<String, String>();
        mIcons.put("logo1.png", "John Doe");
        mIcons.put("logo2.png", "Jane Doe");
        mIcons.put("logo3.png", "Jill Doe");
        mBackgrounds = new ArrayMap<String, String>();
        mBackgrounds.put("background.jpg", "Wavy Grass");

        //Dump asset images onto internal storage
        writeAssets(mIcons.keySet());
        writeAssets(mBackgrounds.keySet());
        return true;
    }

    /*
     * Helper method to stream some dummy files out to the
     * internal storage directory
     */
    private void writeAssets(Set<String> filenames) {
        for (String name : filenames) {
            try {
                Log.d("ImageProvider", "Writing " + name + " to storage");
                InputStream in = getContext().getAssets().open(name);
                FileOutputStream out = getContext().openFileOutput(name, Context.MODE_PRIVATE);

                int size;
                byte[] buffer = new byte[1024];
                while ((size = in.read(buffer, 0, 1024)) >= 0) {
                    out.write(buffer, 0, size);
                }
                out.flush();
                out.close();
            } catch (IOException e) {
                Log.w(TAG, e);
            }
        }
    }

    /* Helper methods to construct documentId from a file name */
    private String getIconsDocumentId(String filename) {
        return DOCID_ICONS_DIR + filename;
    }

    private String getBackgroundsDocumentId(String filename) {
        return DOCID_BGS_DIR + filename;
    }

    /* Helper methods to determine document types */
    private boolean isRoot(String documentId) {
        return DOCID_ROOT.equals(documentId);
    }

    private boolean isIconsDir(String documentId) {
        return DOCID_ICONS_DIR.equals(documentId);
    }

    private boolean isBackgroundsDir(String documentId) {
        return DOCID_BGS_DIR.equals(documentId);
    }

    private boolean isIconDocument(String documentId) {
        return documentId.startsWith(DOCID_ICONS_DIR);
    }

    private boolean isBackgroundsDocument(String documentId) {
        return documentId.startsWith(DOCID_BGS_DIR);
    }

    /*
     * Helper method to extract file name from a documentId.
     * Returns empty string for the "root" document.
     */
    private String getFilename(String documentId) {
        int split = documentId.lastIndexOf(":");
        if (split < 0) {
            return "";
        }
        return documentId.substring(split + 1);
    }

    /*
     * Called by the system to determine how many "providers" are
     * hosted here.  It is most common to only return one, via a
     * Cursor that has only one result row.
     */
    @Override
    public Cursor queryRoots(String[] projection) throws FileNotFoundException {
        if (projection == null) {
            projection = DEFAULT_ROOT_PROJECTION;
        }
        MatrixCursor result = new MatrixCursor(projection);
        //Add the single root for this provider
        MatrixCursor.RowBuilder builder = result.newRow();

        builder.add(Root.COLUMN_ROOT_ID, "root");
        builder.add(Root.COLUMN_TITLE, "Android Recipes");
        builder.add(Root.COLUMN_SUMMARY, "Android Recipes Documents Provider");
        builder.add(Root.COLUMN_ICON, R.drawable.ic_launcher);

        builder.add(Root.COLUMN_DOCUMENT_ID, DOCID_ROOT);

        builder.add(Root.COLUMN_FLAGS,
                //Results will only come from the local file system
                Root.FLAG_LOCAL_ONLY
                        //We support showing recently selected items
                        | Root.FLAG_SUPPORTS_RECENTS
                        //We support doc tree selection (API 21+)
                        | Root.FLAG_SUPPORTS_IS_CHILD);
        builder.add(Root.COLUMN_MIME_TYPES, "image/*");
        builder.add(Root.COLUMN_AVAILABLE_BYTES, 0);

        return result;
    }

    /*
     * Called by the system to determine the child items for a given
     * parent.  Will be called for the root, and for each subdirectory
     * defined within.
     */
    @Override
    public Cursor queryChildDocuments(String parentDocumentId, String[] projection, String sortOrder)
            throws FileNotFoundException {
        if (projection == null) {
            projection = DEFAULT_DOCUMENT_PROJECTION;
        }
        MatrixCursor result = new MatrixCursor(projection);

        if (isIconsDir(parentDocumentId)) {
            //Add all files in the icons collection
            try {
                for (String key : mIcons.keySet()) {
                    addImageRow(result, mIcons.get(key), getIconsDocumentId(key));
                }
            } catch (IOException e) {
                return null;
            }
        } else if (isBackgroundsDir(parentDocumentId)) {
            //Add all files in the backgrounds collection
            try {
                for (String key : mBackgrounds.keySet()) {
                    addImageRow(result, mBackgrounds.get(key), getBackgroundsDocumentId(key));
                }
            } catch (IOException e) {
                return null;
            }
        } else if (isRoot(parentDocumentId)) {
            //Add the top-level directories
            addIconsRow(result);
            addBackgroundsRow(result);
        }

        return result;
    }

    /*
     * Return the same information provided via queryChildDocuments(), but
     * just for the single documentId requested.
     */
    @Override
    public Cursor queryDocument(String documentId, String[] projection)
            throws FileNotFoundException {
        if (projection == null) {
            projection = DEFAULT_DOCUMENT_PROJECTION;
        }

        MatrixCursor result = new MatrixCursor(projection);

        try {
            String filename = getFilename(documentId);
            if (isRoot(documentId)) { //This is a query for root
                addRootRow(result);
            } else if (isIconsDir(documentId)) { //This is a query for icons
                addIconsRow(result);
            } else if (isBackgroundsDir(documentId)) { //This is a query for backgrounds
                addBackgroundsRow(result);
            } else if (isIconDocument(documentId)) {
                addImageRow(result, mIcons.get(filename), getIconsDocumentId(filename));
            } else if (isBackgroundsDocument(documentId)) {
                addImageRow(result, mBackgrounds.get(filename), getBackgroundsDocumentId(filename));
            }
        } catch (IOException e) {
            return null;
        }

        return result;
    }

    /*
     * Called to populate any recently used items from this
     * provider in the Recents picker UI.
     */
    @Override
    public Cursor queryRecentDocuments(String rootId, String[] projection)
            throws FileNotFoundException {
        if (projection == null) {
            projection = DEFAULT_DOCUMENT_PROJECTION;
        }

        MatrixCursor result = new MatrixCursor(projection);

        if (sLastDocumentId != null) {
            String filename = getFilename(sLastDocumentId);
            String recentTitle = "";
            if (isIconDocument(sLastDocumentId)) {
                recentTitle = mIcons.get(filename);
            } else if (isBackgroundsDocument(sLastDocumentId)) {
                recentTitle = mBackgrounds.get(filename);
            }

            try {
                addImageRow(result, recentTitle, sLastDocumentId);
            } catch (IOException e) {
                Log.w(TAG, e);
            }
        }
        Log.d(TAG, "Recents: " + result.getCount());
        //We'll return the last selected result to a recents query
        return result;
    }

    /*
     * This is method is required ONLY if you want to support document
     * tree selection (API 21+). Return whether a given document is and
     * parent are related
     */
    @Override
    public boolean isChildDocument(String parentDocumentId, String documentId) {
        if (isRoot(parentDocumentId)) {
            //The subdirectories are children of root
            return isIconsDir(documentId)
                    || isBackgroundsDir(documentId);
        }

        if (isIconsDir(parentDocumentId)) {
            //All icons are children of the icons directory
            return isIconDocument(documentId);
        }

        if (isBackgroundsDir(parentDocumentId)) {
            //All backgrounds are children of the backgrounds directory
            return isBackgroundsDocument(documentId);
        }

        //Otherwise, these ids don't know each other
        return false;
    }

    /*
     * Helper method to write the root into the supplied
     * Cursor
     */
    private void addRootRow(MatrixCursor cursor) {
        addDirRow(cursor, DOCID_ROOT, "Root");
    }

    private void addIconsRow(MatrixCursor cursor) {
        addDirRow(cursor, DOCID_ICONS_DIR, "Icons");
    }

    private void addBackgroundsRow(MatrixCursor cursor) {
        addDirRow(cursor, DOCID_BGS_DIR, "Backgrounds");
    }

    /*
     * Helper method to write a specific subdirectory into
     * the supplied Cursor
     */
    private void addDirRow(MatrixCursor cursor, String documentId, String name) {
        final MatrixCursor.RowBuilder row = cursor.newRow();

        row.add(Document.COLUMN_DOCUMENT_ID, documentId);
        row.add(Document.COLUMN_DISPLAY_NAME, name);
        row.add(Document.COLUMN_SIZE, 0);
        row.add(Document.COLUMN_MIME_TYPE, Document.MIME_TYPE_DIR);

        long installed;
        try {
            installed = getContext().getPackageManager()
                    .getPackageInfo(getContext().getPackageName(), 0)
                    .firstInstallTime;
        } catch (NameNotFoundException e) {
            installed = 0;
        }
        row.add(Document.COLUMN_LAST_MODIFIED, installed);
        row.add(Document.COLUMN_FLAGS, 0);
    }

    /*
     * Helper method to write a specific image file into
     * the supplied Cursor
     */
    private void addImageRow(MatrixCursor cursor, String title, String documentId) throws IOException {
        final MatrixCursor.RowBuilder row = cursor.newRow();

        String filename = getFilename(documentId);
        AssetFileDescriptor afd = getContext().getAssets().openFd(filename);

        row.add(Document.COLUMN_DOCUMENT_ID, documentId);
        row.add(Document.COLUMN_DISPLAY_NAME, title);
        row.add(Document.COLUMN_SIZE, afd.getLength());
        row.add(Document.COLUMN_MIME_TYPE, "image/*");

        long installed;
        try {
            installed = getContext().getPackageManager()
                    .getPackageInfo(getContext().getPackageName(), 0)
                    .firstInstallTime;
        } catch (NameNotFoundException e) {
            installed = 0;
        }
        row.add(Document.COLUMN_LAST_MODIFIED, installed);
        row.add(Document.COLUMN_FLAGS, Document.FLAG_SUPPORTS_THUMBNAIL);
    }

    /*
     * Return a reference to an image asset the framework will use
     * in the items list for any document with the FLAG_SUPPORTS_THUMBNAIL
     * flag enabled.  This method is safe to block while downloading content.
     */
    @Override
    public AssetFileDescriptor openDocumentThumbnail(String documentId, Point sizeHint, CancellationSignal signal)
            throws FileNotFoundException {
        //We will load the thumbnail from the version on storage
        String filename = getFilename(documentId);
        //Create a file reference to the image on internal storage
        final File file = new File(getContext().getFilesDir(), filename);
        //Return a file descriptor wrapping the file reference
        final ParcelFileDescriptor pfd =
                ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        return new AssetFileDescriptor(pfd, 0, AssetFileDescriptor.UNKNOWN_LENGTH);
    }

    /*
     * Return a file descriptor to the document referenced by the supplied
     * documentId.  The client will use this descriptor to read the contents
     * directly.  This method is safe to block while downloading content.
     */
    @Override
    public ParcelFileDescriptor openDocument(String documentId, String mode, CancellationSignal signal)
            throws FileNotFoundException {
        //We will load the document itself from assets
        try {
            String filename = getFilename(documentId);
            //Create a file reference to the image on internal storage
            final File file = new File(getContext().getFilesDir(), filename);
            //Return a file descriptor wrapping the file reference
            final ParcelFileDescriptor pfd =
                    ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);

            //Save this as the last selected document
            sLastDocumentId = documentId;

            return pfd;
        } catch (IOException e) {
            Log.w(TAG, e);
            return null;
        }
    }
}
