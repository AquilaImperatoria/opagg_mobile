package com.example.opagg.ui.map;


import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;


import com.yandex.mapkit.geometry.Point;


public class MapViewModel extends ViewModel {

    private final SavedStateHandle state;
    private static MutableLiveData<Point> pointContainer = null;

    public MapViewModel(SavedStateHandle state) {
        this.state = state;

        pointContainer = state.getLiveData("point");
    }

    @Nullable
    public static LiveData<Point> getPoint() {
        return pointContainer;
    }

    public void setPoint(Point point) {
        pointContainer.setValue(point);
    }
}