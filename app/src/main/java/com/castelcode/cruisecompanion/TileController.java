package com.castelcode.cruisecompanion;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TileController extends LinearLayout {

    private TextView mLabel;
    private ImageView mImage;
    private int mId;

    public TileController(Context context) {
        super(context);
        inflate(context);
    }

    public void setOnClickListener(OnClickListener listener) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.tile);
        layout.setOnClickListener(listener);
    }

    public TileController(Context context, String label, Drawable image, int id) {
        super(context);
        inflate(context);
        setLabel(label);
        setImage(image);
        setId(id);
    }

    private void inflate(Context context) {
        LayoutInflater inflater =
                (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.tile, this, true);

        mLabel = (TextView) findViewById(R.id.tile_label);
        mImage = (ImageView) findViewById(R.id.tile_image);
    }

    public void setLabel(String label) {
        mLabel.setText(label);
    }

    public void setImage(Drawable image) {
        mImage.setImageDrawable(image);
    }

    public void setId(int id) {
        mId = id;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(obj instanceof TileController) {
            final TileController other = (TileController) obj;
            return this.mImage.equals(other.mImage) && this.mLabel.equals(other.mLabel);
        }
        return false;
    }

    public int getItemId() {
        return mId;
    }
}
