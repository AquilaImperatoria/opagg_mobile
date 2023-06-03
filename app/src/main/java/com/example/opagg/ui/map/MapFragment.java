package com.example.opagg.ui.map;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.opagg.Place;
import com.example.opagg.R;
import com.example.opagg.RetrofitManager;
import com.example.opagg.databinding.FragmentMapBinding;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.GeoObjectCollection;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.VisibleRegionUtils;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;
import com.yandex.mapkit.search.SearchOptions;
import com.yandex.mapkit.search.Session;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment implements UserLocationObjectListener, Session.SearchListener {

    private EditText searchEdit;

    private Point cameraPosPoint;
    private SearchManager searchManager;
    private Session searchSession;
    private CameraPosition cameraPosition;
    private FragmentMapBinding binding;

    private RetrofitManager retrofitManager;

    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 1;

    private MapView mapview;

    private boolean camPosSet = false;

    private UserLocationLayer userLocationLayer;

    private MapViewModel mapViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SearchFactory.initialize(getContext());
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);

        mapViewModel =
                new ViewModelProvider(getActivity()).get(MapViewModel.class);

        retrofitManager = RetrofitManager.getInstance();

        binding = FragmentMapBinding.inflate(inflater, container, false);

//        View v = inflater.inflate(R.layout.fragment_Map, null);
//        MapView mapview = (MapView) v.findViewById(R.id.mapview);
        View root = binding.getRoot();
        searchEdit = (EditText) binding.search;
//        mapview.getMap().= this.getActivity().findViewById(R.id.mapview);

        mapview = (MapView) binding.mapview;


        mapview.getMap().set2DMode(true);
        requestLocationPermission();

        MapKit mapKit = MapKitFactory.getInstance();
        mapKit.resetLocationManagerToDefault();
        userLocationLayer = mapKit.createUserLocationLayer(mapview.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);
        userLocationLayer.setObjectListener(this);

        mapViewModel.getPoint().observe(getViewLifecycleOwner(), new Observer<Point>() {
            @Override
            public void onChanged(Point point) {
                cameraPosPoint = point;
                Log.w("point", String.valueOf(cameraPosPoint.getLongitude()));
            }
        });

        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    submitQuery(searchEdit.getText().toString());
                }

                return false;
            }
        });
// рисуем точку:

        Call<List<Place>> call = retrofitManager.getPointsService().getPlaces();

        call.enqueue(new Callback<List<Place>>() {
            @Override
            public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                List<Place> results = response.body();
                for (int i = 0; i < results.size(); i++) {
                    Place cur = results.get(i);
                      MapFragment.this.createTappableCircle(cur);
//                    mapview.getMap().getMapObjects().addPlacemark(mappoint);
                }
            }

            @Override
            public void onFailure(Call<List<Place>> call, Throwable t) {

                Log.d("TAG", "onFailure: ", t);

            }
        });



        return root;
    }

    private void submitQuery(String query) {
        searchSession = searchManager.submit(
                query,
                VisibleRegionUtils.toPolygon(mapview.getMap().getVisibleRegion()),
                new SearchOptions(),
                this);
    }

    private void createTappableCircle(Place cur) {
        String[] pos = cur.getPoint().split(" ");
        Point mappoint = new Point(Double.parseDouble(pos[1]), Double.parseDouble(pos[0]));
        PlacemarkMapObject circle = mapview.getMap().getMapObjects().addPlacemark(
                mappoint,
                ImageProvider.fromResource(getContext(), R.drawable.search_result_red));;
        circle.setUserData(cur);
//        circle.setZIndex(1000.0f);
        // Client code must retain strong reference to the listener.
        circle.addTapListener(circleMapObjectTapListener);
    }

    private MapObjectTapListener circleMapObjectTapListener = new MapObjectTapListener() {
        @Override
        public boolean onMapObjectTap(MapObject mapObject, Point point) {
            // inflate the layout of the popup window
            LayoutInflater inflater = (LayoutInflater)
                    getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.popup, null);

            // create the popup window
            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            boolean focusable = true; // lets taps outside the popup also dismiss it
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

            // show the popup window
            // which view you pass in doesn't matter, it is only used for the window token
            popupWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);

            Place cur = (Place) mapObject.getUserData();

            TextView nameView = (TextView) popupView.findViewById(R.id.name);
            nameView.setText(cur.getName());
            TextView adressView = (TextView) popupView.findViewById(R.id.adress);
            adressView.setText(cur.getAdress());
            TextView rateView = (TextView) popupView.findViewById(R.id.rating);
            rateView.setText(cur.getRating());
            TextView typeView = (TextView) popupView.findViewById(R.id.type);
            typeView.setText(cur.getPriceandtype());
            TextView tagsView = (TextView) popupView.findViewById(R.id.tags);
            tagsView.setText(cur.getTags());
            TextView contactsView = (TextView) popupView.findViewById(R.id.contact_info);
            contactsView.setText(cur.getContacts());

            // dismiss the popup window when touched
            popupView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popupWindow.dismiss();
                    return true;
                }
            });
            return true;
        }
    };

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                "android.permission.ACCESS_FINE_LOCATION")
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{"android.permission.ACCESS_FINE_LOCATION"},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStop() {
        mapview.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapview.onStart();
    }

    @Override
    public void onObjectAdded(@NonNull UserLocationView userLocationView) {

    }

    @Override
    public void onObjectRemoved(@NonNull UserLocationView userLocationView) {

    }

    @Override
    public void onObjectUpdated(@NonNull UserLocationView userLocationView, @NonNull ObjectEvent objectEvent) {

        if (cameraPosPoint != null)
        {
            camPosSet = true;
            CameraPosition finalPosition = new CameraPosition(cameraPosPoint, 18, 0, 0);
            mapview.getMap().move(
                    finalPosition,
                    new Animation(Animation.Type.SMOOTH, 1),
                    null);
        }
        else {
            cameraPosition = userLocationLayer.cameraPosition();
            if (cameraPosition != null && !camPosSet) {
                camPosSet = true;
                CameraPosition finalPosition = new CameraPosition(cameraPosition.getTarget(), 18, cameraPosition.getAzimuth(), cameraPosition.getTilt());
                mapViewModel.setPoint(cameraPosition.getTarget());
                mapview.getMap().move(
                        finalPosition,
                        new Animation(Animation.Type.SMOOTH, 1),
                        null);
            }
        }

    }

    @Override
    public void onSearchResponse(com.yandex.mapkit.search.Response response) {
        MapObjectCollection mapObjects = mapview.getMap().getMapObjects();


        for (GeoObjectCollection.Item searchResult : response.getCollection().getChildren()) {
            Point resultLocation = searchResult.getObj().getGeometry().get(0).getPoint();
            if (resultLocation != null) {
                PlacemarkMapObject newobj = mapObjects.addPlacemark(
                        resultLocation,
                        ImageProvider.fromResource(getContext(), R.drawable.search_result));
                newobj.setOpacity((float) 0.60);
            }
        }
    }

    @Override
    public void onSearchError(@NonNull Error error) {

    }

}