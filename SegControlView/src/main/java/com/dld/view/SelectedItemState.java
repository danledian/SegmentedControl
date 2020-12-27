package com.dld.view;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

public class SelectedItemState extends View.BaseSavedState {

    private int selectedItem;

    public int getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<SelectedItemState>(){

        @Override
        public SelectedItemState createFromParcel(Parcel source) {
            return new SelectedItemState(source);
        }

        @Override
        public SelectedItemState[] newArray(int size) {
            return new SelectedItemState[size];
        }
    };

    SelectedItemState(Parcel superState) {
        super(superState);
        selectedItem = superState.readInt();
    }

    SelectedItemState(Parcelable source) {
        super(source);
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(selectedItem);
    }

}
